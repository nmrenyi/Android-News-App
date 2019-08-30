package com.example.newstoday.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArraySet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.FragmentTransitionSupport;

import com.example.newstoday.Adapter.CatAdapter;
import com.example.newstoday.News;
import com.example.newstoday.Adapter.NewsAdapter;
import com.example.newstoday.NewsManager;
import com.example.newstoday.R;
import com.example.newstoday.UserManager;
import com.example.newstoday.UserManagerOnServer;
import com.example.newstoday.AsyncServerNews;
import com.example.newstoday.WechatShareManager;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import com.mikepenz.materialdrawer.model.interfaces.IProfile;
//import com.romainpiel.titanic.library.Titanic;
//import com.romainpiel.titanic.library.TitanicTextView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import dmax.dialog.SpotsDialog;

import static com.example.newstoday.Activity.NewsItem.mAdapterNews;

public class Table extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce;

//    private RecyclerView recyclerViewNews;
//    private NewsAdapter mAdapterNews;
//    private CatAdapter mAdapterCat;
//
//    private ArrayList<News> news;
    private NewsManager newsManager;
//    private UserManager userManager;
//    private String currentCategory = "推荐";

    private AsyncServerNews asyncServerNews;
    // private UserManagerOnServer userManagerOnServer;
//    private Titanic titanic;
    private DonutProgress donutProgress;
    private Timer timer;
    private AlertDialog spotsDialog;


    public static Drawer drawer;
    public AccountHeader header;
    private ArraySet<String> account = new ArraySet<>();
    private int identifier = 3;
    private int position = 0;

    private static final int DISMISS_TIMEOUT = 500;

    private final int CAT_REARRANGE = 1;
    private final int LOGIN_REQUEST = 2;
    private final int PICK_IMAGE = 3;
    private final int LOGOUT_REQUEST = 4;

    private final int COLLECTION_IDENTIFIER = 1;
    private final int HISTORY_IDENTIFIER = 2;
    private final int CLEAR_IDENTIFIER = 3;
    private final int NIGHT_IDENTIFIER = 4;
    private final int UPLOAD_IDENTIFIER = 5;
    private final int DOWNLOAD_IDENTIFIER = 6;

    private final int LOGIN_IDENTIFIER = 1;
    private final int LOGOUT_IDENTIFIER = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        NewsItem newsItem = new NewsItem(this.getSupportFragmentManager());
        fragmentTransaction.add(R.id.table_fragment, newsItem);
        fragmentTransaction.commit();

        newsManager = NewsManager.getNewsManager(getApplicationContext());

        asyncServerNews = AsyncServerNews.getAsyncServerNews(getApplicationContext());
//        userManagerOnServer = UserManagerOnServer.getUserManagerOnServer();

        /**
         * Drawer
         */
        buildDrawer(savedInstanceState, this);

        /**
         * Wechat share
         */
        final WechatShareManager wsm = WechatShareManager.getInstance(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST){
            if(resultCode == RESULT_OK){
                String email = (String) data.getSerializableExtra("email");
                if(account.contains(email)){
                    Toast.makeText(getApplicationContext(), "账号已登陆", Toast.LENGTH_LONG).show();
                    return;
                }
                account.add(email);
                String name = (String) data.getSerializableExtra("name");
                header.addProfile(new ProfileDrawerItem().withName(name)
                        .withEmail(email).withIdentifier(identifier)
                        .withIcon(R.drawable.header), position);
                header.setActiveProfile(identifier, true);
                header.updateProfile(header.getProfiles().get(identifier - 1));
                ++identifier;
                ++position;
            }
        } else if(requestCode == PICK_IMAGE){
            try {
                if(data == null)
                    return;
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                header.getActiveProfile().withIcon(bitmap);
                header.updateProfile(header.getActiveProfile());
            } catch (FileNotFoundException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "更换头像失败", Toast.LENGTH_SHORT);
            }
        }
    }

    private void buildDrawer(final Bundle savedInstanceState, final Activity activity){
        BaseDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(COLLECTION_IDENTIFIER).withName("我的收藏")
                .withIcon(R.drawable.ic_star).withTextColor(Color.parseColor("#ababab"));
        BaseDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(HISTORY_IDENTIFIER).withName("浏览历史")
                .withIcon(R.drawable.ic_history).withTextColor(Color.parseColor("#ababab"));
        BaseDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(UPLOAD_IDENTIFIER).withName("上传用户数据")
                .withIcon(R.drawable.ic_cloud_upload).withTextColor(Color.parseColor("#ababab"));
        BaseDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(DOWNLOAD_IDENTIFIER).withName("下载用户数据")
                .withIcon(R.drawable.ic_cloud_download).withTextColor(Color.parseColor("#ababab"));
        BaseDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(CLEAR_IDENTIFIER).withName("清除历史")
                .withIcon(R.drawable.clear).withTextColor(Color.parseColor("#ababab"));
        SwitchDrawerItem switchDrawerItem = new SwitchDrawerItem().withIdentifier(NIGHT_IDENTIFIER).withName("夜间模式")
                .withIcon(R.drawable.night).withTextColor(Color.parseColor("#ababab")).withSelectable(false)
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        else
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

//                        onSaveInstanceState(savedInstanceState);
                        Intent intent = getIntent();
                        finish();
                        overridePendingTransition(R.xml.slide_no_move, R.xml.fade);
                        startActivity(intent);
//                        finish();
//                        recreate();
                    }
                });
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            switchDrawerItem.withChecked(true);
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        header = new AccountHeaderBuilder()
                .withActivity(this)
//                .addProfiles(
//                        new ProfileDrawerItem().withName("Weize Chen").withIdentifier(3)
//                        .withEmail("chenweize@mails.tsinghua.edu.cn").withIcon(R.drawable.chenweize),
//                        new ProfileDrawerItem().withName("Hao Peng").withIdentifier(4)
//                                .withEmail("h-peng17@mails.tsinghua.edu.cn").withIcon(R.drawable.penghao)
//                )
                .addProfiles(
                        new ProfileSettingDrawerItem().withName("Add Account")
                                .withIcon(R.drawable.plus).withIdentifier(LOGIN_IDENTIFIER),
                        new ProfileSettingDrawerItem().withName("Log Out")
                                .withIcon(R.drawable.ic_exit_to_app).withIdentifier(LOGOUT_IDENTIFIER))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile.getIdentifier() == LOGIN_IDENTIFIER){
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivityForResult(intent, LOGIN_REQUEST);
                        } else if (profile.getIdentifier() == LOGOUT_IDENTIFIER){
                            if(identifier == 3) {
                                Toast.makeText(getApplicationContext(), "当前没有用户登录", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            account.remove(header.getActiveProfile().getEmail().toString());
                            header.removeProfileByIdentifier(header.getActiveProfile().getIdentifier());
                            --identifier;
                            --position;
                            if(identifier != 3)
                                header.setActiveProfile(identifier - 1);
                            newsManager.deleteAllHistory();
                            newsManager.deleteAllCollection();
                        }
                        return false;
                    }
                })
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        if(header.getActiveProfile() == profile) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        } else {
                            header.setActiveProfile(profile.getIdentifier(), true);
                            newsManager.deleteAllCollection();
                            newsManager.deleteAllHistory();
                        }
                        return true;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withTextColor(Color.parseColor("#ababab"))
                .build();
        if(savedInstanceState != null){
            String[] email = savedInstanceState.getStringArray("email");
            String[] name = savedInstanceState.getStringArray("name");
            System.out.println(name[0]);
            for(int i = 0; i < email.length; ++i){
                header.addProfiles(new ProfileDrawerItem().withName(name[i]).withIdentifier(3+i)
                                        .withEmail(email[i]));
            }
        }
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .withSelectedItem(-1)
                .addDrawerItems(
                        item1,
                        item2,
                        new DividerDrawerItem(),
                        item3,
                        item4,
                        item5,
                        new DividerDrawerItem(),
                        switchDrawerItem
                )
                .withSliderBackgroundDrawableRes(R.drawable.drawer_bg)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem){
                        if(drawerItem.getIdentifier() == COLLECTION_IDENTIFIER) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            CollectionNews collectionNews = new CollectionNews(fragmentManager);
                            fragmentTransaction.replace(R.id.table_fragment, collectionNews);
                            if(fragmentManager.getBackStackEntryCount() == 0)
                                fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        } else if (drawerItem.getIdentifier() == HISTORY_IDENTIFIER) {
//                            Intent intent = new Intent(getApplicationContext(), HistoryNews.class);
//                            startActivity(intent);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            HistoryNews historyNews = new HistoryNews(fragmentManager);
                            fragmentTransaction.replace(R.id.table_fragment, historyNews);
                            if(fragmentManager.getBackStackEntryCount() == 0)
                                fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else if (drawerItem.getIdentifier() == CLEAR_IDENTIFIER) {
                            newsManager.deleteAllHistory();
                            mAdapterNews.notifyDataSetChanged();

                            spotsDialog = new SpotsDialog.Builder()
                                    .setContext(Table.this)
                                    .setCancelable(false)
                                    .setTheme(R.style.Clearing)
                                    .build();
                            spotsDialog.show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1500);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                    spotsDialog.dismiss();
//                                    Toast.makeText(getApplicationContext(), "历史记录已清除", Toast.LENGTH_LONG).show();
                                }
                            }).start();

                        } else if (drawerItem.getIdentifier() == UPLOAD_IDENTIFIER) {
                            asyncServerNews.asyncCollectionNewsToServer();
                            asyncServerNews.asyncHistoryNewsToServer();

                            spotsDialog = new SpotsDialog.Builder()
                                    .setContext(Table.this)
                                    .setCancelable(false)
                                    .setTheme(R.style.Uploading)
                                    .build();
                            spotsDialog.show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                    spotsDialog.dismiss();
                                }
                            }).start();

                        } else if (drawerItem.getIdentifier() == DOWNLOAD_IDENTIFIER) {
                            newsManager.deleteAllHistory();
                            asyncServerNews.asyncHistoryNewsFromServer();
                            newsManager.deleteAllCollection();
                            asyncServerNews.asyncCollectionNewsFromServer();
                            mAdapterNews.notifyDataSetChanged();

                            spotsDialog = new SpotsDialog.Builder()
                                    .setContext(Table.this)
                                    .setCancelable(false)
                                    .setTheme(R.style.Downloading)
                                    .build();
                            spotsDialog.show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                    spotsDialog.dismiss();
                                }
                            }).start();

                        }
                        return false;
                    }
                })
                .build();
    }

    @Override
    protected void onStop (){
        super.onStop();
        newsManager.resetRecommendation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String[] email = new String[header.getProfiles().size() - 2];
        String[] name = new String[header.getProfiles().size() - 2];
        int cnt = 0;
        for(IProfile profile : header.getProfiles()){
            if(profile.getIdentifier() < 3)
                continue;
            email[cnt] = profile.getEmail().toString();
            name[cnt] = profile.getName().toString();
            ++cnt;
        }
        outState.putStringArray("email", email);
        outState.putStringArray("name", name);
    }

//    @Override
//    protected void onNewIntent(Intent intent){
////        mAdapterNews.notifyDataSetChanged();
////        drawer.setSelectionAtPosition(-1);
//    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
        else {
//            newsManager.resetRecommendation();
            getSupportFragmentManager().popBackStack();
            drawer.setSelectionAtPosition(-1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsManager.deleteAllCollection();
        newsManager.deleteAllHistory();
    }
}
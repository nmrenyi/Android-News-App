package com.example.newstoday.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.newstoday.Adapter.CatAdapter;
import com.example.newstoday.News;
import com.example.newstoday.Adapter.NewsAdapter;
import com.example.newstoday.NewsManager;
import com.example.newstoday.R;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class Table extends AppCompatActivity {
    private RecyclerView recyclerViewNews;
    private NewsAdapter mAdapterNews;
    private RecyclerView.LayoutManager layoutManagerNews;
    private RecyclerView recyclerViewCat;
    private CatAdapter mAdapterCat;
    private RecyclerView.LayoutManager layoutManagerCat;
    private ArrayList<News> news;
    private NewsManager newsManager;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private static final int DISMISS_TIMEOUT = 2000;
    private String currentCategory = "娱乐";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_activity);

        BaseDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("我的收藏")
                .withIcon(R.mipmap.star).withTextColor(Color.parseColor("#ababab"));
        BaseDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("浏览历史")
                .withIcon(R.mipmap.history).withTextColor(Color.parseColor("#ababab"));
        SwitchDrawerItem switchDrawerItem = new SwitchDrawerItem().withIdentifier(3).withName("夜间模式")
                .withIcon(R.mipmap.night).withTextColor(Color.parseColor("#ababab"));
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName("Weize Chen")
                        .withEmail("wei10@mails.tsinghua.edu.cn").withIcon(R.mipmap.chenweize)
                )
                .addProfiles(
                        new ProfileDrawerItem().withName("Hao Peng")
                                .withEmail("h-peng17@mails.tsinghua.edu.cn").withIcon(R.mipmap.penghao)
                )
                .withTextColor(Color.parseColor("#ababab"))
                .build();
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .addDrawerItems(
                        item1,
                        item2,
                        new DividerDrawerItem(),
                        switchDrawerItem
                )
                .withSliderBackgroundDrawableRes(R.mipmap.drawer_bg)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem){
                        // do something with the clicked item :D
                        return false;
                    }
                })
                .build();

        ImageButton imgButton = findViewById(R.id.cat_arange);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CategoryArrangement.class);
                intent.putExtra("cat", mAdapterCat.category);
                intent.putExtra("delCat", mAdapterCat.delCategory);
                startActivity(intent);
            }
        });

        CatAdapter.OnItemClickListener listenerCat = new CatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String category) {
                Table.this.currentCategory = category;
                if(newsManager.getLastCategory() != category){
                    news = newsManager.getNews(20, "2019-08-09", "2019-08-10", null, currentCategory, false);
                    mAdapterNews.updateNews(news);
                    mAdapterNews.notifyDataSetChanged();
                    recyclerViewNews.smoothScrollToPosition(0);
                }
                else
                    recyclerViewNews.smoothScrollToPosition(0);
            }
        };

        NewsAdapter.OnItemClickListener listenerNews = new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), NewsPage.class);
                intent.putExtra("news", news.get(position));
                startActivity(intent);
                newsManager.addInHistory(news.get(position));
            }
        };
        mSwipyRefreshLayout = findViewById(R.id.item_swipyrefresh);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.TOP) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Table.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipyRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }, DISMISS_TIMEOUT);
                }
                else if(direction == SwipyRefreshLayoutDirection.BOTTOM){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Table.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<News> newsTmp = newsManager.getNews(20, "2019-08-09",
                                            "2019-08-10", null, currentCategory, true);
                                    mAdapterNews.refreshNews(newsTmp);
                                    mAdapterNews.notifyDataSetChanged();
                                    mSwipyRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }, DISMISS_TIMEOUT);
                }
            }
        });

        newsManager = NewsManager.getNewsManager(this);
        ArrayList<News> newsTmp = newsManager.getNews(20, "2019-08-09",
                "2019-08-10", null, currentCategory, false);
        news = new ArrayList<>();
        news.addAll(newsTmp);

        recyclerViewNews = findViewById(R.id.table_recycler_view);
        layoutManagerNews = new LinearLayoutManager(this);
        recyclerViewNews.setLayoutManager(layoutManagerNews);
        mAdapterNews = new NewsAdapter(news);
        mAdapterNews.setOnItemClickListener(listenerNews);
        recyclerViewNews.setAdapter(mAdapterNews);

        recyclerViewCat = findViewById(R.id.cat_recycler_view);
        layoutManagerCat = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManagerCat).setOrientation(LinearLayout.HORIZONTAL);
        recyclerViewCat.setLayoutManager(layoutManagerCat);
        mAdapterCat = new CatAdapter();
        mAdapterCat.setOnItemClickListener(listenerCat);
        recyclerViewCat.setAdapter(mAdapterCat);

        Intent intent = getIntent();
        ArrayList<String> category = (ArrayList<String>) ((Intent) intent).getSerializableExtra("cat");
        ArrayList<String> delCategory = (ArrayList<String>) ((Intent) intent).getSerializableExtra("delCat");
        if(category != null || delCategory != null){
            mAdapterCat.category = category;
            mAdapterCat.delCategory = delCategory;
            mAdapterCat.notifyDataSetChanged();
            currentCategory = category.get(0);
            news = newsManager.getNews(20, "2019-08-09",
                    "2019-08-10", null, currentCategory, false);
            mAdapterNews.updateNews(news);
            mAdapterNews.notifyDataSetChanged();
        }


    }

}
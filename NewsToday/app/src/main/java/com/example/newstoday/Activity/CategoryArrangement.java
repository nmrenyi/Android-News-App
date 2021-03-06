package com.example.newstoday.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newstoday.CustomLayout.DragGridLayout;
import com.example.newstoday.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryArrangement extends AppCompatActivity {
    private DragGridLayout gridLayout1;
    private DragGridLayout gridLayout2;
    private ArrayList<String> mCategory;
    private ArrayList<String> mDelCategory;
//    private boolean editFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_arrangement);

        gridLayout1 = findViewById(R.id.arr_grid1);
        gridLayout1.setIsRemain(true);
        gridLayout2 = findViewById(R.id.arr_grid2);
        mCategory = new ArrayList<>();
        mDelCategory = new ArrayList<>();

        Intent intent = getIntent();
        ArrayList<String> category = (ArrayList<String>) ((Intent) intent).getSerializableExtra("cat");
        ArrayList<String> delCategory = (ArrayList<String>) ((Intent) intent).getSerializableExtra("delCat");

        mCategory = (ArrayList<String>) ((Intent) intent).getSerializableExtra("cat");
        mDelCategory = (ArrayList<String>) ((Intent) intent).getSerializableExtra("delCat");

        gridLayout1.setCanDrag(true);
        gridLayout1.setItems(category);
        gridLayout2.setItems(delCategory);

        gridLayout1.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                mCategory.remove(tv.getText().toString().replace("+", ""));
                mDelCategory.add(tv.getText().toString().replace("+", ""));
                gridLayout1.removeView(tv);
                gridLayout2.addGridItem(tv.getText().toString());
//                editFlag = true;
            }
        });
        gridLayout2.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                mDelCategory.remove(tv.getText().toString().replace("+", ""));
                mCategory.add(tv.getText().toString().replace("+", ""));
                gridLayout2.removeView(tv);
                gridLayout1.addGridItem(tv.getText().toString());
//                editFlag = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mCategory.size() == 0){
            Toast.makeText(getApplicationContext(), "必须至少选择一个频道", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = getIntent();
        intent.putExtra("cat", mCategory);
        intent.putExtra("delCat", mDelCategory);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}

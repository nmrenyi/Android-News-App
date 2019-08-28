package com.example.newstoday.Adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.newstoday.News;

public class LoopRecyclerAdapter extends BaseRecyclerAdapter {
    private News mNews;
    private int mWidth;

    public LoopRecyclerAdapter(int count, int width, News news) {
        super(count, width, news);
        mNews = news;
        mWidth = width;
    }

    @Override public int getItemCount() {
        if (mCount > 1) {
            return mCount + 2;
        } else {
            return mCount;
        }
    }

    public int getRealItemCount() {
        return mCount;
    }

    public int getRealPosition(int position) {
        if (mCount <= 1) {
            return 0;
        } else {
            return position % mCount;
        }
    }

    public int getLoopPosition(int position) {
        int realPosition = getRealPosition(position);
        if (realPosition == 0) {
            return mCount;
        } else {
            return realPosition;
        }
    }

    @Override public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindView(getRealPosition(position), mWidth, mNews);
    }
}

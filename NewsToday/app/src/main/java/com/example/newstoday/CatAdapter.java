package com.example.newstoday;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.res.Resources.*;
import android.content.Context.*;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.MyViewHolder> {
    private String[] category = {"娱乐", "军事", "教育", "文化",
            "健康", "财经", "体育", "汽车", "科技", "社会"};
    private OnItemClickListener listener;
    private View lastClicked;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.cat_text);
        }
    }

//    public void setOnItemClickListener(CatAdapter.OnItemClickListener listener) {
//        this.listener = listener;
//    }

    @Override
    public CatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cat_text, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textView.setText(category[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                if(listener != null) {
                    int pos = holder.getLayoutPosition();
                    if(lastClicked != null){
                        unselect(lastClicked);
                    }
//                    listener.onItemClick(holder.itemView, pos);
                    select(holder.itemView);
                    lastClicked = holder.itemView;
//                }
             }
        });
        if(lastClicked == null && position == 0){
            select(holder.itemView);
            lastClicked = holder.itemView;
        }
    }

    private void select(View itemView){
        itemView.setBackgroundColor(0xAA151515);
    }

    private void unselect(View itemView){
        itemView.setBackgroundColor(0xFF000000);
    }

    @Override
    public int getItemCount() {
        return category.length;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
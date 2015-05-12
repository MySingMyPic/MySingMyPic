package com.ylsg365.pai.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<String> labels;
    private int layoutResId;

    public HomeAdapter(int layoutResId, int count) {
        labels = new ArrayList<String>(count);
        for (int i = 0; i < count; ++i) {
            labels.add(String.valueOf(i));
        }

        this.layoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ImageView userHeadImageview;
        public ImageView infoIconImageView;
        public TextView userNickNameTextView;
        public TextView cTimeTextView;
        public TextView infoContentTextView;
        public TextView niceCountTextView;
        public TextView commentCountTextView;
        public TextView forwardCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void addData( List<String> tempLabels){
        labels.addAll(tempLabels);
    }
}
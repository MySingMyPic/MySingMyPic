package com.ylsg365.pai.activity.room;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ylsg365.pai.activity.RoomMainViewHolder;

import java.util.ArrayList;
import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RoomMainViewHolder> {

    private final List<String> labels;

    private onRoomItemClickListener onRoomItemClickListener;

    private int layoutResId;

    public RoomAdapter(int layoutResId, int count) {
        labels = new ArrayList<String>(count);
        for (int i = 0; i < count; ++i) {
            labels.add(String.valueOf(i));
        }

        this.layoutResId = layoutResId;

    }

    @Override
    public RoomMainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onRoomItemClickListener.onRoomItemClick();
//            }
//        });
        return new RoomMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RoomMainViewHolder holder, final int position) {
    }


    @Override
    public int getItemCount() {
        return labels.size();
    }


    public void setOnRoomItemClickListener(RoomAdapter.onRoomItemClickListener onRoomItemClickListener) {
        this.onRoomItemClickListener = onRoomItemClickListener;
    }

    interface onRoomItemClickListener {
        void onRoomItemClick();
    }
}
package com.ylsg365.pai.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;

public class RoomMainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
  public TextView textView;
   public View itemView;
  public RoomMainViewHolder(View itemView) {
    super(itemView);
      itemView = itemView.findViewById(R.id.layout_item);
      itemView.setOnClickListener(this);
      System.out.println("itemView get..................");

  }

    @Override
    public void onClick(View v) {
        NavHelper.toRoomMainPage(v.getContext());
    }
}
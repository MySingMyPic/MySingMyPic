package com.ylsg365.pai.activity.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.TextViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<TextViewHolder> {
    private List<String> labels;
    private final int ITEM_VIEW_TYPE_0 = 0;
    private final int ITEM_VIEW_TYPE_1 = 1;
    private final int ITEM_VIEW_TYPE_2 = 2;
    private final int ITEM_VIEW_TYPE_DEFAULT = 100;
    private int layoutResId;

    public MessageAdapter(int layoutResId, int count) {
        labels = new ArrayList<String>(count);
        for (int i = 0; i < count; ++i) {
            labels.add(String.valueOf(i));
        }

        this.layoutResId = layoutResId;
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        switch (position) {
            case 0:
                type = ITEM_VIEW_TYPE_0;
                break;
            case 1:
                type = ITEM_VIEW_TYPE_1;
                break;
            case 2:
                type = ITEM_VIEW_TYPE_2;
                break;

            default:
                type = ITEM_VIEW_TYPE_DEFAULT;
        }
        return type;
    }


    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ImageView imageView = null;
        TextView nameTextView = null;
        switch (viewType) {
            case ITEM_VIEW_TYPE_0:
                layoutResId = R.layout.item_message_category;
                 view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                 imageView = (ImageView)view.findViewById(R.id.img_icon);
                imageView.setBackgroundResource(R.drawable.icon_notice_big);
                 nameTextView = (TextView)view.findViewById(R.id.message_name);
                nameTextView.setText("通知");
                break;
            case ITEM_VIEW_TYPE_1:
                layoutResId = R.layout.item_message_category;
                 view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                 imageView = (ImageView)view.findViewById(R.id.img_icon);
                imageView.setBackgroundResource(R.drawable.icon_comment_big);
                 nameTextView = (TextView)view.findViewById(R.id.message_name);
                nameTextView.setText("评论");
                break;
            case ITEM_VIEW_TYPE_2:
                layoutResId = R.layout.item_message_category;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                imageView = (ImageView)view.findViewById(R.id.img_icon);
                imageView.setBackgroundResource(R.drawable.icon_share_big);
                nameTextView = (TextView)view.findViewById(R.id.message_name);
                nameTextView.setText("转发");
                break;

            default:
                layoutResId = R.layout.item_message_normal;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        }

        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {
//    final String label = labels.get(position);
//    holder.textView.setText(label);
//    holder.textView.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        Toast.makeText(
//            holder.textView.getContext(), label, Toast.LENGTH_SHORT).show();
//      }
//    });
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public void addData( List<String> tempLabels){
        labels.addAll(tempLabels);
    }
}
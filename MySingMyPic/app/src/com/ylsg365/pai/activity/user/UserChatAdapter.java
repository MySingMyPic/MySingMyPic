package com.ylsg365.pai.activity.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.TextViewHolder;

import java.util.ArrayList;
import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<TextViewHolder> {
    private List<String> labels;
    private int layoutResId;
    private final int ITEM_TYPE_ME = 1;
    private final int ITEM_TYPE_OTHER = 2;

    public UserChatAdapter(int layoutResId, int count) {
        labels = new ArrayList<String>(count);
        for (int i = 0; i < count; ++i) {
            labels.add(String.valueOf(i));
        }

        this.layoutResId = layoutResId;
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int itemLayoutResId = -1;
        switch (viewType) {
            case ITEM_TYPE_ME:
                itemLayoutResId = R.layout.item_chat_me;
                break;
            case ITEM_TYPE_OTHER:
                itemLayoutResId = R.layout.item_chat_other;
                break;
            default:
                itemLayoutResId = R.layout.item_chat_me;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResId, parent, false);
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
    public int getItemViewType(int position) {
        return position % 2 + 1;
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }
}
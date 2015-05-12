package com.ylsg365.pai.activity.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.TextViewHolder;
import com.ylsg365.pai.event.NavEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class SingShootAdapter extends RecyclerView.Adapter<TextViewHolder> {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private final View header;
    private final List<String> labels;

    private View.OnClickListener onNacClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(new NavEvent(v.getId()));
        }
    };

  private int  layoutResId;
    public boolean isHeader(int position) {
        return position == 0;
    }
  public SingShootAdapter(View header, int layoutResId, int count) {
      if (header == null) {
          throw new IllegalArgumentException("header may not be null");
      }
      this.header = header;
      this.labels = new ArrayList<String>(count);
      for (int i = 0; i < count; ++i) {
          labels.add(String.valueOf(i));
      }

    this.layoutResId = layoutResId;
  }

  @Override
  public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      if (viewType == ITEM_VIEW_TYPE_HEADER) {
          View head = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);

          head.findViewById(R.id.layout_category_1).setOnClickListener(onNacClickListener);
          head.findViewById(R.id.layout_category_2).setOnClickListener(onNacClickListener);
          head.findViewById(R.id.layout_category_3).setOnClickListener(onNacClickListener);
          head.findViewById(R.id.layout_category_4).setOnClickListener(onNacClickListener);
          head.findViewById(R.id.layout_category_5).setOnClickListener(onNacClickListener);
          head.findViewById(R.id.layout_category_6).setOnClickListener(onNacClickListener);
          head.findViewById(R.id.layout_category_7).setOnClickListener(onNacClickListener);
          head.findViewById(R.id.layout_category_8).setOnClickListener(onNacClickListener);

          return new TextViewHolder(head);
      }

    View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    return new TextViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final TextViewHolder holder, final int position) {
//      if (isHeader(position)) {
//          return;
//      }
//      final String label = labels.get(position - 1);  // Subtract 1 for header
//      holder.textView.setText(label);
//      holder.textView.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//              Toast.makeText(
//                      holder.textView.getContext(), label, Toast.LENGTH_SHORT).show();
//          }
//      });
  }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }



    @Override
    public int getItemCount() {
        return labels.size() + 1;
    }

    public void addData(List<String> tempLabels){
        tempLabels.addAll(tempLabels);
    }

}
package com.ylsg365.pai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ylsg365.pai.R;

import java.util.List;

/**
 * Created by ann on 2015-05-07.
 */
public class DialogListViewAdapter extends BaseAdapter {
    Context context;
    List<String> list;
    public DialogListViewAdapter(Context _context, List<String> _list) {
        this.list = _list;
        this.context = _context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ViewHolder holder;
        if(convertView==null) {
            convertView = layoutInflater.inflate(R.layout.dialog_list_item_layout, null);
            holder=new ViewHolder();
            holder.text=(TextView)convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }
        else holder=(ViewHolder)convertView.getTag();

        holder.text.setText(list.get(position)+"");

        return convertView;
    }

    class ViewHolder
    {
        public TextView text;
    }
}

package com.ylsg365.pai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ylsg365.pai.R;

import java.util.List;

/**
 * Created by ann on 2015-05-06.
 */
public class ChangeMusicVolumeGridViewAdapter extends BaseAdapter {
    Context context;
    List<Integer> list;
    public ChangeMusicVolumeGridViewAdapter(Context _context, List<Integer> _list) {
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
        convertView = layoutInflater.inflate(R.layout.change_music_shengyin_layout, null);

        return convertView;
    }
}

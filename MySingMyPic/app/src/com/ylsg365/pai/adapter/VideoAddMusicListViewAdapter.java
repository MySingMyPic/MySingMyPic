package com.ylsg365.pai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ylsg365.pai.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ann on 2015-05-09.
 */
public class VideoAddMusicListViewAdapter extends BaseAdapter {
    Context context;
    List<Map<String,Object>> list;
    public VideoAddMusicListViewAdapter(Context _context,  List<Map<String,Object>> _list) {
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
            convertView = layoutInflater.inflate(R.layout.video_music_add_list_item_layout, null);
            holder=new ViewHolder();
            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.time=(TextView)convertView.findViewById(R.id.time);
            //holder.title.setText();
            convertView.setTag(holder);
        }
        else holder=(ViewHolder)convertView.getTag();

        if(position < list.size()){
            holder.title.setText((String)list.get(position).get("songName"));
            holder.time.setText((String)list.get(position).get("songId"));
            holder.title.setTag(position);
            holder.time.setTag(position);
        }
        return convertView;
    }

    class ViewHolder
    {
        public TextView title,time;
    }
}

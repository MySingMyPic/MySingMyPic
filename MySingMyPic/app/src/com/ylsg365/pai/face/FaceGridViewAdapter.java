package com.ylsg365.pai.face;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ylsg365.pai.R;

import java.util.List;

public class FaceGridViewAdapter  extends BaseAdapter {

	private LayoutInflater mInflater;
	private Activity activity;
	private List<Integer> data;


	public FaceGridViewAdapter(Activity act, List<Integer> points) {
		mInflater = LayoutInflater.from(act);
		activity = act;

		this.data = points;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public int getSelect(int pos)
	{
		return data.get(pos);
	}
	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.face_grid_view_item_layout, null);

			
			holder.info = (ImageView) convertView
					.findViewById(R.id.imageView1);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(data.get(position)!=-1){
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeResource(activity.getResources(), data.get(position));
		holder.info.setImageBitmap(bitmap);
		}
		else{
			holder.info.setImageDrawable(activity.getResources().getDrawable(R.drawable.clear_icon));
		}
		return convertView;
	}

	private class ViewHolder {
		public ImageView info;		
	}
}


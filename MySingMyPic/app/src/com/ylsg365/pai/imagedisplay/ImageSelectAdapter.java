package com.ylsg365.pai.imagedisplay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ann on 2015-05-14.
 */
public class ImageSelectAdapter extends BaseAdapter {

    ImageLoader mImageLoader= ImageLoader.getInstance();
    private LayoutInflater mInflater;
    private Activity activity;
    private List<PhotoItem> data;
    private int width;
    public ImageSelectAdapter(Activity act,List<PhotoItem> infoList)
    {
        mInflater = LayoutInflater.from(act);
        data=infoList;
        activity=act;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);

         width = wm.getDefaultDisplay().getWidth();

        for(int i=0;i<data.size();i++){
            if(data.get(i)!=null)
            data.get(i).setSelect(false);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelect(int pos)
    {
        if(data.get(pos)!=null){
        boolean flag=data.get(pos).isSelect();
        if(flag==false)
            data.get(pos).setSelect(true);
        else data.get(pos).setSelect(false);
        this.notifyDataSetChanged();
        }
    }

    public List<PhotoItem> getSelect()
    {
        List<PhotoItem> selectList=new ArrayList<PhotoItem>();
        for(int i=0;i<data.size();i++)
        {
            if(data.get(i)!=null&&data.get(i).isSelect())
                selectList.add(data.get(i));
        }
        return selectList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.image_select_item,
                    null);

            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.select=(ImageView)convertView.findViewById(R.id.select);
            holder.root = (RelativeLayout) convertView.findViewById(R.id.root);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int itemWidth=width-2* DensityUtil.dip2px(activity,(float)10.0);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.root
                .getLayoutParams();
        linearParams.height = itemWidth/3;
        linearParams.width = itemWidth/3;
        holder.root.setLayoutParams(linearParams);


        if(data.get(position)!=null){
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(activity.getContentResolver(),  data.get(position).getPhotoID(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
            holder.image.setImageBitmap(bitmap);
        }
        else
        {
            holder.image.setImageDrawable(activity.getResources().getDrawable(R.drawable.paizhao));
        }
        if(data.get(position)!=null){
        if(data.get(position).isSelect()==false)
            holder.select.setImageDrawable(activity.getResources().getDrawable(R.drawable.duoxuan));
        else holder.select.setImageDrawable(activity.getResources().getDrawable(R.drawable.duoxuanxuanzhong));

        }
        else holder.select.setVisibility(View.GONE);
        return convertView;
    }

    private class ViewHolder {
        public ImageView image,select;
        public RelativeLayout root;
    }
}

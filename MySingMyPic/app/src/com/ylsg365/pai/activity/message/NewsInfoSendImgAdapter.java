package com.ylsg365.pai.activity.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.app.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ann on 2015-05-14.
 */
public class NewsInfoSendImgAdapter extends RecyclerView.Adapter<NewsInfoSendImgAdapter.ViewHolder> {
    private List<String> infoList;
    private int layoutResId;
    private OnItemClickListener itemClickListener;
    public NewsInfoSendImgAdapter(int layoutResId, List<String> list) {
        infoList = new ArrayList<String>(list);
        this.layoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);

        ViewHolder holder = new ViewHolder(view);
        holder.setItemClickListener(itemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String url = infoList.get(position);


        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + url, holder.inifoImageview);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    public String getItem(int position){
        return infoList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        private OnItemClickListener mListener;

        public ImageView inifoImageview;

        public ViewHolder(View itemView) {
            super(itemView);

            inifoImageview = (ImageView)itemView.findViewById(R.id.img_info);

            itemView.setOnClickListener(this);
        }

        public OnItemClickListener getItemClickListener() {
            return mListener;
        }

        public void setItemClickListener(OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }


    }

    public void setData(List<String> l)
    {
        infoList.addAll(l);
    }

    public List<String> getData()
    {
        return infoList;
    }
}
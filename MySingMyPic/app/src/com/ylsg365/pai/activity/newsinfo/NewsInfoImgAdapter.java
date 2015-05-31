package com.ylsg365.pai.activity.newsinfo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.List;

public class NewsInfoImgAdapter extends
        RecyclerView.Adapter<NewsInfoImgAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private OnItemClickListener itemClickListener;

    public NewsInfoImgAdapter(int layoutResId, List<JSONObject> list) {
        infoList = list;
        this.layoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                layoutResId, parent, false);
        
        ViewHolder holder = new ViewHolder(view);
        holder.setItemClickListener(itemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        JSONObject infoJsonObject = infoList.get(position);

        ImageLoader.getInstance().displayImage(
                Constants.WEB_IMG_DOMIN
                        + JsonUtil.getString(infoJsonObject, "imgUrl"),
                holder.inifoImageview);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    /**
     * 设置Item点击监听
     * 
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    
    public JSONObject getItem(int position) {
        return infoList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public View itemView;
        private OnItemClickListener mListener;

        public ImageView inifoImageview;

        public ViewHolder(View itemView) {
            super(itemView);

            inifoImageview = (ImageView) itemView.findViewById(R.id.img_info);

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
            if (mListener != null) {
                mListener.onItemClick(v, getPosition());
            }
        }

    }
}

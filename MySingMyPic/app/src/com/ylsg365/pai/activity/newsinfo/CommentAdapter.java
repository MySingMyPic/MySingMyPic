package com.ylsg365.pai.activity.newsinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.face.FaceUtil;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private OnItemClickListener itemClickListener;
    Context context;
    public CommentAdapter(Context context,int layoutResId, List<JSONObject> list) {
        infoList = new ArrayList<JSONObject>(list);
        this.context=context;
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
        JSONObject infoJsonObject = infoList.get(position);

        holder.userNickNameTextView.setText(JsonUtil.getString(infoJsonObject, "nickName"));
        holder.infoContentTextView.setText(FaceUtil.setText(context,JsonUtil.getString(infoJsonObject, "ntext")));

        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "headImg"), holder.userHeadImageview);
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

    public JSONObject getItem(int position){
        return infoList.get(position);
    }

    public void clearData() {
        infoList.clear();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        private OnItemClickListener mListener;

        public ImageView userHeadImageview;
        public TextView userNickNameTextView;
        public TextView cTimeTextView;
        //内容
        public TextView infoContentTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            userHeadImageview = (ImageView)itemView.findViewById(R.id.img_headImg);
            userNickNameTextView = (TextView)itemView.findViewById(R.id.text_nickName);
            infoContentTextView = (TextView)itemView.findViewById(R.id.text_content);
            cTimeTextView = (TextView)itemView.findViewById(R.id.text_cTime);

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

    public void addData(List<JSONObject> tempInfoList){
        infoList.addAll(tempInfoList);
    }
}
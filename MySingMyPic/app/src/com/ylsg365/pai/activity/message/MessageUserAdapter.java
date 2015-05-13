package com.ylsg365.pai.activity.message;

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
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ann on 2015-05-12.
 */
public class MessageUserAdapter extends RecyclerView.Adapter<MessageUserAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private OnItemClickListener onItemClickListener;
    Context context;
    private int selectPos=-1;
    public MessageUserAdapter(Context context,int layoutResId, List<JSONObject> list) {
        infoList = new ArrayList<JSONObject>(list);
        this.context=context;
        this.layoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        JSONObject infoJsonObject = infoList.get(position);


        holder.userNicknameTextView.setText(JsonUtil.getString(infoJsonObject, "nickName"));

        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "headImg"), holder.userHeadImageview);
        if(selectPos==position)
            holder.selectImage.setImageDrawable(context.getResources().getDrawable(R.drawable.duoxuanxuanzhong));
        else holder.selectImage.setImageDrawable(context.getResources().getDrawable(R.drawable.duoxuan));
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public JSONObject getItem(int position){
        return infoList.get(position);
    }

    public void clear() {
        infoList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View itemView;

        public ImageView userHeadImageview;
        public TextView userNicknameTextView;
        public ImageView selectImage;

        public ViewHolder(View itemView) {
            super(itemView);

            userHeadImageview = (ImageView)itemView.findViewById(R.id.img_headImg);
            selectImage=(ImageView)itemView.findViewById(R.id.user_select);
            userNicknameTextView = (TextView)itemView.findViewById(R.id.text_nickName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void addData(List<JSONObject> tempInfoList){
        infoList.addAll(tempInfoList);
    }

    public void setSelectPso(int pos)
    {
        selectPos=pos;
    }
}

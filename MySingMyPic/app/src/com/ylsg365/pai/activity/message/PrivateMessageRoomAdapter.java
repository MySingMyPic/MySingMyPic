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
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ann on 2015-05-13.
 */
public class PrivateMessageRoomAdapter extends RecyclerView.Adapter<PrivateMessageRoomAdapter.ViewHolder>{

    private final int ITEM_VIEW_TYPE_0 = 0;
    private final int ITEM_VIEW_TYPE_1 = 1;

    private List<JSONObject> infoList;
    private int layoutResId;
    private onRoomItemClickListener onRoomItemClickListener;
    private Context mContext;
    private int userId=-1;
    public PrivateMessageRoomAdapter(Context context,int userId, int layoutResId, List<JSONObject> list) {
        mContext = context;
        infoList = new ArrayList<JSONObject>(list);
        this.layoutResId = layoutResId;
    }



    @Override
    public int getItemViewType(int position) {
        int type = 0;
        switch (position) {
            case 0:
                type = ITEM_VIEW_TYPE_0;
                break;
            case 1:
                type = ITEM_VIEW_TYPE_1;
                break;

        }
        return type;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case ITEM_VIEW_TYPE_0:
                layoutResId = R.layout.private_msg_come_layout;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);

                break;
            case ITEM_VIEW_TYPE_1:
                layoutResId = R.layout.private_msg_to_layout;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);

                break;

        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if(position > 2) {
            JSONObject infoJsonObject = infoList.get(position);

            holder.message.setText("");

            ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "sendHeadImg"), holder.userHead);
        }


    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public Object getItem(int position)
    {
        return infoList.get(position);
    }
    public void clearData() {
        infoList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ImageView userHead;
        public TextView message;

        public ViewHolder(View itemView) {
            super(itemView);

            userHead = (ImageView)itemView.findViewById(R.id.imageView);

            message = (TextView)itemView.findViewById(R.id.message);
        }
    }

    public void setOnRoomItemClickListener(PrivateMessageRoomAdapter.onRoomItemClickListener onRoomItemClickListener) {
        this.onRoomItemClickListener = onRoomItemClickListener;
    }

    public interface onRoomItemClickListener {
        void onRoomItemClick(int position);
    }

    public void addData(List<JSONObject> tempInfoList){
        infoList.addAll(tempInfoList);
    }
}

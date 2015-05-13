package com.ylsg365.pai.activity.room;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class RoomChatAdapter extends RecyclerView.Adapter<RoomChatAdapter.ViewHolder> {
    private List<JSONObject> labels = new ArrayList<JSONObject>();;

    public RoomChatAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        JSONObject item = labels.get(position);
        holder.setup(item);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
        public void setup(JSONObject item) {
            ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +JsonUtil.getString(item,"headImg"), (ImageView)getView(R.id.img_headImg));
            setText(R.id.text_cTime,JsonUtil.getString(item,"cTime"));
            setText(R.id.text_nickName,JsonUtil.getString(item,"nickName"));
            setText(R.id.text_content,JsonUtil.getString(item,"ntext"));
        }
        private void setText(int id, String text) {
            TextView tv = (TextView)itemView.findViewById(id);
            tv.setText(text);
        }
        private View getView(int id) {
            return itemView.findViewById(id);
        }
    }

    public List<JSONObject> getList() {
        return labels;
    }
}
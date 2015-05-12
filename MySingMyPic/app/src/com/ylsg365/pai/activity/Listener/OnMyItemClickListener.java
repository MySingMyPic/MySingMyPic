package com.ylsg365.pai.activity.Listener;

import android.view.View;

/**
 * Created by lanzhihong on 2015/5/4.
 */
public interface OnMyItemClickListener {
    public void OnCollectClick(View view, int postion);
    public void OnShareClick(View view, int postion);
    public void OnMsgClick(View view, int postion);
    public void OnGiftClick(View view, int postion);
}
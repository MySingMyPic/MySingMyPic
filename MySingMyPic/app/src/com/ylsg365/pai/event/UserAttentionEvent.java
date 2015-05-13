package com.ylsg365.pai.event;

import android.widget.Button;

public class UserAttentionEvent {
    public int userId;
    public boolean isAttentioned;
    public Button btn;
    public UserAttentionEvent(int id, boolean isAttentioned, Button bt) {
        userId = id;
        this.isAttentioned = isAttentioned;
        btn = bt;
    }
}

package com.ylsg365.pai.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ann on 2015-05-06.
 */
public class MoveView extends ImageView {

    public MoveView(Context context) {
        super(context);
    }

    public MoveView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MoveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 通过layout方法，移动view
     * 优点：对view所在的布局，要求不苛刻，不要是RelativeLayout，而且可以修改view的大小
     *
     * @param view
     * @param rawX
     * @param rawY
     */
    private void moveViewByLayout(View view, int rawX, int rawY) {
        int left = rawX - view.getWidth() / 2;
        int top = rawY  - view.getHeight() / 2;
        int width = left + view.getWidth();
        int height = top + view.getHeight();
        this.setFrame(left, top, width, height);
//        view.layout(left, top, width, height);
    }

    public void moveTo(int x)
    {
        int left = x - this.getWidth() / 2;
        int top = this.getTop();
        int width = left + this.getWidth();
        int height = this.getBottom();
        this.setFrame(left, top, width, height);
    }
    private int downY;
    // 移动
    public boolean autoMouse(MotionEvent event) {
        boolean rb = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                this.moveViewByLayout(MoveView.this, (int) event.getX(), this.getHeight() / 2);
                rb = true;
                break;
        }
        return rb;
    }
}
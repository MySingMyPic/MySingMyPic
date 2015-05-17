package com.ylsg365.pai.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ylsg365.pai.R;

/**
 * Created by ann on 2015-05-06.
 */
public class DrawView extends View {

    Paint p1,p2,p3;
    int leftX=0,rightX=0;
    boolean isMoveLeft=true;
    public DrawView(Context context) {
        super(context);initPaint();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);initPaint();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);initPaint();
    }

    public void initPaint()
    {

        p1 = new Paint();
        //设置抗锯齿效果
        p1.setAntiAlias(true);
        //设置画刷的颜色
        p1.setColor(this.getResources().getColor(R.color.text_item_grey_2));

        p2 = new Paint();
        //设置抗锯齿效果
        p2.setAntiAlias(true);
        //设置画刷的颜色
        p2.setColor(this.getResources().getColor(R.color.tab_line_red));

        p3 = new Paint();
        //设置抗锯齿效果
        p3.setAntiAlias(true);
        //设置画刷的颜色
        p3.setColor(this.getResources().getColor(R.color.text_item_grey_2));
    }


    @Override protected void onDraw(Canvas canvas) {
//          canvas.drawColor(Color.WHITE);
        if(leftX==0)
            leftX=this.getLeft();
        if(rightX==0)
            rightX=this.getRight();

            draw(canvas,mov_x);
//          invalidate();
    }

    int mov_x=0,mov_y;
    public void autoMouse(MotionEvent event) {
        mov_x=(int) event.getX();
        mov_y=(int) event.getY();

        if (event.getAction()==MotionEvent.ACTION_MOVE) {//如果拖动
            this.invalidate();

        }
        else if (event.getAction()==MotionEvent.ACTION_DOWN) {//如果拖动

            if(Math.abs((mov_x-leftX))>Math.abs((mov_x-rightX)))
            {
                isMoveLeft=false;
            }
            else isMoveLeft=true;

            this.invalidate();

        }
       else if (event.getAction()==MotionEvent.ACTION_UP) {//如果拖动
            this.invalidate();
            if(isMoveLeft==true)
                leftX=mov_x;
            else rightX=mov_x;
        }

    }




    public void draw(Canvas canvas,int x)
    {
        if(isMoveLeft==true) {
            if(x<rightX){
                p1.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(this.getLeft(), this.getTop(), x, this.getBottom()), p1);

                p2.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(x, this.getTop(), rightX, this.getBottom()), p2);

                p3.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(rightX, this.getTop(), this.getRight(), this.getBottom()), p3);
            }
            else
            {
                p1.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(this.getLeft(), this.getTop(), this.getRight(), this.getBottom()), p1);
            }
        }
        else
        {
            if(x>leftX){

                p1.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(this.getLeft(), this.getTop(), leftX, this.getBottom()), p1);

                p2.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(leftX, this.getTop(), x, this.getBottom()), p2);

                p3.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(x, this.getTop(), this.getRight(), this.getBottom()), p3);
            }
            else
            {
                p1.setStyle(Paint.Style.FILL);
                canvas.drawRect(new RectF(this.getLeft(), this.getTop(), this.getRight(), this.getBottom()), p1);
            }
        }
    }
}

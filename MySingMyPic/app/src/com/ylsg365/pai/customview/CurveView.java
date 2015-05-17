package com.ylsg365.pai.customview;

/**
 * Created by ann on 2015-05-09.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.ylsg365.pai.R;

import java.util.ArrayList;
import java.util.List;

public class CurveView extends View {
    private int XPoint = 0;
    private int YPoint = 260;
    private int XScale = 8; // 刻度长度
    private int YScale = 40;
    private int XLength = 380;


    private int MaxDataSize = XLength / XScale;

    public List<Integer> data = new ArrayList<Integer>();



    public CurveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        XLength = wm.getDefaultDisplay().getWidth();
        XScale=XLength/10;
        MaxDataSize = XLength / XScale+1;



    }

    public void setData(int pos)
    {
        if (data.size() >= MaxDataSize) {
            data.remove(0);
        }
        data.add(pos);

        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        YPoint=this.getHeight();
        YScale=this.getHeight()/5;




        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); // 去锯齿
        paint.setColor(this.getResources().getColor(R.color.tab_line_red2));

        // 绘折线
         paint.setStrokeWidth(3);
         if(data.size() > 1){ for(int i=1; i<data.size(); i++){
          canvas.drawLine(XPoint + (i-1) * XScale, YPoint - data.get(i-1) *
          YScale, XPoint + i * XScale, YPoint - data.get(i) * YScale, paint); }
          }

        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setAntiAlias(true); // 去锯齿
        paint2.setColor(this.getResources().getColor(R.color.tab_line_red3));
        paint2.setStyle(Paint.Style.FILL);
        if (data.size() > 1) {
            Path path = new Path();
            path.moveTo(XPoint, YPoint);
            for (int i = 0; i < data.size(); i++) {
                path.lineTo(XPoint + i * XScale, YPoint - data.get(i) * YScale);
            }
            path.lineTo(XPoint + (data.size() - 1) * XScale, YPoint);
            canvas.drawPath(path, paint2);
        }
    }
}


package com.ylsg365.pai.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ann on 2015-05-06.
 */
public class RepeatImageView extends ImageView {

    public RepeatImageView(Context context) {
        super(context);
    }

    public RepeatImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public RepeatImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * * 拉伸图片
     *
     * @author ann
     */
    @Override
    protected void onDraw(Canvas canvas) {

        try {
            Drawable drawable = getDrawable();
            if (null != drawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Rect rect = new Rect (0,0,bitmap.getWidth(),bitmap.getHeight());
                int width=bitmap.getWidth()*getHeight()/bitmap.getHeight();

                int count = (this.getWidth() + width - 1) / width;

                for(int idx = 0; idx < count; ++ idx){
                    RectF rectf = new RectF( idx *width,0,( idx+1) * width,getHeight());

                    canvas.drawBitmap(bitmap,rect,rectf,null);
                }


            } else {
                super.onDraw(canvas);
            }
        } catch (Exception e) {
            System.out
                    .println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }


}

package com.ylsg365.pai.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import com.ylsg365.pai.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ann on 2015-05-11.
 */
public class FaceUtil {
    static String zhengze = "#[1-9]#|#1[0-9]#|#2[0-5]#";

    public static SpannableString setText(Context context,String info)
    {
        return getExpressionString(context,info,zhengze);
    }
    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     * @param context
     * @param str
     * @return
     */
    public static SpannableString getExpressionString(Context context,String str,String zhengze){
        SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		//通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
            setUserText(context,spannableString, AT_PATTERN, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }

    private static final Pattern AT_PATTERN = Pattern.compile("@[\\u4e00-\\u9fa5\\w\\-]+");

    private static void setUserText(Context context,SpannableString spannableString, Pattern patten, int start)
    {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.purple));
                int end = matcher.start() + key.length();					//计算该图片名字的长度，也就是要替换的字符串的长度
                spannableString.setSpan(span, matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	//将该图片替换字符串中规定的位置中
                if (end < spannableString.length()) {						//如果整个字符串还未验证完，则继续。。
                    setUserText(context,spannableString,  patten, end);
                }
                break;

        }

    }


    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     * @param context
     * @param spannableString
     * @param patten
     * @param start
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            int resId = FaceImage.images.get(key);		//通过上面匹配得到的字符串来生成图片资源id
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                ImageSpan imageSpan = new ImageSpan(big(bitmap));				//通过图片资源id来得到bitmap，用一个ImageSpan来包装
                int end = matcher.start() + key.length();					//计算该图片名字的长度，也就是要替换的字符串的长度
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);	//将该图片替换字符串中规定的位置中
                if (end < spannableString.length()) {						//如果整个字符串还未验证完，则继续。。
                    dealExpression(context,spannableString,  patten, end);
                }
                break;
            }
        }
    }

    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(2f,2f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}

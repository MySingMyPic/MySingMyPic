package com.ylsg365.pai.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ylsg365.pai.activity.room.RoomMainActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.sina.AccessTokenKeeper;

import java.io.ByteArrayOutputStream;

/**
 * Created by lanzhihong on 2015/4/16.
 */
public class ShareUtil {

    public static IWXAPI wxApi;
    public static IWeiboShareAPI mWeiboShareAPI;

    /**
     * 微信分享实例化
     */
    private  static void initSinaShare(Context mContext) {
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, Constants.APP_KEY);
            mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端
    }

    public  static void initWXShare(Context mContext) {
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(mContext, Constants.WX_APP_ID);
            wxApi.registerApp(Constants.WX_APP_ID);
        }
    }


    /**
     * 分享图片和文字
     *
     * @param context
     * @param text
     * @param bmp
     */
    public static void sendReq(Context context, int flag, String text, Bitmap bmp) {
        initWXShare(context);
        String url = "http://www.baidu.com";//收到分享的好友点击信息会跳转到这个地址去
        WXWebpageObject localWXWebpageObject = new WXWebpageObject();
        localWXWebpageObject.webpageUrl = url;
        WXMediaMessage localWXMediaMessage = new WXMediaMessage(
                localWXWebpageObject);
        localWXMediaMessage.title = "我的应用";//不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
        localWXMediaMessage.description = text;
        localWXMediaMessage.thumbData = getBitmapBytes(bmp, false);
        SendMessageToWX.Req localReq = new SendMessageToWX.Req();
        localReq.transaction = System.currentTimeMillis() + "";
        localReq.message = localWXMediaMessage;
        localReq.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(localReq);
    }

    /**
     * 分享music
     * @param mContext
     * @param flag
     * @param musicUrl
     * @param title
     * @param description
     * @param bmp
     */
    public static void sendMusic(Context mContext, int flag, String musicUrl, String title, String description, Bitmap bmp) {
        initWXShare(mContext);

        WXMusicObject music = new WXMusicObject();

        music.musicUrl = musicUrl;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;
        msg.description = description;
        msg.thumbData = getBitmapBytes(bmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);

    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    // 需要对图片进行处理，否则微信会在log中输出thumbData检查错误
    private static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
        Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        int i;
        int j;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            i = bitmap.getWidth();
            j = bitmap.getWidth();
        } else {
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
        while (true) {
            localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, 80, 80), null);
            if (paramBoolean)
                bitmap.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {

            }
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
    }


    /**
     * 第三方应用响应微博客户端的请求，提供需要分享的数据。
     */
    public static void responseMessage(final Activity mContext, String text, Bitmap bitmap,String title,String shareUrl ,boolean hasText, boolean hasImage, boolean hasWebpage,
                                       boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        initSinaShare(mContext);

        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                responseMultiMessage(mContext,text,bitmap,title,shareUrl,hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
            } else {
                responseSingleMessage(mContext,text,bitmap,title,shareUrl,hasText, hasImage, hasWebpage, hasMusic, hasVideo);
            }
        } else {

            Toast.makeText(mContext, "请安装微博客户端之后重试!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 第三方应用响应微博客户端的请求，提供需要分享的数据。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     */
    private  static void responseMultiMessage(final Activity mContext, String text, Bitmap bitmap,String title,String shareUrl ,boolean hasText, boolean hasImage, boolean hasWebpage,
                                      boolean hasMusic, boolean hasVideo, boolean hasVoice) {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj(text);
        }

        if (hasImage) {
            weiboMessage.imageObject = getImageObj(bitmap);
        }

       // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj(title,text,bitmap,shareUrl);
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj(title,text,bitmap,shareUrl);
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj(title,text,bitmap,shareUrl);
        }
        if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj(title,text,bitmap,shareUrl);
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            mWeiboShareAPI.sendRequest(mContext, request);
        }else  {
            AuthInfo authInfo = new AuthInfo(mContext, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(mContext.getApplicationContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(mContext, request, authInfo, token, new WeiboAuthListener() {

                @Override
                public void onWeiboException( WeiboException arg0 ) {
                }

                @Override
                public void onComplete( Bundle bundle ) {
                    // TODO Auto-generated method stub
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(mContext.getApplicationContext(), newToken);
                    Toast.makeText(mContext, "onAuthorizeComplete token = " + newToken.getToken(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

    /**
     * 第三方应用响应微博客户端的请求，提供需要分享的数据。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    private static  void responseSingleMessage(final Activity mContext, String text, Bitmap bitmap,String title,String shareUrl ,boolean hasText, boolean hasImage, boolean hasWebpage,
                                               boolean hasMusic, boolean hasVideo) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj(text);
        }

        if (hasImage) {
            weiboMessage.mediaObject = getImageObj(bitmap);
        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj(title,text,bitmap,shareUrl);
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj(title,text,bitmap,shareUrl);
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj(title,text,bitmap,shareUrl);
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(mContext, request);
    }


    private static TextObject getTextObj(String shareText) {
        TextObject textObject = new TextObject();
        textObject.text = shareText;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private static ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private static  WebpageObject getWebpageObj(String title,String content,Bitmap bitmap,String shareUrl) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = content;

        // 设置 Bitmap 类型的图片到视频对象里
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = shareUrl;
        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private static  MusicObject getMusicObj(String title,String content,Bitmap bitmap,String shareUrl) {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = title;
        musicObject.description = content;

        // 设置 Bitmap 类型的图片到视频对象里
        musicObject.setThumbImage(bitmap);
        musicObject.actionUrl = shareUrl;
        musicObject.dataUrl = "www.weibo.com";
        musicObject.dataHdUrl = "www.weibo.com";
        musicObject.duration = 10;
        musicObject.defaultText = "Music 默认文案";
        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private static  VideoObject getVideoObj(String title,String content,Bitmap bitmap,String shareUrl) {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = title;
        videoObject.description = content;

        // 设置 Bitmap 类型的图片到视频对象里
        videoObject.setThumbImage(bitmap);
        videoObject.actionUrl = shareUrl;
        videoObject.dataUrl = "www.weibo.com";
        videoObject.dataHdUrl = "www.weibo.com";
        videoObject.duration = 10;
        videoObject.defaultText = "Vedio 默认文案";
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private static  VoiceObject getVoiceObj(String title,String content,Bitmap bitmap,String shareUrl) {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
        voiceObject.identify = Utility.generateGUID();
        voiceObject.title = title;
        voiceObject.description = content;

        // 设置 Bitmap 类型的图片到视频对象里
        voiceObject.setThumbImage(bitmap);
        voiceObject.actionUrl = shareUrl;
        voiceObject.dataUrl = "www.weibo.com";
        voiceObject.dataHdUrl = "www.weibo.com";
        voiceObject.duration = 10;
        voiceObject.defaultText = "Voice 默认文案";
        return voiceObject;
    }

}

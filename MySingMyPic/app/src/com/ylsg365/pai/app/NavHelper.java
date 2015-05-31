package com.ylsg365.pai.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ylsg365.pai.OpenVipSelectActivity;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.PicCutActivity;
import com.ylsg365.pai.activity.RechargeActivity;
import com.ylsg365.pai.activity.login.AccountSettingActivity;
import com.ylsg365.pai.activity.login.LoginActivity;
import com.ylsg365.pai.activity.login.PassWordResetActivity;
import com.ylsg365.pai.activity.login.PasswordForgetActivity;
import com.ylsg365.pai.activity.login.PasswordModifyActivity;
import com.ylsg365.pai.activity.login.PhoneBindActivity;
import com.ylsg365.pai.activity.login.RegisterActivity;
import com.ylsg365.pai.activity.main.DepositActivity;
import com.ylsg365.pai.activity.main.GiftListActivity;
import com.ylsg365.pai.activity.main.MyGiftListActivity;
import com.ylsg365.pai.activity.main.OriginalBaseActivity;
import com.ylsg365.pai.activity.message.CommentActivity;
import com.ylsg365.pai.activity.message.CommentSendActivity;
import com.ylsg365.pai.activity.message.ForwardSendActivity;
import com.ylsg365.pai.activity.message.NewsInfoSendActivity;
import com.ylsg365.pai.activity.message.PrivateMessageSelectUserActivity;
import com.ylsg365.pai.activity.message.PrivateMessageSendActivity;
import com.ylsg365.pai.activity.message.SystemMessageActivity;
import com.ylsg365.pai.activity.music.ChangeMusicVolumeActivity;
import com.ylsg365.pai.activity.music.SingASoneActivity;
import com.ylsg365.pai.activity.music.SongScoreActivity;
import com.ylsg365.pai.activity.newsinfo.NewsInfoDetalActivity;
import com.ylsg365.pai.activity.room.GameCenterActivity;
import com.ylsg365.pai.activity.room.GameInfoActivity;
import com.ylsg365.pai.activity.room.GiftInfoActivity;
import com.ylsg365.pai.activity.room.KaraokeActivity;
import com.ylsg365.pai.activity.room.MyGameActivity;
import com.ylsg365.pai.activity.room.MyRoomActivity;
import com.ylsg365.pai.activity.room.RoomCreateActivity;
import com.ylsg365.pai.activity.room.RoomInfoActivity;
import com.ylsg365.pai.activity.room.RoomMainActivity;
import com.ylsg365.pai.activity.setting.SettingActivity;
import com.ylsg365.pai.activity.singsong.SingerActivity;
import com.ylsg365.pai.activity.singsong.SongActivity;
import com.ylsg365.pai.activity.singsong.SongCategoryActivity;
import com.ylsg365.pai.activity.user.AlreadyPurchaseActivity;
import com.ylsg365.pai.activity.user.AttentionListActivity;
import com.ylsg365.pai.activity.user.BuyTheCopyrightActivity;
import com.ylsg365.pai.activity.user.FansListActivity;
import com.ylsg365.pai.activity.user.LookMeActivity;
import com.ylsg365.pai.activity.user.MoneyManagerActivity;
import com.ylsg365.pai.activity.user.MyAttentionsActivity;
import com.ylsg365.pai.activity.user.MyCollectionListActivity;
import com.ylsg365.pai.activity.user.NewsInfoListActivity;
import com.ylsg365.pai.activity.user.PrivateMessageUserListActivity;
import com.ylsg365.pai.activity.user.UserHomeActivity;
import com.ylsg365.pai.activity.user.UserInfoActivity;
import com.ylsg365.pai.activity.user.WorksListActivity;
import com.ylsg365.pai.activity.video.CappellaRecordActivity;
import com.ylsg365.pai.activity.video.VideoActivity;
import com.ylsg365.pai.activity.video.VideoAddEffectActivity;
import com.ylsg365.pai.activity.video.VideoAddMusicSelectActivity;
import com.ylsg365.pai.activity.video.VideoDetalActivity;
import com.ylsg365.pai.activity.video.VideoLongActivity;
import com.ylsg365.pai.activity.video.VideoStartActivity;
import com.ylsg365.pai.imagedisplay.ImageSelectActivity;
import com.ylsg365.pai.model.UserService;

public class NavHelper {
//	public static void toVoicePage(Context context, Voice voice){
//		Intent intent = new Intent();
//		intent.putExtra("voice", voice);
//		intent.setClass(context, VoiceDetailActivity.class);
//		context.startActivity(intent);
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//
//	public static void toRegisterPageForResult(Context context, int requestCode ){
//		Intent intent = new Intent();
//
//		intent.setClass(context, ZhuCeActivity.class);
//
//		((Activity) context).startActivityForResult(intent, requestCode);
//
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//
//	public static void toProvinceListPage(Context context){
//		Intent intent = new Intent(context, ProvinceListActivity.class);
//		((Activity) context).startActivityForResult(intent, 1);
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//
//	//转到ktv历史页面
//	public static void toOutletsHitsoryPage(Context context){
//		Intent intent = new Intent(context, OutletsHistoryActivity.class);
//		context.startActivity(intent);;
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//
//	public static void mySelfToProvinceListPage(Context context){
//		Intent intent = new Intent(context, ProvinceListActivity.class);
//		((Activity) context).startActivityForResult(intent, 1);
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//
//	public static void toCityListPage(Context context, Province province){
//		Intent intent = new Intent();
//		intent.putExtra("Province", province);
//		intent.setClass(context, CityListActivity.class);
//		((Activity) context).startActivityForResult(intent, 1);
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//
//	public static void toAccountInfoPage(Context context) {
//		Intent intent = new Intent(context, MySelfActivity.class);
//		context.startActivity(intent);
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//	public static void toUserHomePage(Context context, User user) {
//		Intent intent = new Intent(context, UserHomeActivity.class);
//		intent.putExtra("User", user);
//		((Activity) context).startActivityForResult(intent, 1);
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}
//	public static void toUserHomePage(Context context, Voice user) {
//		Intent intent = new Intent(context, UserHomeActivity.class);
//		intent.putExtra("Voice", user);
//		((Activity) context).startActivityForResult(intent, 1);
//		((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
//	}

    public static void toLoginPage(Context context, int requestCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
        forwardAnim((Activity) context);
    }

    public static void toRegisterPageStep_1(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toRegisterPageStep_2ForResult(Context context, String token) {
        Intent intent = new Intent(context, AccountSettingActivity.class);
        intent.putExtra("token", token);
        ((Activity) context).startActivityForResult(intent, 0);
        forwardAnim((Activity) context);
    }

    public static void toUserInfoPage(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toSettingPage(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toPasswordModifyPageForResult(Context context) {
        Intent intent = new Intent(context, PasswordModifyActivity.class);
        ((Activity) context).startActivityForResult(intent, REQUEST_GO_TO_LOGIN);
        forwardAnim((Activity) context);
    }

    public static void toKaraokePage(Context context) {
        Intent intent = new Intent(context, KaraokeActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toSingerPage(Context context) {
        Intent intent = new Intent(context, SingerActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toSongCategoryPage(Context context) {
        Intent intent = new Intent(context, SongCategoryActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toGameCenterPage(Context context) {
        Intent intent = new Intent(context, GameCenterActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toCommentMessagePage(Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转到系统消息页面
     *
     * @param context
     */
    public static void toSystemMessagePage(Context context) {
        Intent intent = new Intent(context, SystemMessageActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去原创基地列表
     *
     * @param context
     */
    public static void toOriginalBaseListPage(Context context, int requestCode) {
        Intent intent = new Intent(context, OriginalBaseActivity.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toOriginalBaseListPage(Context context, int requestCode, int userId) {
        Intent intent = new Intent(context, OriginalBaseActivity.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }
    /**
     * 转去看过我的人页�?
     *
     * @param context
     */
    public static void toLookMeListPage(Context context) {
        Intent intent = new Intent(context, LookMeActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去我的音乐币页�?
     *
     * @param context
     */
    public static void toMyMoneyPage(Context context) {
        Intent intent = new Intent(context, MoneyManagerActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去我赞过的新鲜事列表页�?
     *
     * @param context
     */
    public static void toNiceNewsInfoListPage(Context context) {
        Intent intent = new Intent(context, NewsInfoListActivity.class);
        intent.putExtra(REQUEST_CODE, REQUEST_NICE_NEWSINFO);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去我的新鲜事列表页�?
     * @param context
     */
    public static void toMyNewsInfoListPage(Context context) {
        Intent intent = new Intent(context, NewsInfoListActivity.class);
        intent.putExtra(REQUEST_CODE, REQUEST_MY_NEWSINFO);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去其他用户的新鲜事列表页面
     * @param context
     * @param userId
     */
    public static void toOtherNewsInfoListPage(Context context, int userId) {
        Intent intent = new Intent(context, NewsInfoListActivity.class);
        intent.putExtra(REQUEST_CODE, REQUEST_OTHER_NEWSINFO);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去新鲜事详情页�?
     *
     * @param context
     */
    public static void toNewsInfoDetailPage(Context context, String jsonObjStr) {
        Intent intent = new Intent(context, NewsInfoDetalActivity.class);
        intent.putExtra("newsInfo", jsonObjStr);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去发送私信页�?
     *
     * @param context
     */
    public static void toPrivateMessageSendActivity(Context context,int toUserId,String name)
    {
        Intent intent = new Intent(context, PrivateMessageSendActivity.class);
        intent.putExtra("userId", toUserId);
        intent.putExtra("userName",name);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去选择联系人页�?
     *
     * @param context
     */
    public static void toPrivateMessageSelectUserActivity(Context context)
    {
        Intent intent = new Intent(context, PrivateMessageSelectUserActivity.class);

        context.startActivity(intent);
        forwardAnim((Activity) context);
    }
    public static void toPrivateMessageSelectUserActivityForResult(Activity context, int requestCode, Bundle data)
    {
        Intent intent = new Intent(context, PrivateMessageSelectUserActivity.class);
        intent.putExtras(data);
        context.startActivityForResult(intent, requestCode);
        forwardAnim((Activity) context);
    }

    /**
     * 转去我的收藏列表页面
     *
     * @param context
     */
    public static void toMyCollectionPage(Context context) {
        Intent intent = new Intent(context, MyCollectionListActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }


    /**
     * 转去我的粉丝列表页面
     * @param context
     */
    public static void toMyFansListPage(Context context){
        Intent intent = new Intent(context, FansListActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去我关注的用户列表
     * @param context
     */
    public static void toAttentionListPage(Context context){
        Intent intent = new Intent(context, AttentionListActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去作品列表页面
     *
     * @param context
     */
    public static void toMyWorksPage(Context context) {
        Intent intent = new Intent(context, WorksListActivity.class);
        intent.putExtra(REQUEST_CODE, REQUEST_MY_WORKS);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }
    public static void toOtherWorksPage(Context context, int userId) {
        Intent intent = new Intent(context, WorksListActivity.class);
        intent.putExtra(REQUEST_CODE, REQUEST_OTHER_WORKS);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去音视频详情页�?
     *
     * @param context
     */
    public static void toVideoDetailPage(Context context, String infoObjstr) {
        Intent intent = new Intent(context, VideoDetalActivity.class);
        intent.putExtra("info", infoObjstr);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去其他人空间页�?
     *
     * @param context
     */


    public static void toUserDetailPage(Context context, int userId) {
        Intent intent = new Intent(context, UserHomeActivity.class);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去发评论页�?
     *
     * @param context
     * @param infoObjstr
     */
    public static void toCommentSendPage(Context context, String infoObjstr) {
        Intent intent = new Intent(context, CommentSendActivity.class);
        intent.putExtra("info", infoObjstr);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去发评论页�?
     *
     * @param context
     */
    public static void toNewsInfoSendPage(Context context) {
        Intent intent = new Intent(context, NewsInfoSendActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去转发页面
     *
     * @param context
     * @param infoObjstr
     */
    public static void toForwardSendPage(Context context, String infoObjstr) {
        Intent intent = new Intent(context, ForwardSendActivity.class);
        intent.putExtra("info", infoObjstr);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }


    /**
     * 转去我的评论列表页面
     * @param context
     */
    public static void toMyCommentPage(Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    /**
     * 转去录音界面
     * @param context
     */
    public static void toCappellaRecordPage(Context context) {
        Intent intent = new Intent(context, CappellaRecordActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }
    
    /**
     * 转去录音界面
     * @param context
     */
    public static void toCappellaRecordPage(Context context, String musicUrl) {
        Intent intent = new Intent(context, CappellaRecordActivity.class);
        intent.putExtra("music_url", musicUrl);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toSingASoneActivity(Context context,String path) {
        Intent intent = new Intent(context, SingASoneActivity.class);
        intent.putExtra("AUDIO_PATH",path);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }
	
    public static void toRoomMainPage(Context context) {
        Intent intent = new Intent(context, RoomMainActivity.class);
        ((Activity) context).startActivityForResult(intent, REQUEST_GO_TO_PHONE_BIND);
        forwardAnim((Activity) context);

    }
    
	
    public static void toRoomMainPage(Context context, Bundle data) {
        Intent intent = new Intent(context, RoomMainActivity.class);
        intent.putExtras(data);
        ((Activity) context).startActivityForResult(intent, REQUEST_GO_TO_PHONE_BIND);
        forwardAnim((Activity) context);

    }

    public static void toPassWordForgetActivity(Context mContext) {
        mContext.startActivity(new Intent(mContext, PasswordForgetActivity.class));
        forwardAnim((Activity) mContext);
    }

    public static void toPassWordResetActivity(Context context, String token, String nickName, String headImg) {
        ((Activity) context).finish();
        Intent i = new Intent(context, PassWordResetActivity.class);
        i.putExtra("token", token);
        i.putExtra("nickName", nickName);
        i.putExtra("headImg", headImg);
        context.startActivity(i);
        forwardAnim((Activity) context);
    }

    public static void toPhoneBindActivityForResule(Context context) {
        Intent i = new Intent(context, PhoneBindActivity.class);
        ((Activity) context).startActivityForResult(i, REQUEST_GO_TO_PHONE_BIND);
        forwardAnim((Activity) context);

    }

    public static void toPicCutActivityForResule(Context context, String path) {
        Intent i = new Intent(context, PicCutActivity.class);
        i.putExtra("fileLocation", path);
        ((Activity) context).startActivityForResult(i, REQUEST_GO_TO_PICCUT);
        forwardAnim((Activity) context);

    }

    public static void toRoomCreateActivity(Activity context) {
        Intent i = new Intent(context, RoomCreateActivity.class);
        context.startActivity(i);
        forwardAnim((Activity) context);
    }

    public static void toMyRoomActivity(Activity context) {
        Intent i = new Intent(context, MyRoomActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        forwardAnim((Activity) context);
//        finish(context);
    }

    public static void toRoomInfoActivity(Activity context, boolean IsOwner,String nid) {
        Intent i = new Intent(context, RoomInfoActivity.class);
        i.putExtra("IsOwner", IsOwner);
        i.putExtra("nid", nid);
        context.startActivity(i);
        forwardAnim((Activity) context);
    }

    public static void toSongActivity(Activity mContext, String code, String name) {
        Intent i = new Intent(mContext,SongActivity.class);
        i.putExtra("type",code);
        i.putExtra("typename",name);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);

    }

public static void toSongActivity(Activity mContext, String code, String name,int use) {
        Intent i = new Intent(mContext,SongActivity.class);
        i.putExtra("singid",code);
        i.putExtra("typename",name);
        i.putExtra("use",use);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);

    }
    public static void toVideoActivity(Activity mContext,int choice) {
        Intent i = new Intent(mContext,VideoActivity.class);
        i.putExtra("choice",choice);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);

    }
    public static void toSongActivity(Activity mContext, String name) {
        Intent i = new Intent(mContext,SongActivity.class);
        i.putExtra("songname", name);
        i.putExtra("typename", name);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);

    }

    public static void toMyGameActivity(Activity mContext) {
        Intent i = new Intent(mContext,MyGameActivity.class);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toGameInfoActivity(Activity mContext, String nid) {
        Intent i = new Intent(mContext,GameInfoActivity.class);
        i.putExtra("nid",nid);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    /**
     * 选择@的人
     * @param context
     */
    public static void toPrivateMessageUserListPage(Activity context) {
        Intent i = new Intent(context, PrivateMessageUserListActivity.class);
        context.startActivityForResult(i, REQUEST_SELECT_USER);
        forwardAnim((Activity) context);
    }

    public static void toGiftInfoActivity(Context mContext, String[] giftInfo,String houseId,String receiveUserId,String count) {  //houseId,receiveUserId ,count
        Intent i = new Intent(mContext,GiftInfoActivity.class);
        i.putExtra("giftInfo",giftInfo);
        i.putExtra("houseId",houseId);
        i.putExtra("receiveUserId",receiveUserId);
        i.putExtra("count",count);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toGiftInfoActivityForNewsInfo(Context mContext, String[] giftInfo,int type
            ,String houseId,String count) {  //houseId,receiveUserId ,count
        Intent i = new Intent(mContext,GiftInfoActivity.class);
        i.putExtra("giftInfo",giftInfo);
        i.putExtra("houseId",houseId);
        i.putExtra("type",type);
        i.putExtra("count",count);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toMyGiftActivity(Context mContext) {
        Intent i = new Intent(mContext,MyGiftListActivity.class);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }
    public static void toGiftListActivity(Context mContext,String houseId,String receiveUserId ) {
        Intent i;
        if(UserService.isLogin()){
            i = new Intent(mContext,GiftListActivity.class);
            i.putExtra("houseId",houseId);
            i.putExtra("receiveUserId",receiveUserId);
        }else{
            i = new Intent(mContext,LoginActivity.class);
        }
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toGiftListActivityForNewsInfo(Context mContext,String newsInfoId,int type ) {
        Intent i;
        if(UserService.isLogin()){
            i = new Intent(mContext,GiftListActivity.class);
            i.putExtra("houseId",newsInfoId);
            i.putExtra("type",type);
        }else{
            i = new Intent(mContext,LoginActivity.class);
        }
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toDepositActivity(Context mContext) {
        Intent i = new Intent(mContext,DepositActivity.class);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toOpenVipSelectActivity(Context mContext) {
        Intent i = new Intent(mContext,OpenVipSelectActivity.class);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    /**
     * 跳转到充值界�?
     * @param mContext
     */
    public static void toRechargeActivity(Context mContext) {
        Intent i = new Intent(mContext,RechargeActivity.class);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toMyAttentionsActivity(Context mContext) {
        Intent i = new Intent(mContext,MyAttentionsActivity.class);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);

    }


    public static void toAlreadyPurchaseActivity(Context mContext) {
        Intent i = new Intent(mContext,AlreadyPurchaseActivity.class);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toBuyTheCopyrightActivity(Context mContext,String id,String money,String name,String songName) {
        Intent i = new Intent(mContext,BuyTheCopyrightActivity.class);
        i.putExtra("id",id);
        i.putExtra("money",money);
        i.putExtra("name",name);
        i.putExtra("songName",songName);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    /**
     * 转到新鲜事选择图片
     *
     * @param context
     */
    public static void toImageSelectList(Context context,int requestCode) {
            Intent intent = new Intent(context, ImageSelectActivity.class);
            ((Activity) context).startActivityForResult(intent, requestCode);
            forwardAnim((Activity) context);
    }

    public static void toVideoAddEffectActivity(Activity mContext,String path) {
        Intent i = new Intent(mContext,VideoAddEffectActivity.class);
        i.putExtra("VIDEO_PATH",path);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }
    public static void toVideoAddEffectActivity(Activity mContext,String videopath,String audiopath) {
        Intent i = new Intent(mContext,VideoAddEffectActivity.class);
        i.putExtra("VIDEO_PATH",videopath);
        i.putExtra("AUDIO_PATH",audiopath);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toVideoLongActivity(Activity mContext,String path) {
        Intent i = new Intent(mContext,VideoLongActivity.class);
        i.putExtra("VIDEO_PATH",path);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }

    public static void toChangeMusicVolumeActivity(Activity mContext,String path) {
        Intent i = new Intent(mContext,ChangeMusicVolumeActivity.class);
        i.putExtra("AUDIO_PATH",path);
        mContext.startActivity(i);
        forwardAnim((Activity) mContext);
    }


    public static void toVideoStartActivity(Context context) {
        Intent intent = new Intent(context, VideoStartActivity.class);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }

    public static void toVideoAddMusicSelectActivityForResult(Activity context,int choice) {
        Intent intent = new Intent(context, VideoAddMusicSelectActivity.class);
        intent.putExtra("choice",choice);
        context.startActivityForResult(intent, 0x1111);

        forwardAnim((Activity) context);
    }
    
    public static void toSongScoreActivity(Context context,String singer,String song) {
        Intent intent = new Intent(context, SongScoreActivity.class);
        intent.putExtra("singer",singer);
        intent.putExtra("song",song);
        context.startActivity(intent);
        forwardAnim((Activity) context);
    }
    
    public static void forwardAnim(Activity activity, int animInId, int animOutId) {
        activity.overridePendingTransition(animInId, animOutId);
    }

    public static void forwardAnim(Activity activity) {
        forwardAnim(activity, R.anim.inktv_mysonglist_anim_show_in, R.anim.inktv_mysonglist_anim_show_out);
    }

    public static void finish(Context context, int result) {
        ((Activity) context).setResult(result);
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_hide_in, R.anim.inktv_mysonglist_anim_hide_out);
    }

    public static void finish(Context context) {
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(R.anim.inktv_mysonglist_anim_hide_in, R.anim.inktv_mysonglist_anim_hide_out);
    }


    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, String msg, int time) {
        Toast.makeText(context, msg, time).show();
    }

    public static final String REQUEST_CODE = "request_code";
    public static final String USER_ID = "userId";

    //请求去登�?
    public static final int REQUEST_GO_TO_LOGIN = 1;
    public static final int RESULT_GO_TO_LOGIN_SUCCESS = 2;
    public static final int RESULT_GO_TO_LOGIN_FAILED = 3;
    //绑定手机
    public static final int REQUEST_GO_TO_PHONE_BIND = 4;
    public static final int RESULT_GO_TO_PHONE_BIND_SUCCESS = 5;
    public static final int RESULT_GO_TO_PHONE_BIND_FAILED = 6;

    //选择图片并且裁剪
    public static final int REQUEST_GO_TO_PICCUT = 7;
    public static final int RESULT_GO_TO_PICCUT_SUCCESS = 8;
    public static final int RESULT_GO_TO_PICCUT_FAILED = 9;


    //请求选择要@的人
    public static final int REQUEST_SELECT_USER = 10;
    public static final int RESULT_SELECT_USER_SUCCESS = 11;

    //请求去新鲜事列表页面
    public static final int REQUEST_GO_TO_MY_NEWSINFO = 12;
    public static final int REQUEST_GO_TO_NEWSINFO_BY_NICE = 13;

    //我的原创基地
    public static final int REQUEST_MY_ORIGINAL = 14;
    //其他用户的原创基�?
    public static final int REQUEST_OTHER_ORIGINAL = 15;
    //所有原创基�?
    public static final int REQUEST_ALL_ORIGINAL = 16;

    //我的作品
    public static final int REQUEST_MY_WORKS = 17;
    //其他用户的作�?
    public static final int REQUEST_OTHER_WORKS = 18;


    //我的新鲜事列�?
    public static final int REQUEST_MY_NEWSINFO = 19;
    //我赞过的新鲜事列�?
    public static final int REQUEST_NICE_NEWSINFO = 20;
    //其他用户的新鲜事列表
    public static final int REQUEST_OTHER_NEWSINFO = 21;

    public static final int REQUEST_FROM_LOGIN = 10001;

    public static final int RESULT_SELECT_IMAGE_SUCCESS = 22;
    public static final int RESULT_SELECT_PRIOR_SINGER = 23;
    public static final int RESULT_SELECT_MASTER = 24;

}

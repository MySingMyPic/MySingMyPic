package com.ylsg365.pai.app;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ylsg365.pai.util.ConfigUtil;
import com.ylsg365.pai.util.JsonPostRequest;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.URLUtils;
import com.ylsg365.pai.web.dic.EnumAction;
import com.ylsg365.pai.web.dic.EnumController;
import com.ylsg365.pai.web.dic.EnumParameter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by ylsg365 on 2015-02-08.
 */
public class YinApi {
    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     */
    public static void getValidateCode(String phone, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForPost(url, EnumAction.VALIDATECODE);
//        URLUtils.addParameter(url, EnumParameter.PHONE, phone);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.PHONE.getDesc(), phone);
        httpPost(url.toString(), params, responseListener, errorListener);
    }
//    /**
//     * 忘记密码获取短信验证码
//     * @param phone 手机号
//     */
//    public static void getValidateCodePwdForget(String phone,  Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
//       StringBuilder url = new StringBuilder();
//        URLUtils.addController(url, EnumController.MEMBER);
//        URLUtils.addActionForPost(url, EnumAction.FINDPWDVALIDATECODE);
////        URLUtils.addParameter(url, EnumParameter.PHONE, phone);
//
//        Map<String, String> params = new HashMap<>();
//        params.put( EnumParameter.PHONE.getDesc(), phone);
//        httpPost(url.toString(), params, responseListener, errorListener);
//    }


    /**
     * 用手机号登陆
     *
     * @param phone    手机号
     * @param password 密码
     */
    public static void loginByPhone(String phone, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.LOGIN);
        URLUtils.addParameter(url, EnumParameter.PHONE, phone);
        URLUtils.addParameter(url, EnumParameter.PWD, password);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 通过手机号注册
     *
     * @param phone        手机号
     * @param password     密码
     * @param validateCode 短信验证码
     */
    public static void registerByPhoneStep_1(String phone, String password, String validateCode, String token, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForPost(url, EnumAction.REGISTER);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.VALIDATECODE.getDesc(), validateCode);
        params.put(EnumParameter.PHONE.getDesc(), phone);
        params.put(EnumParameter.PWD.getDesc(), password);
        params.put(EnumParameter.TOKEN.getDesc(), token);

        httpPost(url.toString(), params, responseListener, errorListener);
    }

    /**
     * 注册第二步，此操作完成才算注册成功
     *
     * @param nickName
     * @param headImgUrl
     * @param gender
     * @param address
     * @param token
     * @param responseListener
     * @param errorListener
     */
    public static void registerByPhoneStep_2(String nickName, String headImgUrl, String gender, String address, String token, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.REGISTER2);

        URLUtils.addParameter(url, EnumParameter.NICKNAME, nickName);
        URLUtils.addParameter(url, EnumParameter.HEADIMG, headImgUrl);
        URLUtils.addParameter(url, EnumParameter.SEX, gender);
        URLUtils.addParameter(url, EnumParameter.AREA, address);
        URLUtils.addParameter(url, EnumParameter.TOKEN, token);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 用户修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public static void modifyPassword(String oldPassword, String newPassword, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.UPDATEPWD);
        URLUtils.addParameter(url, EnumParameter.OLDPWD, oldPassword);
        URLUtils.addParameter(url, EnumParameter.PWD, newPassword);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));


        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 获取用户信息
     */
    public static void getUserInfo(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.GETINFO);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));


        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 首页获取信息列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getNewInfos(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETNEWINFOS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取新鲜事详情
     *
     * @param newInfoId
     * @param responseListener
     * @param errorListener
     */
    public static void getNewInfo(int newInfoId , Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETNEWINFO);
        URLUtils.addParameter(url, EnumParameter.NEWINFOID, newInfoId + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 获取所有原创基地列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getALLOriginal(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETORIGINAL);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的原创基地列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyOriginal(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETMYORIGINAL);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取他人原创基地列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getOhterOriginal(int userId, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETHISORIGINAL);
        URLUtils.addParameter(url, EnumParameter.USERID, userId + "");
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的粉丝
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyFans(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.GETFANS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的联系人
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyContact(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.GETCONTACT);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 获取我关注的用户
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyAttentions(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.GETATTENTIONS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取比赛中心列表，搜索
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getGameCenters(String gameName, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GAMECENTER);
        URLUtils.addActionForGet(url, EnumAction.GETGAMECENTERS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        if (!StringUtils.isEmpty(gameName)) {
            URLUtils.addParameter(url, EnumParameter.NNAME, gameName);
        }

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取包房列表
     *
     * @param roomName
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getKtvRoomList(String roomName, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.GETHOUSELIST);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        if (!StringUtils.isEmpty(roomName)) {
            URLUtils.addParameter(url, EnumParameter.NNAME, roomName);
        }

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取用户私信列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getPrivateMessageList(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.PRIVATEMSG);
        URLUtils.addActionForGet(url, EnumAction.GETMSGS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取用户私信列表
     *
     * @param responseListener
     * @param errorListener
     */
    public static void getPrivateContantList( Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.PRIVATEMSG);
        URLUtils.addActionForGet(url, EnumAction.GETCONTACTS);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取用户私信列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getPrivateMessageListOfUser(int PAGE, int rows,int userId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.PRIVATEMSG);
        URLUtils.addActionForGet(url, EnumAction.GETMSGS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.RECEIVEUSERID, userId + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取用户私信列表
     *
     * @param msg
     * @param userId
     * @param responseListener
     * @param errorListener
     */
    public static void sendPrivateMessageOfUser(String msg,int userId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.PRIVATEMSG);
        URLUtils.addActionForPost(url, EnumAction.SENDMSG);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.RECEIVEUSERID.getDesc(), userId + "");
        params.put( EnumParameter.NTEXT.getDesc(), msg+"");
        params.put( EnumParameter.TOKEN.getDesc(), ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpPost(url.toString(),params, responseListener, errorListener);
    }

    /**
     * 获取系统消息列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getSystemMessageList(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.PRIVATEMSG);
        URLUtils.addActionForGet(url, EnumAction.GETSYSMSGS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 获取我关注的人列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getAttentions(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.GETATTENTIONS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取看过我的用户列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getLookmeList(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.LOOKLIST);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 我的赞过新鲜列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getNiceNewInfos(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETNEWINFOSBYUSERNICE);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 新鲜事收藏
     *
     * @param newsInfoId
     * @param responseListener
     * @param errorListener
     */
    public static void collectNewsInfo(int newsInfoId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.NEWINFOCOLLECTION);
        URLUtils.addParameter(url, EnumParameter.NEWINFOID, newsInfoId + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 录音收藏
     *
     * @param recordId
     * @param responseListener
     * @param errorListener
     */
    public static void collectRecord(int recordId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.RECORDCOLLECTION);
        URLUtils.addParameter(url, EnumParameter.RECORDID, recordId + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 视频收藏
     *
     * @param mvId
     * @param responseListener
     * @param errorListener
     */
    public static void mvRecord(int mvId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.MVCOLLECTION);
        URLUtils.addParameter(url, EnumParameter.RECORDID, mvId + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的收藏列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyCollection(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETMYCOLLECTION);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的作品列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyWorks(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETMYWORKS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取其他用户的作品列表
     * @param userId
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getOtherWorks(int userId, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETHISWORKS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的新鲜事列表
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyNewsInfos(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETMYNEWINFOS);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取其他用户的新鲜事列表
     * @param userId
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getOtherNewsInfos(int userId, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETHISNEWINFOS);
        URLUtils.addParameter(url, EnumParameter.USERID, userId + "");
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取新鲜事的转发列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getNewInfoForwards(int newsInfoId, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETNEWINFOFORWARDS);
        URLUtils.addParameter(url, EnumParameter.NEWINFOID, newsInfoId + "");
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取新鲜事的评论列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getNewInfoComments(int newsInfoId, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETNEWINFOCOMMENTS);
        URLUtils.addParameter(url, EnumParameter.NEWINFOID, newsInfoId + "");
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的评论列表
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyComments(int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETNEWINFOCOMMENTSBYFORWARD);
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取新鲜事的点赞列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getNewInfoNices(int newsInfoId, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GENEWINFONICES);
        URLUtils.addParameter(url, EnumParameter.NEWINFOID, newsInfoId + "");
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取新鲜事的点赞列表
     *
     * @param PAGE
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getNewInfoGifts(int type,int newsInfoId, int PAGE, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GIFT);
        URLUtils.addActionForGet(url, EnumAction.INDEXGIFTLIST);
        URLUtils.addParameter(url, EnumParameter.ntype,type+"");
        URLUtils.addParameter(url, EnumParameter.NID, newsInfoId + "");
        URLUtils.addParameter(url, EnumParameter.PAGE, PAGE + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取新鲜事的图片列表
     *
     * @param responseListener
     * @param errorListener
     */
    public static void getNewInfoImg(int newsInfoId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.GETNEWINFOIMG);
        URLUtils.addParameter(url, EnumParameter.NEWINFOID, newsInfoId + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取mv详情
     *
     * @param responseListener
     * @param errorListener
     */
    public static void getMvDetail(int recordId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETMVDETAIL);
        URLUtils.addParameter(url, EnumParameter.RECORDID, recordId + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取音频详情
     *
     * @param responseListener
     * @param errorListener
     */
    public static void getRecordDetail(int recordId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETRECORDDETAIL);
        URLUtils.addParameter(url, EnumParameter.RECORDID, recordId + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 6.根据验证码获取用户接口
     *
     * @param responseListener
     * @param errorListener
     */
    public static void findPwdMember(String phone, String validateCode, String token, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForPost(url, EnumAction.FINDPWDMEMBER);
//        URLUtils.addParameter(url, EnumParameter.VALIDATECODE, validateCode);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.PHONE.getDesc(), phone);
        params.put(EnumParameter.VALIDATECODE.getDesc(), validateCode);
        params.put(EnumParameter.TOKEN.getDesc(), token);
        httpPost(url.toString(), params, responseListener, errorListener);
    }

    /**
     * 设置新密码
     * @param token
     * @param password
     * @param responseListener
     * @param errorListener
     */
    public static void setNewPassword(String token, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.RESETPWD);
        URLUtils.addParameter(url, EnumParameter.TOKEN, token);
        URLUtils.addParameter(url, EnumParameter.PWD, password);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 更新用户昵称
     * @param token
     * @param nickName
     * @param responseListener
     * @param errorListener
     */
    public static void updateNickName(String token, String nickName, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.UPDATENICKNAME);
        URLUtils.addParameter(url, EnumParameter.TOKEN, token);
        URLUtils.addParameter(url, EnumParameter.NICKNAME, nickName);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 修改绑定手机
     * @param phone
     * @param newPhone
     * @param validateCode
     * @param responseListener
     * @param errorListener
     */
    public static void updatePhone(String phone, String newPhone, String validateCode, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForPost(url, EnumAction.UPDATENICKNAME);
//        URLUtils.addParameter(url, EnumParameter.NEWPHONE, newPhone);
//        URLUtils.addParameter(url, EnumParameter.PHONE, phone);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.NEWPHONE.getDesc(), newPhone);
        params.put(EnumParameter.PHONE.getDesc(), phone);

        httpPost(url.toString(), params, responseListener, errorListener);
    }

    /**
     * 上传图片
     */
    public static String imgUplode(String actionUrl, Map<String, String> params,
                                   Map<String, File> files) throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }

        DataOutputStream outStream = new DataOutputStream(conn
                .getOutputStream());
        outStream.write(sb.toString().getBytes());

        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getKey() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        InputStreamReader isReader = new InputStreamReader(in);
        BufferedReader bufReader = new BufferedReader(isReader);
        String line = null;
        String data = "OK";

        while ((line = bufReader.readLine()) == null)
            data += line;
        if (res == 200) {
            int ch;
            StringBuilder sb2 = new StringBuilder();
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        outStream.close();
        conn.disconnect();
        return line;
    }

    /**
     * 上传音频或视频
     */
    public static String mediaUpload(String actionUrl, Map<String, String> params,Map<String, File> files) throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        if (params!=null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }
        }
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());

        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getKey() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        InputStreamReader isReader = new InputStreamReader(in);
        BufferedReader bufReader = new BufferedReader(isReader);
        String line = null;
        String data = "OK";

        while ((line = bufReader.readLine()) == null)
            data += line;
        if (res == 200) {
            int ch;
            StringBuilder sb2 = new StringBuilder();
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        outStream.close();
        conn.disconnect();
        return line;
    }

    /**
     * 新鲜事点赞
     *
     * @param newsInfoId
     * @param responseListener
     * @param errorListener
     */
    public static void doNewsInfoLike(int newsInfoId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForGet(url, EnumAction.NEWINFONICE);
        URLUtils.addParameter(url, EnumParameter.NEWINFOID, newsInfoId + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 新鲜事发送评论
     *
     * @param newsInfoId
     * @param comment
     * @param userId
     * @param responseListener
     * @param errorListener
     */
    public static void sendNewsInfoComment(int newsInfoId, String comment, String userId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForPost(url, EnumAction.NEWINFOCOMMENT);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.FORWARDTEXT.getDesc(), comment);
        if(!TextUtils.isEmpty(userId))
            params.put(EnumParameter.FORWARDUSERID.getDesc(), userId);
        params.put(EnumParameter.NEWINFOID.getDesc(), newsInfoId + "");
        params.put(EnumParameter.TOKEN.getDesc(), ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpPost(url.toString(), params, responseListener, errorListener);
    }

    /**
     * 新鲜事转发
     *
     * @param newsInfoId
     * @param comment
     * @param userId
     * @param responseListener
     * @param errorListener
     */
    public static void sendNewsInfoForward(int newsInfoId, String comment, String userId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForPost(url, EnumAction.NEWINFOFORWARD);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.FORWARDTEXT.getDesc(), comment);
        if(!TextUtils.isEmpty(userId))
            params.put(EnumParameter.FORWARDUSERID.getDesc(), userId);
        params.put(EnumParameter.NEWINFOID.getDesc(), newsInfoId + "");
        params.put(EnumParameter.TOKEN.getDesc(), ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpPost(url.toString(), params, responseListener, errorListener);
    }

    /**
     * 发送新鲜事
     * @param content
     * @param imgIds
     * @param forwardUserIds
     * @param responseListener
     * @param errorListener
     */
    public static void sendNewsInfo(String content, String imgIds, String forwardUserIds, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.NEWINFO);
        URLUtils.addActionForPost(url, EnumAction.ADDNEWINFO);

        Map<String, String> params = new HashMap<String,String>();
        params.put(EnumParameter.TEXT.getDesc(), content);
        if(!StringUtils.isEmpty(imgIds)) {
            params.put(EnumParameter.IMAGES.getDesc(), imgIds);
        }
        if(!StringUtils.isEmpty(forwardUserIds)){
            params.put(EnumParameter.FORWARDUSERIDS.getDesc(), forwardUserIds);
        }
        params.put(EnumParameter.TOKEN.getDesc(), ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpPost(url.toString(), params, responseListener, errorListener);
    }

    /**
     * 上传头像
     * @param headImg
     * @param responseListener
     * @param errorListener
     */
    public static void updateHeadImg(String headImg, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.UPDATEHEADIMG);
        URLUtils.addParameter(url, EnumParameter.HEADIMG, headImg);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 获取其他人的信息
     *
     * @param userId
     * @param responseListener
     * @param errorListener
     */
    public static void getOtherInfo(int userId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.GETOTHERINFO);
        URLUtils.addParameter(url, EnumParameter.USERID, userId + "");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取我的包房列表
     * @param nname
     * @param page
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyHouse(String nname, String page, String rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.GETMYHOUSE);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.NNAME, nname);
        URLUtils.addParameter(url, EnumParameter.PAGE , page );
        URLUtils.addParameter(url, EnumParameter.ROWS  , rows  );

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 新增包房
     *
     * @param nname
     * @param accessAuth
     * @param pwd
     * @param singAuth
     * @param imgUrl
     * @param houseNo
     * @param notice
     * @param zhuboId
     * @param manageIds
     * @param autoQiemai
     * @param responseListener
     * @param errorListener
     */
    public static void addHouse(String nname, String accessAuth, String pwd, String singAuth, String imgUrl, String houseNo, String notice, String zhuboId, String manageIds, String autoQiemai, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.ADDHOUSE);
        URLUtils.addParameter(url, EnumParameter.NNAME, nname);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.ACCESSAUTH, accessAuth);
        URLUtils.addParameter(url, EnumParameter.PWD, pwd);
        URLUtils.addParameter(url, EnumParameter.SINGAUTH, singAuth);
        URLUtils.addParameter(url, EnumParameter.IMGURL, imgUrl);
        URLUtils.addParameter(url, EnumParameter.HOUSENO, houseNo);
        URLUtils.addParameter(url, EnumParameter.NOTICE, notice);
        URLUtils.addParameter(url, EnumParameter.ZHUBOID, zhuboId);
        URLUtils.addParameter(url, EnumParameter.MANAGEIDS, manageIds);
        URLUtils.addParameter(url, EnumParameter.AUTOQIEMAI, autoQiemai);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 包房设置
     *
     * @param nid
     * @param nname
     * @param accessAuth
     * @param pwd
     * @param singAuth
     * @param imgUrl
     * @param houseNo
     * @param notice
     * @param zhuboId
     * @param manageIds
     * @param autoQiemai
     * @param responseListener
     * @param errorListener
     */
    public static void houseUpdate(String nid, String nname, String accessAuth, String pwd, String singAuth, String imgUrl, String houseNo, String notice, String zhuboId, String manageIds, String autoQiemai, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.HOUSEUPDATE);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.NID, nid);
        URLUtils.addParameter(url, EnumParameter.NNAME, nname);
        URLUtils.addParameter(url, EnumParameter.ACCESSAUTH, accessAuth);
        URLUtils.addParameter(url, EnumParameter.PWD, pwd);
        URLUtils.addParameter(url, EnumParameter.SINGAUTH, singAuth);
        URLUtils.addParameter(url, EnumParameter.IMGURL, imgUrl);
        URLUtils.addParameter(url, EnumParameter.HOUSENO, houseNo);
        URLUtils.addParameter(url, EnumParameter.NOTICE, notice);
        URLUtils.addParameter(url, EnumParameter.ZHUBOID, zhuboId);
        URLUtils.addParameter(url, EnumParameter.MANAGEIDS, manageIds);
        URLUtils.addParameter(url, EnumParameter.AUTOQIEMAI, autoQiemai);

        httpGet(url.toString(), responseListener, errorListener);
        
//        StringBuilder url = new StringBuilder();
//      URLUtils.addController(url, EnumController.HOUSE);
//      URLUtils.addActionForPost(url, EnumAction.HOUSEUPDATE);
//
//      Map<String, String> params = new HashMap<String, String>();
//      params.put(EnumParameter.NID.getDesc(), nid);
//      params.put(EnumParameter.NNAME.getDesc(), nname);
//      params.put(EnumParameter.ACCESSAUTH.getDesc(), accessAuth);
//      params.put(EnumParameter.PWD.getDesc(), pwd);
//      params.put(EnumParameter.SINGAUTH.getDesc(), singAuth);
//      params.put(EnumParameter.IMGURL.getDesc(), imgUrl);
//      params.put(EnumParameter.HOUSENO.getDesc(), houseNo);
//      params.put(EnumParameter.NOTICE.getDesc(), notice);
//      params.put(EnumParameter.ZHUBOID.getDesc(), zhuboId);
//      params.put(EnumParameter.MANAGEIDS.getDesc(), manageIds);
//      params.put(EnumParameter.AUTOQIEMAI.getDesc(), autoQiemai);
//      
//      params.put(EnumParameter.TOKEN.getDesc(), ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
//
//      httpPost(url.toString(), params, responseListener, errorListener);
    }

    /**
     * 获取包房详情
     *
     * @param nid
     * @param responseListener
     * @param errorListener
     */
    public static void getHouseDetail(String nid, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.GETHOUSEDETAIL);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.NID, nid);


        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url.toString(), responseListener, errorListener);

        YinApplication.getInstance().getRequestQueue().add(jsonObjReq);
    }
    /**
     * 获取包房聊天列表
     * @param houseId
     * @param responseListener
     * @param errorListener
     */
    public static void getHouseChat(String houseId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.GETHOUSECHATS);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.HOUSEID, houseId);


        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url.toString(), responseListener, errorListener);

        YinApplication.getInstance().getRequestQueue().add(jsonObjReq);
    }
    /**
     * 进如|退出包房
     * @param houseId
     * @param type 0：进入，1：退出
     * @param pwd 密码
     * @param responseListener
     * @param errorListener
     */
    public static void inoutHouse(String houseId, int type, String pwd, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.INOUTTHOUSEUSER);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.HOUSEID, houseId);
        URLUtils.addParameter(url, EnumParameter.TYPE, type + "");
        URLUtils.addParameter(url, EnumParameter.PWD, pwd);


        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url.toString(), responseListener, errorListener);

        YinApplication.getInstance().getRequestQueue().add(jsonObjReq);
        
//        StringBuilder url = new StringBuilder();
//        URLUtils.addController(url, EnumController.HOUSE);
//        URLUtils.addActionForPost(url, EnumAction.INOUTTHOUSEUSER);
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(EnumParameter.TYPE.getDesc(), type + "");
//        params.put(EnumParameter.HOUSEID.getDesc(), houseId);
//        params.put(EnumParameter.PWD.getDesc(), pwd);
//        params.put(EnumParameter.TOKEN.getDesc(), ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
//
//        httpPost(url.toString(), params, responseListener, errorListener);
    }
    /**
     * 发送包房聊天
     * @param houseId
     * @param content
     * @param responseListener
     * @param errorListener
     */
    public static void sendHouseChat(String houseId, String content, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForPost(url, EnumAction.HOUSECHAT);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnumParameter.NTEXT.getDesc(), content);
        params.put(EnumParameter.HOUSEID.getDesc(), houseId);
        params.put(EnumParameter.TOKEN.getDesc(), ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpPost(url.toString(), params, responseListener, errorListener);
    }
    
    /**
     * 获取包房观众列表
     * @param houseId
     * @param page
     * @param responseListener
     * @param errorListener
     */
    public static void getHouseViewers(String houseId, int page, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.GETHOUSEVIEWS);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.HOUSEID, houseId);
        URLUtils.addParameter(url, EnumParameter.PAGE, page + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, 100000 + "");


        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url.toString(), responseListener, errorListener);

        YinApplication.getInstance().getRequestQueue().add(jsonObjReq);
    }
    /**
     * 获取包房排麦列表
     * @param houseId
     * @param page
     * @param responseListener
     * @param errorListener
     */
    public static void getHouseSing(String houseId, int page, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.HOUSE);
        URLUtils.addActionForGet(url, EnumAction.GETHOUSESING);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.HOUSEID, houseId);
        URLUtils.addParameter(url, EnumParameter.PAGE, page + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, 100000 + "");


        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url.toString(), responseListener, errorListener);

        YinApplication.getInstance().getRequestQueue().add(jsonObjReq);
    }
    

    /**
     * 获取歌曲类别
     *
     * @param responseListener
     * @param errorListener
     */
    public static void getSongType(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.SONGTYPE);
        URLUtils.addActionForGet(url, EnumAction.GETSONGTYPE);


        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取歌手类别
     * @param responseListener
     * @param errorListener
     */
    public static void getSingerType(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.SINGERTYPE);
        URLUtils.addActionForGet(url, EnumAction.GETSINGERTYPE);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 我参加的比赛
     * @param page
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getGameCenterByUsers(String page,String rows,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GAMECENTER);
        URLUtils.addActionForGet(url, EnumAction.GETGAMECENTERBYUSERS);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.PAGE, page);
        URLUtils.addParameter(url, EnumParameter.ROWS, rows);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取歌曲
     *
     * @param typeId
     * @param singerId
     * @param songName
     * @param page
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getSongs(String typeId, String singerId, String songName, int page, int rows, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.SONG);
        URLUtils.addActionForGet(url, EnumAction.GETSONGS);
        if (!StringUtil.isNull(typeId)) {
            URLUtils.addParameter(url, EnumParameter.TYPEID, typeId);
        }
        if (!StringUtil.isNull(singerId)) {

            URLUtils.addParameter(url, EnumParameter.SINGERID, singerId);
        }
        if (!StringUtil.isNull(songName)) {

            URLUtils.addParameter(url, EnumParameter.SONGNAME, songName);
        }
        URLUtils.addParameter(url, EnumParameter.PAGE, page + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取比赛详情
     * @param responseListener
     * @param errorListener
     */
    public static void getGameCenterInfo(String nid,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GAMECENTER);
        URLUtils.addActionForGet(url, EnumAction.GETGAMECENTERDETAIL);
        URLUtils.addParameter(url, EnumParameter.NID, nid);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 获取比赛详情
     * @param responseListener
     * @param errorListener
     */
    public static void getGameUsers(String nid,int page, int rows,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GAMECENTER);
        URLUtils.addActionForGet(url, EnumAction.GETGAMEUSERS);
        URLUtils.addParameter(url, EnumParameter.GAMECENTERID, nid);
        URLUtils.addParameter(url, EnumParameter.PAGE, page + "");
        URLUtils.addParameter(url, EnumParameter.ROWS, rows + "");

        httpGet(url.toString(), responseListener, errorListener);
    }



    /**
     * 关注用户
     * @param attentionId
     * @param responseListener
     * @param errorListener
     */
    public static void attentionToUser(int attentionId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.ATTENTION);
        URLUtils.addParameter(url, EnumParameter.ATTENTIONID, attentionId + "");
        URLUtils.addParameter(url, EnumParameter.TYPE, "0");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 悄悄关注用户
     * @param attentionId
     * @param responseListener
     * @param errorListener
     */
    public static void attentionToUserQuiet(int attentionId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.ATTENTION);
        URLUtils.addParameter(url, EnumParameter.ATTENTIONID, attentionId + "");
        URLUtils.addParameter(url, EnumParameter.TYPE, "1");
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 取消关注用户
     * @param attentionId
     * @param responseListener
     * @param errorListener
     */
    public static void unAttentionToUser(String attentionId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.ATTENTION);
        URLUtils.addActionForGet(url, EnumAction.CANCELATTENTION);
        URLUtils.addParameter(url, EnumParameter.ATTENTIONID, attentionId );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }


    public static void uploadRecord(RequestCallBack callBack){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.FILE);
        URLUtils.addActionForGet(url, EnumAction.SNSBYFILE);

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("method", "upload");
        params.addQueryStringParameter("path", "/apps/测试应用/test.zip");
        params.addBodyParameter("file", new File("/sdcard/test.zip"));

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url.toString(), params, callBack);
    }

    /**
     * 获取礼物列表
     * @param page
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getGiftList( String page ,String rows,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GIFT);
        URLUtils.addActionForGet(url, EnumAction.GETGIFTLIST);
        URLUtils.addParameter(url, EnumParameter.PAGE, page );
        URLUtils.addParameter(url, EnumParameter.ROWS, rows );

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 我的礼物列表
     * @param page
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getMyGiftList( String page ,String rows,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GIFT);
        URLUtils.addActionForGet(url, EnumAction.GETMYGIFTLIST);
        URLUtils.addParameter(url, EnumParameter.PAGE, page );
        URLUtils.addParameter(url, EnumParameter.ROWS, rows );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 送礼物
     * @param giftId
     * @param gCount
     * @param houseId
     * @param receiveUserId
     * @param responseListener
     * @param errorListener
     */
    public static void sendGift( String giftId ,String gCount,String houseId ,String receiveUserId ,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GIFT);
        URLUtils.addActionForGet(url, EnumAction.SENDGIFT);
        URLUtils.addParameter(url, EnumParameter.GIFTID, giftId );
        URLUtils.addParameter(url, EnumParameter.GCOUNT, gCount );
        if(!StringUtil.isNull(houseId)){
            URLUtils.addParameter(url, EnumParameter.HOUSEID , houseId  );
        }
        URLUtils.addParameter(url, EnumParameter.RECEIVEUSERID , receiveUserId  );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 送礼物
     * @param giftId
     * @param gCount
     * @param houseId
     * @param type
     * @param responseListener
     * @param errorListener
     */
    public static void sendGiftForNewsInfo( String giftId ,String gCount,String houseId ,int type ,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.GIFT);
        URLUtils.addActionForGet(url, EnumAction.SENDINDEXGIFT);
        URLUtils.addParameter(url, EnumParameter.GIFTID, giftId );
        URLUtils.addParameter(url, EnumParameter.GCOUNT, gCount );
        if(!StringUtil.isNull(houseId)){
            URLUtils.addParameter(url, EnumParameter.NID , houseId  );
        }
        URLUtils.addParameter(url, EnumParameter.NTYPE , type+""  );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }


    /**
     * 通过银行卡号码获得银行名称
     * @param id
     * @param responseListener
     * @param errorListener
     */
    public static void getBankBist(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.CASH);
        URLUtils.addActionForGet(url, EnumAction.GETBANKLIST);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 取现
     * @param bankCard
     * @param idNumber
     * @param bankId
     * @param bankBranchName
     * @param bankPhone
     * @param money
     * @param responseListener
     * @param errorListener
     */
    public static void cash( String bankCard ,String idNumber,String bankId,String bankBranchName,String bankPhone,String money,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.CASH);
        URLUtils.addActionForGet(url, EnumAction.CASH);
        URLUtils.addParameter(url, EnumParameter.BANKCARD , bankCard  );
        URLUtils.addParameter(url, EnumParameter.IDNUMBER , idNumber  );
        URLUtils.addParameter(url, EnumParameter.BANKID , bankId  );
        URLUtils.addParameter(url, EnumParameter.BANKBRANCHNAME , bankBranchName  );
        URLUtils.addParameter(url, EnumParameter.BANKPHONE , bankPhone  );
        URLUtils.addParameter(url, EnumParameter.MONEY , money  );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 90.会员套餐接口
     * @param responseListener
     * @param errorListener
     */
    public static void getMeals( Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.GETMEALS);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 开通会员
     * @param MealId
     * @param responseListener
     * @param errorListener
     */
    public static void openMeal( String MealId,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.MEMBER);
        URLUtils.addActionForGet(url, EnumAction.OPENMEAL);
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));
        URLUtils.addParameter(url, EnumParameter.MEALID,MealId);

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 提现记录
     * @param responseListener
     * @param errorListener
     */
    public static void getCashList(String page,String rows ,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.CASH);
        URLUtils.addActionForGet(url, EnumAction.GETCASHLIST);
        URLUtils.addParameter(url, EnumParameter.PAGE , page  );
        URLUtils.addParameter(url, EnumParameter.ROWS , rows  );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 充值记录
     * @param page
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getRechargeList(String page,String rows ,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.CASH);
        URLUtils.addActionForGet(url, EnumAction.GETRECHARGELIST);
        URLUtils.addParameter(url, EnumParameter.PAGE , page  );
        URLUtils.addParameter(url, EnumParameter.ROWS , rows  );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     *89.已购单曲接口
     * @param page
     * @param rows
     * @param responseListener
     * @param errorListener
     */
    public static void getPayRecords(String page,String rows ,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.GETPAYRECORDS);
        URLUtils.addParameter(url, EnumParameter.PAGE , page  );
        URLUtils.addParameter(url, EnumParameter.ROWS , rows  );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }

    /**
     * 购买版权
     * @param responseListener
     * @param errorListener
     */
    public static void recordPay(String songId,String name,String idCard ,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.RECORD);
        URLUtils.addActionForGet(url, EnumAction.RECORDPAY);
        URLUtils.addParameter(url, EnumParameter.RECORDID  , songId  );
        URLUtils.addParameter(url, EnumParameter.NNAME  , name  );
        URLUtils.addParameter(url, EnumParameter.IDNUMBER  , idCard  );
        URLUtils.addParameter(url, EnumParameter.TOKEN, ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN));

        httpGet(url.toString(), responseListener, errorListener);
    }





    private static void httpGet(String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        LogUtil.logd("httpGet", url);
//        http.send(HttpRequest.HttpMethod.GET, url, requestCallBack);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);

        YinApplication.getInstance().getRequestQueue().add(jsonObjReq);
    }

    private static void httpPost(String url, final Map<String, String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        LogUtil.logd("httpPost", url);
//        http.send(HttpRequest.HttpMethod.GET, url, requestCallBack);


        JsonPostRequest jsonObjReq = new JsonPostRequest(url, params, responseListener, errorListener);

        YinApplication.getInstance().getRequestQueue().add(jsonObjReq);
    }


    /**
     * 获取30秒歌曲
     *
     * @param responseListener
     * @param errorListener
     */
    public static void getSongs30( Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        StringBuilder url = new StringBuilder();
        URLUtils.addController(url, EnumController.SONG);
        URLUtils.addActionForGet(url, EnumAction.GETSONGS30);

        httpGet(url.toString(), responseListener, errorListener);
    }

}

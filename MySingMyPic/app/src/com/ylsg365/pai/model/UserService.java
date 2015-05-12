package com.ylsg365.pai.model;

import com.google.gson.Gson;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.ConfigUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * Created by ylsg365 on 2015-03-09.
 */
public class UserService {
    public static boolean isLogin(){
        return getUser() == null ? false : true;
    }

    public static User getUser(){
        return new Gson().fromJson(ConfigUtil.getStringValue(ConfigUtil.CONFIG_USER), User.class);
    }

    public static User getUser(JSONObject json){
        User user = new User();
        user.setUserId(JsonUtil.getInt(json, "userId"));
        user.setPhone(JsonUtil.getString(json, "phone"));
        user.setNickName(JsonUtil.getString(json, "nickName"));
        user.setArea(JsonUtil.getString(json, "area"));
        user.setBalance(JsonUtil.getDouble(json, "balance"));
        user.setFansNum(JsonUtil.getInt(json, "fansNum"));
        user.setcTime(JsonUtil.getString(json, "cTime"));
        user.setAttention(JsonUtil.getBoolean(json, "attention"));
        user.setGiftMoney(JsonUtil.getDouble(json, "giftMoney"));
        user.setAttentionNUm(JsonUtil.getInt(json, "attentionNum"));
        user.setNewInfoNum(JsonUtil.getInt(json, "newInfoNum"));

        if(!StringUtils.isEmpty(JsonUtil.getString(json, "headImg"))){
            user.setHeadImg(Constants.WEB_IMG_DOMIN + JsonUtil.getString(json, "headImg").trim());
        }

        return user;
    }

    public static User saveUser(JSONObject json){
        User user = new User();
        user.setUserId(JsonUtil.getInt(json, "userId"));
        user.setPhone(JsonUtil.getString(json, "phone"));
        user.setNickName(JsonUtil.getString(json, "nickName"));
        user.setArea(JsonUtil.getString(json, "area"));
        user.setBalance(JsonUtil.getDouble(json, "balance"));
        user.setFansNum(JsonUtil.getInt(json, "fansNum"));
        user.setcTime(JsonUtil.getString(json, "cTime"));
        user.setAttention(JsonUtil.getBoolean(json, "attention"));
        user.setGiftMoney(JsonUtil.getDouble(json, "giftMoney"));
        user.setAttentionNUm(JsonUtil.getInt(json, "attentionNum"));
        user.setNewInfoNum(JsonUtil.getInt(json, "newInfoNum"));

        if(!StringUtils.isEmpty(JsonUtil.getString(json, "headImg"))){
            user.setHeadImg(Constants.WEB_IMG_DOMIN + JsonUtil.getString(json, "headImg"));
        }

        ConfigUtil.saveValue(ConfigUtil.CONFIG_USER, new Gson().toJson(user));
        if(!StringUtil.isNull(JsonUtil.getString(json, "token"))){
            ConfigUtil.saveValue(ConfigUtil.CONFIG_TOKEN, JsonUtil.getString(json, "token"));
        }

        return user;
    }

    public static void logout(){
        ConfigUtil.remove(ConfigUtil.CONFIG_USER);
        ConfigUtil.remove(ConfigUtil.CONFIG_TOKEN);
    }
}

package com.ylsg365.pai.util;

import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.YinApplication;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ylsg365 on 2015-02-08.
 */
public class ValidateUtil {
    public static boolean checkUserNickName(String nickName){
        boolean result = true;
        int noticeId = -1;

        if(StringUtils.isBlank(nickName)){
            noticeId = R.string.register_notice_nickname_blank;
            result = false;
        }else if(nickName.length()>15){
            noticeId = R.string.register_notice_nickname_length;
            result = false;
        }

        if(-1 != noticeId){
            Toast.makeText(YinApplication.getInstance(), YinApplication.getInstance().getResources().getString(noticeId), Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public static boolean checkUserAddress(String address){
        boolean result = true;
        int noticeId = -1;

        if(StringUtils.isBlank(address)){
            noticeId = R.string.register_notice_address_blank;
            result = false;
        }else if(address.length()>15){
            noticeId = R.string.register_notice_address_length;
            result = false;
        }

        if(-1 != noticeId){
            Toast.makeText(YinApplication.getInstance(), YinApplication.getInstance().getResources().getString(noticeId), Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public static boolean checkPhone(String phone){
        boolean result = true;
        int noticeId = -1;

        if(StringUtils.isBlank(phone)){
            noticeId = R.string.register_notice_name_blank;
            result = false;
        }else if(!isPhoneNumber(phone)){
            noticeId = R.string.register_notice_name_length;
            result = false;
        }

        if(-1 != noticeId){
            Toast.makeText(YinApplication.getInstance(), YinApplication.getInstance().getResources().getString(noticeId), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
    public static boolean checkValidateCode(String validateCode){
        boolean result = true;
        int noticeId = -1;

        if(StringUtils.isBlank(validateCode)){
            noticeId = R.string.register_notice_validate_code_blank;
            result = false;
        }else if(validateCode.length() != 6){
            noticeId = R.string.register_notice_validate_code_error;
            result = false;
        }

        if(-1 != noticeId){
            Toast.makeText(YinApplication.getInstance(), YinApplication.getInstance().getResources().getString(noticeId), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public static boolean checkUserPassword(String password){
        boolean result = true;
        int noticeId = -1;

        if(StringUtils.isBlank(password)){
            noticeId = R.string.register_notice_password_blank;
            result = false;
        }else if(password.length() < 6 || password.length()>12){
            noticeId = R.string.register_notice_password_length;
            result = false;
        }

        if(-1 != noticeId){
            Toast.makeText(YinApplication.getInstance(), YinApplication.getInstance().getResources().getString(noticeId), Toast.LENGTH_SHORT).show();
        }

        return result;
    }
    public static boolean checkUserPasswordConfirm(String password, String confrimPassword){
        boolean result = true;
        int noticeId = -1;

        if(StringUtils.isBlank(password)){
            noticeId = R.string.register_notice_password_blank;
            result = false;
        }else if(password.length() < 6 || password.length()>12){
            noticeId = R.string.register_notice_password_length;
            result = false;
        }else if(!password.equals(confrimPassword)){
            noticeId = R.string.register_notice_password_confirm_error;
            result = false;
        }

        if(-1 != noticeId){
            Toast.makeText(YinApplication.getInstance(), YinApplication.getInstance().getResources().getString(noticeId), Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public static boolean isPhoneNumber(String phoneNum) {
        String expression = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        CharSequence inputStr = phoneNum;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 正则表达式 判断邮箱格式是否正确
     * */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}

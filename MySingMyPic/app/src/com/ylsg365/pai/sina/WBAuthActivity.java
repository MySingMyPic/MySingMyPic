package com.ylsg365.pai.sina;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.ConfigUtil;

/**
 * 新浪微博授权页面
 */
public class WBAuthActivity extends Activity {
    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wbauth);

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);


        mSsoHandler = new SsoHandler(WBAuthActivity.this, mAuthInfo);

        mSsoHandler.authorize(new AuthListener());
    }


    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {

            mAccessToken = Oauth2AccessToken.parseAccessToken(values); // 从 Bundle 中解析 Token
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), mAccessToken);; //保存Token     .........

            } else {       // 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
//                String code = values.getString("code", "");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }

        @Override
        public void onCancel() {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


}

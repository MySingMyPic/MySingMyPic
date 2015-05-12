package com.ylsg365.pai.activity.main;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.event.NavEvent;
import com.ylsg365.pai.event.UserInfoRefreshEvent;
import com.ylsg365.pai.event.UserInfoReloadEvent;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.layout_main_bottom_bar_home).setOnClickListener(onBottomBarBtnClickListener);
        findViewById(R.id.layout_main_bottom_bar_message).setOnClickListener(onBottomBarBtnClickListener);
        findViewById(R.id.layout_main_bottom_bar_sing).setOnClickListener(onBottomBarBtnClickListener);
        findViewById(R.id.layout_main_bottom_bar_me).setOnClickListener(onBottomBarBtnClickListener);

        init();


    }

    private void setTitle(){
//       findViewById(R.id.img_actionbar_back).setVisibility(View.GONE);
    }

    public void init(){
        setTitle();
        initBottomBar();
        setCurrentPage(R.id.layout_main_bottom_bar_home);
        initFragments();
        refreshInfo();

        EventBus.getDefault().register(this);
    }

    private void initBottomBar(){
    }

    private int lastClickBottomBarLayoutId = 0;
    private void setBottomBarBtnHighlight(int btn){
        int btnTextId = 0;
        int btnImgId = 0;
        int btnHighLightImgResId = 0;
        switch (btn){
            case R.id.layout_main_bottom_bar_home:
                btnTextId = R.id.text_bottom_bar_home;
                btnImgId = R.id.img_bottom_bar_home;
                btnHighLightImgResId = R.drawable.btn_main_home_p;
                break;
            case R.id.layout_main_bottom_bar_message:
                btnTextId = R.id.text_bottom_bar_message;
                btnImgId = R.id.img_bottom_bar_message;
                btnHighLightImgResId = R.drawable.btn_main_message_p;
                break;
            case R.id.layout_main_bottom_bar_sing:
                btnTextId = R.id.text_bottom_bar_sing;
                btnImgId = R.id.img_bottom_bar_sing;
                btnHighLightImgResId = R.drawable.btn_main_sing_p;
                break;
            case R.id.layout_main_bottom_bar_me:
                btnTextId = R.id.text_bottom_bar_me;
                btnImgId = R.id.img_bottom_bar_me;
                btnHighLightImgResId = R.drawable.btn_main_me_p;
                break;
        }

        TextView textView = (TextView)findViewById(btnTextId);
        textView.setTextAppearance(this, R.style.Yin_main_bottombar_btn_Highlight);

        ImageView imageView = (ImageView)findViewById(btnImgId);
        imageView.setImageResource(btnHighLightImgResId);

        setBottomBarBtnNormal(lastClickBottomBarLayoutId);

        lastClickBottomBarLayoutId = btn;
    }

    private void setBottomBarBtnNormal(int btn){
        if(btn == 0){
            return;
        }

        int btnTextId = 0;
        int btnImgId = 0;
        int btnHighLightImgResId = 0;
        switch (btn){
            case R.id.layout_main_bottom_bar_home:
                btnTextId = R.id.text_bottom_bar_home;
                btnImgId = R.id.img_bottom_bar_home;
                btnHighLightImgResId = R.drawable.btn_main_home_n;
                break;
            case R.id.layout_main_bottom_bar_message:
                btnTextId = R.id.text_bottom_bar_message;
                btnImgId = R.id.img_bottom_bar_message;
                btnHighLightImgResId = R.drawable.btn_main_message_n;
                break;
            case R.id.layout_main_bottom_bar_sing:
                btnTextId = R.id.text_bottom_bar_sing;
                btnImgId = R.id.img_bottom_bar_sing;
                btnHighLightImgResId = R.drawable.btn_main_sing_n;
                break;
            case R.id.layout_main_bottom_bar_me:
                btnTextId = R.id.text_bottom_bar_me;
                btnImgId = R.id.img_bottom_bar_me;
                btnHighLightImgResId = R.drawable.btn_main_me_n;
                break;
        }

        TextView textView = (TextView)findViewById(btnTextId);
        textView.setTextAppearance(this, R.style.Yin_main_bottombar_btn);

        ImageView imageView = (ImageView)findViewById(btnImgId);
        imageView.setImageResource(btnHighLightImgResId);

    }

    private int currentPageId = 0;
    private void setCurrentPage(int bottomBarLayoutId){
        if(bottomBarLayoutId == currentPageId){
            //如果已经是当前选中的就不处理
            return;
        }

        setBottomBarBtnHighlight(bottomBarLayoutId);

        switchContent(getCurrentFramgent(), getTargetFragment(bottomBarLayoutId));

        currentPageId = bottomBarLayoutId;
    }


    private View.OnClickListener onBottomBarBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isLogin() && v.getId() !=R.id.layout_main_bottom_bar_home){
                toLoginPage();
                return;
            }
            switch (v.getId()){
                case R.id.layout_main_bottom_bar_home:
                    setCurrentPage(R.id.layout_main_bottom_bar_home);
                    break;
                case R.id.layout_main_bottom_bar_message:
                    setCurrentPage(R.id.layout_main_bottom_bar_message);
                    break;
                case R.id.layout_main_bottom_bar_sing:
                    setCurrentPage(R.id.layout_main_bottom_bar_sing);
                    break;
                case R.id.layout_main_bottom_bar_me:
                    setCurrentPage(R.id.layout_main_bottom_bar_me);
                    break;
            }
        }
    };

    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private SingShootFragment singShootFragment;
    private MeFragment meFragment;
    private void initFragments() {
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        singShootFragment = new SingShootFragment();
        meFragment = new MeFragment();

        addToThis();
    }

    private Fragment getCurrentFramgent(){
        if(currentFragment == null){
            currentFragment = homeFragment;
        }

        return currentFragment;
    }

    private Fragment getTargetFragment(int targetId) {
        Fragment tragetFragment = null;

            switch (targetId){
                case R.id.layout_main_bottom_bar_home:
                    tragetFragment = homeFragment;
                    break;
                case R.id.layout_main_bottom_bar_message:
                    tragetFragment = messageFragment;
                    break;
                case R.id.layout_main_bottom_bar_sing:
                    tragetFragment = singShootFragment;
                    break;
                case R.id.layout_main_bottom_bar_me:
                    tragetFragment = meFragment;
                    break;
            }

        return tragetFragment;
    }

    private void addToThis() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frame_main_center, homeFragment);
        transaction.add(R.id.frame_main_center, messageFragment).hide(messageFragment);
        transaction.add(R.id.frame_main_center, singShootFragment).hide(singShootFragment);
        transaction.add(R.id.frame_main_center, meFragment).hide(meFragment);
        transaction.commitAllowingStateLoss();
    }

    private Fragment currentFragment;

    private void switchContent(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            transaction = fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, R.anim.pop_fade_out);
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(from).add(R.id.frame_main_center, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
//			transaction.replace(R.id.frame_main_center, to).addToBackStack(null).commit();
        }

    }

    private boolean isLogin(){
        return UserService.isLogin();
    }


    private void refreshInfo(){

        if(UserService.isLogin()){
            refreshUserInfo();
        }
    }

    private void refreshUserInfo(){

        YinApi.getUserInfo(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getUserInfo", response.toString());


                if (JsonUtil.getBoolean(response, "status")) {
                    UserService.saveUser(response);
                    EventBus.getDefault().post(new UserInfoRefreshEvent());
                }
            }
        }, null);
    }

    public void onEvent(NavEvent navEvent){
        switch (navEvent.id){
            case R.id.layout_main_bottom_bar_sing:
                setCurrentPage(R.id.layout_main_bottom_bar_sing);
                break;
            case R.id.layout_main_bottom_bar_home:
                setCurrentPage(R.id.layout_main_bottom_bar_home);
                break;
        }
    }

    public void onEvent(UserInfoReloadEvent event){
        refreshUserInfo();
    }

    private void toLoginPage(){
        NavHelper.toLoginPage(MainActivity.this, REQUEST_LOGIN_FROM_MAIN);
    }


    public static final int RESULT_LOGIN_SUCCESS = 1;
    public static final int RESULT_LOGOUT_SUCCESS = 2;
    public static final int REQUEST_LOGIN_FROM_MAIN = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOGIN_FROM_MAIN && resultCode == RESULT_LOGIN_SUCCESS){
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();

            EventBus.getDefault().post(new UserInfoRefreshEvent());
            EventBus.getDefault().post(new NavEvent(R.id.layout_main_bottom_bar_sing));
        }
    }
}

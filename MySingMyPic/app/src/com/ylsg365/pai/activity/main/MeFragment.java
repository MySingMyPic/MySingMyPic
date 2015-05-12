package com.ylsg365.pai.activity.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.dialog.VipOpenPromptFragment;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.event.UserInfoRefreshEvent;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.activity.base.BaseFragment;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.util.BitmapUtils;
import com.ylsg365.pai.util.BmpUtils;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import de.greenrobot.event.EventBus;


public class MeFragment extends BaseFragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //粉丝数
    private TextView userFansNumTextView;
    //被关注数
    private TextView userAttentionNumTextView;
    //新鲜事数量
    private TextView userNewInfoNumTextView;
    //账户创建时间
    private TextView userCTimeTextView;
    //用户地区
    private TextView userAreaTextView;
    //用户昵称
    private TextView userNickNameTextView;
    //用户头像
    private ImageView userHeadImageView;
    //titlebar上的软件设置按钮
    private TextView settingTextView;

    private View myOriginalView;
    private View lookmeView;
    private View myMoneyView;


    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.purple));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_me, container, false);
//        Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.s);
//        Drawable bgDrawable = BitmapUtils.BoxBlurFilter(bgBitmap);

//        rootView.findViewById(R.id.layout_me_bg).setBackgroundDrawable(bgDrawable);

//        new ImageView(getActivity()).setBackgroundDrawable(bgDrawable);
        // Inflate the layout for this fragment

        EventBus.getDefault().register(this);
        init();

        return rootView;
    }

     protected void init(){
        loadViews();
        setupListeners();
        initViews();
    }

    protected void loadViews(){
        userCTimeTextView = (TextView)rootView.findViewById(R.id.text_user_ctime);
        userAreaTextView = (TextView)rootView.findViewById(R.id.text_user_area);
        userNickNameTextView = (TextView)rootView.findViewById(R.id.text_user_nickName);
        userFansNumTextView = (TextView)rootView.findViewById(R.id.text_user_fansNum);
        userAttentionNumTextView = (TextView)rootView.findViewById(R.id.text_user_attentionNum);
        userHeadImageView = (ImageView)rootView.findViewById(R.id.img_user_avatar);
        settingTextView = (TextView)rootView.findViewById(R.id.text_right);
        userNewInfoNumTextView = (TextView)rootView.findViewById(R.id.text_user_newInfoNum);

        myOriginalView = rootView.findViewById(R.id.layout_myOriginal);
        lookmeView = rootView.findViewById(R.id.layout_lookme);
        myMoneyView = rootView.findViewById(R.id.layout_myMoney);


    }

    protected void setupListeners(){
        userHeadImageView.setOnClickListener(this);
        settingTextView.setOnClickListener(this);

        myOriginalView.setOnClickListener(this);
        lookmeView.setOnClickListener(this);
        myMoneyView.setOnClickListener(this);

        rootView.findViewById(R.id.layout_newsInfo).setOnClickListener(this);
        rootView.findViewById(R.id.layout_myCollection).setOnClickListener(this);
        rootView.findViewById(R.id.layout_song_purchased).setOnClickListener(this);
        rootView.findViewById(R.id.layout_attention).setOnClickListener(this);
        rootView.findViewById(R.id.layout_myWorks).setOnClickListener(this);
        rootView.findViewById(R.id.layout_vip_open).setOnClickListener(this);

        userFansNumTextView.setOnClickListener(this);
        userNewInfoNumTextView.setOnClickListener(this);
        userAttentionNumTextView.setOnClickListener(this);
    }

    protected void initViews(){
        rootView.findViewById(R.id.text_toolbar_left).setVisibility(View.GONE);
        setToolbarRightButtonText("软件设置");
        if(UserService.isLogin()){
            User user = UserService.getUser();
            setToolbarTitle("我的空间");
            userNickNameTextView.setText(user.getNickName());
            userFansNumTextView.setText(String.format("粉丝（%d）", user.getFansNum()));
            userAttentionNumTextView.setText(String.format("关注（%d）", user.getAttentionNUm()));
            userNewInfoNumTextView.setText(String.format("新鲜事（%d）", user.getNewInfoNum()));
            userCTimeTextView.setText(user.getcTime().split(" ")[0]);
            userAreaTextView.setText(user.getArea());

            LogUtil.logd("initViews", user.getHeadImg());

            if(!StringUtils.isEmpty(user.getHeadImg())){
                ImageLoader.getInstance().displayImage(user.getHeadImg(), userHeadImageView, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override

                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        Drawable bgDrawable = BitmapUtils.BoxBlurFilter(bitmap);

                        rootView.findViewById(R.id.layout_me_bg).setBackgroundDrawable(bgDrawable);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }

        }else {
            setToolbarTitle("请先登录");
        }
    }

    public void onEvent(UserInfoRefreshEvent userInfoRefreshEvent){
        initViews();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_user_avatar:
                toUserInfoPage();
                break;
            case R.id.text_right:
                toSettingPage();
                break;
            case R.id.layout_myOriginal:
                toMyOriginalPage();
                break;
            case R.id.layout_lookme:
                toLookmePage();
                break;
            case R.id.layout_myMoney:
                toMyMoneyPage();
                break;
            case R.id.layout_newsInfo:
                toNewsInfoByNicePage();
                break;
            case R.id.layout_myCollection:
                toMyCollectionPage();
                break;
            case R.id.layout_song_purchased:  //已购作品
                toAlreadyPurchasePage();
                break;
            case R.id.layout_attention:   //悄悄关注
                toMyAttentionsPage();
                break;
            case R.id.layout_myWorks:
                toMyWorksPage();
                break;
            case R.id.text_user_fansNum:
                toMyFansListPage();
                break;
            case R.id.text_user_newInfoNum:
                toMyNewsInfoListPage();
                break;
            case R.id.layout_vip_open:
                showOpenVipDialog();
                break;
            case R.id.text_user_attentionNum:
                toMyAttentionListPage();
                break;
        }
    }

    private void toAlreadyPurchasePage() {
        NavHelper.toAlreadyPurchaseActivity(getActivity());
    }

    private void toMyAttentionsPage() {
        NavHelper.toMyAttentionsActivity(getActivity());
    }

    private void toUserInfoPage(){
        NavHelper.toUserInfoPage(getActivity());
    }
    private void toSettingPage(){
        NavHelper.toSettingPage(getActivity());
    }
    private void toMyOriginalPage(){
        NavHelper.toOriginalBaseListPage(getActivity(), NavHelper.REQUEST_MY_ORIGINAL);
    }
    private void toLookmePage(){
        NavHelper.toLookMeListPage(getActivity());
    }
    private void toMyMoneyPage(){
        NavHelper.toMyMoneyPage(getActivity());
    }
    private void toNewsInfoByNicePage(){
        NavHelper.toNiceNewsInfoListPage(getActivity());
    }
    private void toMyCollectionPage(){
        NavHelper.toMyCollectionPage(getActivity());
    }
    private void toMyWorksPage(){
        NavHelper.toMyWorksPage(getActivity());
    }
    private void toMyFansListPage(){
        NavHelper.toMyFansListPage(getActivity());
    }
    private void toMyNewsInfoListPage(){
        NavHelper.toMyNewsInfoListPage(getActivity());
    }

    private void showOpenVipDialog(){
        VipOpenPromptFragment vipFragment = VipOpenPromptFragment.newInstance("");
        vipFragment.show(getActivity().getSupportFragmentManager(), "VipOpenPromptFragment");
        vipFragment.setOnMyOkClickListener(new VipOpenPromptFragment.OnMyOkClickListener() {
            @Override
            public void onMyOkClick() {
                NavHelper.toOpenVipSelectActivity(MeFragment.this.getActivity());
            }
        });
    }
    private void toMyAttentionListPage(){
        NavHelper.toAttentionListPage(getActivity());}
    @Override
    public void onResume() {
        super.onResume();
        initViews();

//        ImageLoader.getInstance().displayImage("file:///"+FileUtils.path+FileUtils.headPath, userHeadImageView);
    }
}

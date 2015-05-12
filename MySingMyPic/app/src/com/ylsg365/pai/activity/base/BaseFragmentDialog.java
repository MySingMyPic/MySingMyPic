package com.ylsg365.pai.activity.base;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;


public class BaseFragmentDialog extends DialogFragment {
    protected View rootView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//如果setCancelable()中参数为true，若点击dialog覆盖不到的activity的空白或者按返回键，则进行cancel，状态检测依次onCancel()和onDismiss()。如参数为false，则按空白处或返回键无反应。缺省为true 
        setCancelable(true); 
        //可以设置dialog的显示风格，如style为STYLE_NO_TITLE，将被显示title。遗憾的是，我没有在DialogFragment中找到设置title内容的方法。theme为0，表示由系统选择合适的theme。
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style,theme);  
	}

    protected void init(){
        loadViews();
        initViews();
        setupListeners();
    }

    protected void loadViews(){

    };

    protected void initViews(){

    };

    protected void setupListeners(){

    };
}

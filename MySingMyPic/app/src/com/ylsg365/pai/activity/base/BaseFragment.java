package com.ylsg365.pai.activity.base;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.ylsg365.pai.R;


public class BaseFragment extends DialogFragment {
    protected View rootView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			((BaseActivity)getActivity()).setTranslucentStatus(true);
//
//
//			SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
//			tintManager.setStatusBarTintEnabled(true);
//			tintManager.setStatusBarTintResource(0);//状态栏无背景
//		}


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

	protected void setToolbarRightButtonText(String title){
		TextView toolbarRightButtonTextView = (TextView)rootView.findViewById(R.id.text_right);
		if(toolbarRightButtonTextView != null){
            toolbarRightButtonTextView.setText(title);
		}
	}
	protected void setToolbarTitle(String title){
		TextView toolbarTitleTextView = (TextView)rootView.findViewById(R.id.toolbar_title);
		if(toolbarTitleTextView != null){
            toolbarTitleTextView.setText(title);
		}
	}


}

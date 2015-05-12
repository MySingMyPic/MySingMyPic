package com.ylsg365.pai.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseFragmentDialog;

/**
 * Order: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart
 * -> onResume
 */
public class NoticeDialodFragment extends BaseFragmentDialog {
	TextView noticeTextView;
    int noticeResId;

	public static NoticeDialodFragment newInstance(int noticeResId) {

		NoticeDialodFragment f = new NoticeDialodFragment();
		Bundle args = new Bundle();
        args.putInt("noticeResId", noticeResId);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

        noticeResId = getArguments().getInt("noticeResId");
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.dialog_notice, container, false);

		initViews();
		return rootView;
	}

	
	protected void initViews(){
        noticeTextView = (TextView)rootView.findViewById(R.id.text_notice);
        noticeTextView.setText(noticeResId);
	}


}

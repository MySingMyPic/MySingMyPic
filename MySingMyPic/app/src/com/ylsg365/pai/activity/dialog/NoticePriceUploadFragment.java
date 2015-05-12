package com.ylsg365.pai.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseFragmentDialog;

/**
 * Order: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart
 * -> onResume
 */
public class NoticePriceUploadFragment extends BaseFragmentDialog implements View.OnClickListener{
    private Button cancelButton;


	public static NoticePriceUploadFragment newInstance() {

		NoticePriceUploadFragment f = new NoticePriceUploadFragment();
		Bundle args = new Bundle();
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

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.dialog_notice_price_upload, container, false);

//		initViews();
//        setupListeners();
		return rootView;
	}

	
	protected void initViews(){
	}

    protected void setupListeners(){
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}

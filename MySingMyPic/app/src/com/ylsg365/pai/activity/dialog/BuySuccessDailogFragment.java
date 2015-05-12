package com.ylsg365.pai.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseFragmentDialog;

/**
 * Order: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart
 * -> onResume
 */
public class BuySuccessDailogFragment extends BaseFragmentDialog implements View.OnClickListener{
    private TextView text_notice;
    private static String str;

	public static BuySuccessDailogFragment newInstance(String text) {

		BuySuccessDailogFragment f = new BuySuccessDailogFragment();
		Bundle args = new Bundle();
		f.setArguments(args);
        str = text;
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
		rootView = inflater.inflate(R.layout.dialog_buy_success, container, false);
        text_notice = (TextView) rootView.findViewById(R.id.text_notice);
        initText();
		return rootView;
	}

	
	public void initText(){
        text_notice.setText(str);
	}


    @Override
    public void onClick(View v) {
        dismiss();
    }
}

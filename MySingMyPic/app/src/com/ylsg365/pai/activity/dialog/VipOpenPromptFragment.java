package com.ylsg365.pai.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseFragmentDialog;
import com.ylsg365.pai.activity.user.MoneyManagerActivity;

/**
 * Order: onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart
 * -> onResume
 */
public class VipOpenPromptFragment extends BaseFragmentDialog implements View.OnClickListener{
	TextView noticeTextView;
    String notice;
    OnMyOkClickListener mOnMyOkClickListener;

	public static VipOpenPromptFragment newInstance(String notice) {

		VipOpenPromptFragment f = new VipOpenPromptFragment();
		Bundle args = new Bundle();
        args.putString("notice", notice);
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

        notice = getArguments().getString("notice");
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.dialog_vip_open_prompt, container, false);

		init();
		return rootView;
	}


	@Override
	protected void setupListeners() {
		rootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.btn_ok).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_cancel:
				dismiss();
               break;
            case R.id.btn_ok:
                if(mOnMyOkClickListener != null){
                    mOnMyOkClickListener.onMyOkClick();
                }
                dismiss();
                break;
		}
	}

    public void setOnMyOkClickListener(OnMyOkClickListener onMyOkClickListener){
        mOnMyOkClickListener = onMyOkClickListener;
    }

    public interface  OnMyOkClickListener{
        public abstract void onMyOkClick();
    }
}

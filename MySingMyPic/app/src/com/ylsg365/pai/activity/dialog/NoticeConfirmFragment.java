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
public class NoticeConfirmFragment extends BaseFragmentDialog implements View.OnClickListener {
	TextView noticeTextView;
    String notice;
    OnMyClickListener mOnMyClickListener;

	public static NoticeConfirmFragment newInstance(String notice) {

		NoticeConfirmFragment f = new NoticeConfirmFragment();
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
		rootView = inflater.inflate(R.layout.dialog_confirm, container, false);

		initViews();
		return rootView;
	}

	
	protected void initViews(){
        noticeTextView = (TextView)rootView.findViewById(R.id.text_notice);
        noticeTextView.setText(notice);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.btn_ok).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(mOnMyClickListener != null){
            switch (v.getId()){
                case R.id.btn_cancel:
                    mOnMyClickListener.onMyCancelClick();
                    break;
                case R.id.btn_ok:
                    mOnMyClickListener.onMyOkClick();
                    break;
            }
        }
        dismiss();
    }

    public void setOnMyClickListener(NoticeConfirmFragment.OnMyClickListener onMyClickListener){
        mOnMyClickListener = onMyClickListener;
    }

    public interface  OnMyClickListener{
        public abstract void onMyOkClick();
        public abstract void onMyCancelClick();
    }
}

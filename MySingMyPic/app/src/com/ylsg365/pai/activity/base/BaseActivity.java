package com.ylsg365.pai.activity.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconsFragment;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;


public abstract class BaseActivity extends ActionBarActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    protected void init() {
        loadViews();
        initViews();
        setupListeners();
    }

    protected void loadViews() {
    }


    protected void initViews() {

    }


    protected void setupListeners() {

    }

    ;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;

    public void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);

            leftTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHelper.finish(BaseActivity.this);
                }
            });
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }


    private ProgressDialog getLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(BaseActivity.this);
            progressDialog.setTitle("notice");
            progressDialog.setMessage("please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
        }

        return progressDialog;
    }

    public void showLoading() {
        getLoading().show();
    }

    public void hideLoading() {
        getLoading().dismiss();
    }

    private View mEmptyView;

    protected void checkAdapterIsEmpty(RecyclerView.Adapter adapter) {
        mEmptyView = findViewById(R.id.empty_view);
        if (mEmptyView == null) return;

        if (adapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    protected void setToolbarTitle(String title){
        TextView toolbarTitleTextView = (TextView)findViewById(R.id.toolbar_title);
        if(toolbarTitleTextView != null){
            toolbarTitleTextView.setText(title);
        }
    }

}

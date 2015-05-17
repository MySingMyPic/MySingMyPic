package com.ylsg365.pai.customview;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.adapter.DialogListViewAdapter;
import com.ylsg365.pai.listener.DialogListViewListener;
import com.ylsg365.pai.listener.DialogMessageListener;
import com.ylsg365.pai.listener.DialogMsgAndPriceListener;
import com.ylsg365.pai.listener.DialogShareListener;

import java.util.List;

/**
 * Created by ann on 2015-05-07.
 */
public class DialogView {
    Activity act;
    Dialog dialog;
    Dialog dialog2;
    Dialog dialog3;
    ListView listView;
    TextView title;
    DialogListViewAdapter adapter;

    TextView ok,cancel,info;
    EditText msg,price;
    ImageView weixin,weibo;

    public void createListViewDialog(Activity act, List<String> list,
                             final DialogListViewListener listener) {
        this.act = act;
        dialog = new Dialog(act, R.style.myDialogTheme);
        View root2 = LayoutInflater.from(act).inflate(R.layout.dialog_layout1,
                null);
        listView=(ListView)root2.findViewById(R.id.listView);
        adapter=new DialogListViewAdapter(act,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.select(position);
                dialog.dismiss();
            }
        });
        dialog.setContentView(root2);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public void createListViewWithTitleDialog(Activity act,String titleStr, List<String> list,
                                     final DialogListViewListener listener) {
        this.act = act;
        dialog = new Dialog(act, R.style.myDialogTheme);
        View root2 = LayoutInflater.from(act).inflate(R.layout.dialog_layout4,
                null);
        title=(TextView)root2.findViewById(R.id.title);
        title.setText(titleStr);
        listView=(ListView)root2.findViewById(R.id.listView);
        adapter=new DialogListViewAdapter(act,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.select(position);
                dialog.dismiss();
            }
        });
        dialog.setContentView(root2);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public void dismissDialog()
    {
        dialog.dismiss();
    }

    public void createMessageDialog(Activity act,
                                     final DialogMessageListener listener) {
        this.act = act;
        dialog2 = new Dialog(act, R.style.myDialogTheme);
        View root2 = LayoutInflater.from(act).inflate(R.layout.dialog_layout2,
                null);

        msg=(EditText)root2.findViewById(R.id.msg);
        price=(EditText)root2.findViewById(R.id.price);
        price.setVisibility(View.GONE);

        ok=(TextView)root2.findViewById(R.id.ok);
        cancel=(TextView)root2.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getMessage(msg.getText().toString());
                dialog2.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        dialog2.setContentView(root2);
        dialog2.setCanceledOnTouchOutside(false);

        dialog2.show();
    }

    public void createMsgAndPriceDialog(Activity act,
                                     final DialogMsgAndPriceListener listener) {
        this.act = act;
        dialog2 = new Dialog(act, R.style.myDialogTheme);
        View root2 = LayoutInflater.from(act).inflate(R.layout.dialog_layout2,
                null);

        msg=(EditText)root2.findViewById(R.id.msg);
        price=(EditText)root2.findViewById(R.id.price);

        ok=(TextView)root2.findViewById(R.id.ok);
        cancel=(TextView)root2.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getMessage(msg.getText().toString(),Integer.parseInt(price.getText().toString().trim()));
                dialog2.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        dialog2.setContentView(root2);
        dialog2.setCanceledOnTouchOutside(false);

        dialog2.show();
    }

    public void createShareDialog(Activity act,
                                        final DialogShareListener listener) {
        this.act = act;
        dialog3 = new Dialog(act, R.style.myDialogTheme);
        View root2 = LayoutInflater.from(act).inflate(R.layout.dialog_layout3,
                null);

        weixin=(ImageView)root2.findViewById(R.id.weixin);
        weibo=(ImageView)root2.findViewById(R.id.weibo);
        cancel=(TextView)root2.findViewById(R.id.cancel);

        weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.select(0);
                dialog3.dismiss();
            }
        });

        weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.select(1);
                dialog3.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();
            }
        });

        dialog3.setContentView(root2);
        dialog3.setCanceledOnTouchOutside(false);

        dialog3.show();
    }

    public void createInfoDialog(Activity act,String infoStr) {
        this.act = act;
        dialog = new Dialog(act, R.style.myDialogTheme);
        View root2 = LayoutInflater.from(act).inflate(R.layout.dialog_layout5,
                null);

        info=(TextView)root2.findViewById(R.id.info);
        info.setText(infoStr);

        dialog.setContentView(root2);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public void changeInfo(String infoStr)
    {
        if(info!=null)
            info.setText(infoStr);
    }
}

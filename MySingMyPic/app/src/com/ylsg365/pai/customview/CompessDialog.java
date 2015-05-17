package com.ylsg365.pai.customview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.ylsg365.pai.R;

public class CompessDialog extends Activity {

    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout5);

        info=(TextView)findViewById(R.id.info);
        info.setText("合成中，请稍等\n(0%)");


        new Thread(){
            @Override
            public void run()
            {
                for(int i=1;i<101;i++)
                {
                    try {
                        sleep(500);
                        Message msg=new Message();
                        msg.what=0;
                        msg.obj=i;
                        handler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(1);
            }
        }.start();
    }


    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what==0)
            {
                int info=(Integer)msg.obj;
                changeInfo("合成中，请稍等\n("+info+"%)");
            }
            else if(msg.what==1)
            {
                finish();
            }
        }
    };

    public void changeInfo(String infoStr)
    {

            info.setText(infoStr);
    }
}

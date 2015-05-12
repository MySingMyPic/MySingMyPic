package com.ylsg365.pai.app;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by ylsg365 on 2015-03-08.
 */
public class UIHelper {

    public static void showToast(int resId){
        Toast.makeText(YinApplication.getInstance(), YinApplication.getInstance().getText(resId), Toast.LENGTH_SHORT).show();
    }
    public static void showToast(String msg){
        Toast.makeText(YinApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager) YinApplication.getInstance().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(
                InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

}

package com.c2s.batterychargingstatus.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by user on 9/27/17.
 */

public class ToastHelper {
    public static void showToastLengthShort(final Context context, final String msg,boolean isFromDoInBackground){

        if(isFromDoInBackground){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToastLengthLong(final Context context, final String msg,boolean isFromDoInBackground){

        if(isFromDoInBackground){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        }
    }
}

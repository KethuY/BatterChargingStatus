package com.c2s.batterychargingstatus.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by satya on 07-Oct-17.
 */

public class DateHelper  {

    public static String getCurrentDate() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
       // Log.e(TAG, "Current date" + df.format(c.getTime()));
        return df.format(c.getTime());
    }

    public static Date getDateFromString(String s){
        try {
            if (null != s) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
                return df.parse(s);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }
}

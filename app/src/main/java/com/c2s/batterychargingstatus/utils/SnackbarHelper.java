package com.c2s.batterychargingstatus.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.c2s.batterychargingstatus.R;

/**
 * Created by user on 9/27/17.
 */

public class SnackbarHelper {
    public static void displaySnackbarLengthShort (View view, Context context, String s) {
        Snackbar snack = Snackbar.make(view, s, Snackbar.LENGTH_SHORT);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = (TextView) sbview.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        snack.show();
    }

    public static void displaySnackbarLengthLong (View view, Context context, String s) {
        Snackbar snack = Snackbar.make(view, s, Snackbar.LENGTH_LONG);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = (TextView) sbview.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        snack.show();
    }
}

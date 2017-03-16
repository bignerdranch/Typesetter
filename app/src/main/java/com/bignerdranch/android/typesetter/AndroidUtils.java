package com.bignerdranch.android.typesetter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AndroidUtils {

    public static boolean IS_LOLLIPOP_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String formatFloat(float floatValue) {
        if(floatValue == (int) floatValue) {
            return String.format("%d", (int) floatValue);
        } else {
            return String.format("%s", floatValue);
        }
    }
}

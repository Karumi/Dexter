package com.karumi.dexter.listener;

import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarUtils {

    public static void show(View view, String text, int duration, String buttonText,
            View.OnClickListener onButtonClickListener,
            BaseTransientBottomBar.BaseCallback<Snackbar> snackbarCallback) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        if (buttonText != null && onButtonClickListener != null) {
            snackbar.setAction(buttonText, onButtonClickListener);
        }
        if (snackbarCallback != null) {
            snackbar.addCallback(snackbarCallback);
        }
        snackbar.show();
    }
}

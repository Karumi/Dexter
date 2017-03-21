package com.karumi.dexter.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.sample.permission.PermissionConnector;
import com.karumi.dexter.sample.permission.RequestMultiplePermissionListener;
import com.karumi.dexter.sample.permission.RequestSinglePermissionListener;

/**
 * @author : hafiq on 21/03/2017.
 */

public class BaseActivity extends AppCompatActivity {
    protected RequestSinglePermissionListener feedbackViewPermissionListener;
    protected RequestMultiplePermissionListener feedbackViewMultiplePermissionListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedbackViewMultiplePermissionListener = new RequestMultiplePermissionListener(this);
        feedbackViewPermissionListener = new RequestSinglePermissionListener(this);
    }

    public void setPermission(PermissionConnector connector){
        feedbackViewMultiplePermissionListener.setConnector(connector);
        feedbackViewPermissionListener.setConnector(connector);
    }

    public void setDexterMultiplePermissions(Activity activity, MultiplePermissionsListener permissionsListener, String... permissions){
        Dexter.withActivity(activity)
                .withPermissions(permissions)
                .withListener(permissionsListener)
                .check();
    }

    public void setDexterPermission(Activity activity, PermissionListener permissionsListener, String permissions){
        Dexter.withActivity(activity)
                .withPermission(permissions)
                .withListener(permissionsListener)
                .check();
    }

    public DialogOnDeniedPermissionListener dialogPermission(Activity activity, String title, String message){
        return DialogOnDeniedPermissionListener.Builder.withContext(activity)
                .withTitle(title)
                .withMessage(message)
                .withButtonText(android.R.string.ok)
                .withIcon(R.mipmap.ic_logo_karumi_no_text)
                .build();
    }

    public DialogOnAnyDeniedMultiplePermissionsListener dialogMultiplePermission(Activity activity, String title, String message){
        return DialogOnAnyDeniedMultiplePermissionsListener.Builder.withContext(activity)
                .withTitle(title)
                .withMessage(message)
                .withButtonText(android.R.string.ok)
                .withIcon(R.mipmap.ic_logo_karumi_no_text)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }
}

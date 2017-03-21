package com.karumi.dexter.sample.permission;

import android.app.Activity;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;


/**
 * @author : hafiq on 05/03/2017.
 */

public class RequestSinglePermissionListener implements PermissionListener,PermissionRequestErrorListener {

    private PermissionConnector connector;
    private Activity activity;

    public RequestSinglePermissionListener(Activity activity) {
        this.activity = activity;
    }

    public void setConnector(PermissionConnector connector) {
        this.connector = connector;
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        if (connector == null) return;
        connector.showPermissionGranted(response.getPermissionName());
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        if (connector == null) return;
        connector.showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        if (connector == null) return;
        connector.showPermissionRationale(permission,token);
    }

    @Override
    public void onError(DexterError error) {
        if (connector == null) return;
        connector.showPermissionError(error);
    }
}

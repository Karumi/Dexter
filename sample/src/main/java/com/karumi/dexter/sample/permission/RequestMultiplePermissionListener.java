package com.karumi.dexter.sample.permission;

import android.app.Activity;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

/**
 * @author : hafiq on 05/03/2017.
 */

public class RequestMultiplePermissionListener implements MultiplePermissionsListener,PermissionRequestErrorListener {

    private PermissionConnector connector;
    private Activity activity;

    public RequestMultiplePermissionListener(Activity activity) {
        this.activity = activity;
    }

    public void setConnector(PermissionConnector connector) {
        this.connector = connector;
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (connector == null) return;

        for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
            connector.showPermissionGranted(response.getPermissionName());
        }

        for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
            connector.showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
        }

        connector.isAllPermissionGranted(report.areAllPermissionsGranted());
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        if (connector == null) return;

        for (PermissionRequest permissionRequest : permissions) {
            connector.showPermissionRationale(permissionRequest,token);
        }
    }

    @Override
    public void onError(DexterError error) {
        if (connector == null) return;
        connector.showPermissionError(error);
    }
}

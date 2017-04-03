package com.karumi.dexter.sample.permission;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;

/**
 * @author : hafiq on 05/03/2017.
 */

public interface PermissionConnector {

    void showPermissionGranted(String permissionName);
    void isAllPermissionGranted(boolean isAllGranted);
    void showPermissionDenied(String permission, boolean isPermanentlyDenied);
    void showPermissionRationale(PermissionRequest permissions, PermissionToken token);
    void showPermissionError(DexterError error);
}

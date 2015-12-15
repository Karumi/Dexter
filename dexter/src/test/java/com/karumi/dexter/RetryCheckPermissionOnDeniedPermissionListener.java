/*
 * Copyright (C) 2015 Karumi.
 */

package com.karumi.dexter;

import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.EmptyPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class RetryCheckPermissionOnDeniedPermissionListener extends EmptyPermissionListener {

  private final PermissionListener listener;
  private final CheckPermissionAction checkPermission;

  public RetryCheckPermissionOnDeniedPermissionListener(PermissionListener listener,
      CheckPermissionAction checkPermission) {
    this.listener = listener;
    this.checkPermission = checkPermission;
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    checkPermission.check(listener, response.getPermissionName());
  }

  @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
      PermissionToken token) {
    token.continuePermissionRequest();
  }

  public interface CheckPermissionAction {
    void check(PermissionListener listener, String permission);
  }
}

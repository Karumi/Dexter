package com.karumi.dexter.listener;

import android.support.annotation.NonNull;

/**
 * If a permission was denied, an instance of this class will be returned
 * in the callback.
 */
public final class PermissionDeniedResponse {

  private final PermissionRequest requestedPermission;
  private final boolean permanentlyDenied;

  public PermissionDeniedResponse(@NonNull PermissionRequest requestedPermission, boolean permanentlyDenied) {
    this.requestedPermission = requestedPermission;
    this.permanentlyDenied = permanentlyDenied;
  }

  /**
   * Builds a new instance of PermissionDeniedResponse from a given permission string
   * and a permanently-denied boolean flag
   *
   * @param permission
   * @return
   */
  public static PermissionDeniedResponse from(@NonNull String permission, boolean permanentlyDenied) {
    return new PermissionDeniedResponse(new PermissionRequest(permission), permanentlyDenied);
  }

  public PermissionRequest getRequestedPermission() {
    return requestedPermission;
  }

  public boolean isPermanentlyDenied() {
    return permanentlyDenied;
  }
}

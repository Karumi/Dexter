package com.karumi.dexter.listener;

import android.support.annotation.NonNull;

/**
 * If a permission was granted, an instance of this class will be returned
 * in the callback.
 */
public final class PermissionGrantedResponse {

  private final PermissionRequest requestedPermission;

  public PermissionGrantedResponse(@NonNull PermissionRequest requestedPermission) {
    this.requestedPermission = requestedPermission;
  }

  /**
   * Builds a new instance of PermissionGrantedResponse from a given permission string
   *
   * @param permission
   * @return
   */
  public static PermissionGrantedResponse from(@NonNull String permission) {
    return new PermissionGrantedResponse(new PermissionRequest(permission));
  }

  public PermissionRequest getRequestedPermission() {
    return requestedPermission;
  }
}

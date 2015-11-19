package com.karumi.dexter.listener;

import android.support.annotation.NonNull;

/**
 * Wrapper class for a permission request
 */
public final class PermissionRequest {
  private final String permission;

  public PermissionRequest(@NonNull String permission) {
    this.permission = permission;
  }

  /**
   * One of the values found in {@link android.Manifest.permission}
   *
   * @return
   */
  public String getPermission() {
    return permission;
  }

  @Override public String toString() {
    return "Permission: " + permission;
  }
}

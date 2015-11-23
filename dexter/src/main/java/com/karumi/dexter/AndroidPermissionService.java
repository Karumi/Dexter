/*
 * Copyright (C) 2015 Karumi.
 */

package com.karumi.dexter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Wrapper class for all the static calls to the Android permission system
 */
class AndroidPermissionService {

  /**
   * @see ContextCompat#checkSelfPermission
   */
  public int checkSelfPermission(Activity activity, String permission) {
    return ContextCompat.checkSelfPermission(activity, permission);
  }

  /**
   * @see ActivityCompat#requestPermissions
   */
  public void requestPermissions(final @NonNull Activity activity,
      final @NonNull String[] permissions, final int requestCode) {
    ActivityCompat.requestPermissions(activity, permissions, requestCode);
  }

  /**
   * @see ActivityCompat#shouldShowRequestPermissionRationale
   */
  public boolean shouldShowRequestPermissionRationale(@NonNull Activity activity,
      @NonNull String permission) {
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
  }
}

/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.dexter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionListener;
import com.karumi.dexter.listener.PermissionRequest;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Inner implementation of a dexter instance
 */
final class DexterInstance {

  private String permission;
  private Context context;
  private Activity activity;
  private PermissionListener listener;
  private AtomicBoolean isRequestingPermission = new AtomicBoolean(false);

  DexterInstance(Context context) {
    this.context = context;
  }

  /**
   * Checks the state of a specific permission reporting it when ready to the listener
   *
   * @param permission One of the values found in {@link android.Manifest.permission}
   * @param listener The class that will be reported when the state of the permission is ready
   */
  void checkPermission(String permission, PermissionListener listener) {
    if (isRequestingPermission.getAndSet(true)) {
      throw new IllegalStateException(
          "Only one permission request at a time. Currently handling permission: ["
              + this.permission + "]");
    }

    this.permission = permission;
    this.listener = listener;

    Intent intent = new Intent(context, DexterActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  /**
   * Method called whenever the inner activity has been created and is ready to be used
   */
  void onActivityCreated(Activity activity) {
    this.activity = activity;

    int permissionState = ContextCompat.checkSelfPermission(activity, permission);
    switch (permissionState) {
      case PackageManager.PERMISSION_DENIED:
        handleDeniedPermission(permission);
        break;
      case PackageManager.PERMISSION_GRANTED:
      default:
        finishWithGrantedPermission(permission);
        break;
    }
  }

  /**
   * Method called whenever the permission has been granted by the user
   */
  void onPermissionRequestGranted() {
    finishWithGrantedPermission(permission);
  }

  /**
   * Method called whenever the permission has been denied by the user
   */
  void onPermissionRequestDenied() {
    finishWithDeniedPermission(permission);
  }

  /**
   * Method called when the user has been informed with the rationale and agrees to continue
   * with the permission request process
   */
  void onContinuePermissionRequest(String permission) {
    requestPermission(permission);
  }

  /**
   * Method called when the user has been informed with the rationale and decides to cancel
   * the permission request process
   */
  void onCancelPermissionRequest(String permission) {
    finishWithDeniedPermission(permission);
  }

  /**
   * Starts the native request permission process
   */
  void requestPermission(String permission) {
    int permissionCode = getPermissionCodeForPermission(permission);
    ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionCode);
  }

  private void handleDeniedPermission(String permission) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
      PermissionRationaleToken permissionToken = new PermissionRationaleToken(this, permission);
      listener.onPermissionRationaleShouldBeShown(new PermissionRequest(permission), permissionToken);
    } else {
      requestPermission(permission);
    }
  }

  private void finishWithGrantedPermission(String permission) {
    activity.finish();
    listener.onPermissionGranted(PermissionGrantedResponse.from(permission));
    isRequestingPermission.set(false);
  }

  private void finishWithDeniedPermission(String permission) {
    activity.finish();
    listener.onPermissionDenied(PermissionDeniedResponse.from(permission,
        !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)));
    isRequestingPermission.set(false);
  }

  private int getPermissionCodeForPermission(String permission) {
    return Math.abs(permission.hashCode() % Integer.MAX_VALUE);
  }
}

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
import android.util.SparseArray;
import com.karumi.dexter.listener.PermissionListener;

final class DexterInstance {

  private final SparseArray<String> permissionCodes = new SparseArray<>();
  private String permission;
  private Context context;
  private Activity activity;
  private PermissionListener listener;

  DexterInstance(Context context) {
    this.context = context;
  }

  void checkPermission(String permission, PermissionListener listener) {
    this.permission = permission;
    this.listener = listener;

    Intent intent = new Intent(context, DexterActivity.class);
    context.startActivity(intent);
  }

  void onActivityCreated(Activity activity) {
    this.activity = activity;

    int permissionState = ContextCompat.checkSelfPermission(activity, permission);
    switch (permissionState) {
      case PackageManager.PERMISSION_DENIED:
        handleDeniedPermission(permission);
        break;
      case PackageManager.PERMISSION_GRANTED:
      default:
        listener.onPermissionGranted(permission);
        activity.finish();
        break;
    }
  }

  void onPermissionRequestGranted(int permissionCode) {
    String permission = permissionCodes.get(permissionCode);
    listener.onPermissionGranted(permission);
    activity.finish();
  }

  void onPermissionRequestDenied(int permissionCode) {
    String permission = permissionCodes.get(permissionCode);
    listener.onPermissionDenied(permission);
    activity.finish();
  }

  void requestPermission(String permission) {
    int permissionCode = getPermissionCodeForPermission(permission);
    permissionCodes.put(permissionCode, permission);
    ActivityCompat.requestPermissions(activity, new String[] {permission}, permissionCode);
  }

  void cleanPermission(String permission) {
    permissionCodes.delete(permissionCodes.indexOfValue(permission));
    listener.onPermissionDenied(permission);
  }

  private void handleDeniedPermission(String permission) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
      listener.onPermissionRationaleShouldBeShown(permission,
          new PermissionRationaleToken(this, permission));
    } else {
      requestPermission(permission);
    }
  }

  private int getPermissionCodeForPermission(String permission) {
    return permission.hashCode();
  }
}

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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Inner implementation of a dexter instance
 */
final class DexterInstance {

  private Collection<String> pendingPermissions;
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
    checkPermissions(Collections.singleton(permission), listener);
  }

  /**
   * Checks the state of a group of permissions reporting it when ready to the listener
   *
   * @param permissions Collection of values found in {@link android.Manifest.permission}
   * @param listener The class that will be reported when the state of the permissions are ready
   */
  void checkPermissions(Collection<String> permissions, PermissionListener listener) {
    assertOneDexterRequestOngoing();
    assertRequestSomePermission(permissions);

    this.pendingPermissions = new LinkedList<>(permissions);
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

    Collection<String> deniedPermissions = new LinkedList<>();
    Collection<String> grantedPermissions = new LinkedList<>();

    for (String permission : pendingPermissions) {
      int permissionState = ContextCompat.checkSelfPermission(activity, permission);
      switch (permissionState) {
        case PackageManager.PERMISSION_DENIED:
          deniedPermissions.add(permission);
          break;
        case PackageManager.PERMISSION_GRANTED:
        default:
          grantedPermissions.add(permission);
          break;
      }
    }

    handleDeniedPermissions(deniedPermissions);
    finishWithGrantedPermissions(grantedPermissions);
  }

  /**
   * Method called whenever the permission has been granted by the user
   */
  void onPermissionRequestGranted(Collection<String> permissions) {
    finishWithGrantedPermissions(permissions);
  }

  /**
   * Method called whenever the permission has been denied by the user
   */
  void onPermissionRequestDenied(Collection<String> permissions) {
    finishWithDeniedPermission(permissions);
  }

  /**
   * Method called when the user has been informed with the rationale and agrees to continue
   * with the permission request process
   */
  void onContinuePermissionRequest() {
    requestPermissions(pendingPermissions);
  }

  /**
   * Method called when the user has been informed with the rationale and decides to cancel
   * the permission request process
   */
  void onCancelPermissionRequest() {
    finishWithDeniedPermission(pendingPermissions);
  }

  /**
   * Starts the native request permissions process
   */
  void requestPermissions(Collection<String> permissions) {
    int requestCode = getRequestCodeForPermissions(permissions);
    ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]),
        requestCode);
  }

  private void handleDeniedPermissions(Collection<String> permissions) {
    if (permissions.isEmpty()) {
      return;
    }

    Collection<PermissionRequest> shouldShowRequestRationalePermissions = new LinkedList<>();

    for (String permission : permissions) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
        shouldShowRequestRationalePermissions.add(new PermissionRequest(permission));
      }
    }

    if (shouldShowRequestRationalePermissions.isEmpty()) {
      requestPermissions(permissions);
    } else {
      PermissionRationaleToken permissionToken = new PermissionRationaleToken(this);
      listener.onPermissionRationaleShouldBeShown(shouldShowRequestRationalePermissions,
          permissionToken);
    }
  }

  private void finishWithGrantedPermissions(Collection<String> permissions) {
    for (String permission : permissions) {
      listener.onPermissionGranted(PermissionGrantedResponse.from(permission));
    }
    onPermissionsManaged(permissions);
  }

  private void finishWithDeniedPermission(Collection<String> permissions) {
    for (String permission : permissions) {
      listener.onPermissionDenied(PermissionDeniedResponse.from(permission,
          !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)));
    }
    onPermissionsManaged(permissions);
  }

  private void onPermissionsManaged(Collection<String> permissions) {
    pendingPermissions.removeAll(permissions);
    if (pendingPermissions.isEmpty()) {
      activity.finish();
      isRequestingPermission.set(false);
    }
  }

  private int getRequestCodeForPermissions(Collection<String> permissions) {
    return Math.abs(permissions.hashCode() % Integer.MAX_VALUE);
  }

  private void assertOneDexterRequestOngoing() {
    if (isRequestingPermission.getAndSet(true)) {
      throw new IllegalStateException("Only one Dexter request at a time is allowed");
    }
  }

  private void assertRequestSomePermission(Collection<String> permissions) {
    if (permissions.isEmpty()) {
      throw new IllegalStateException("Dexter has to be called with at least one permission");
    }
  }
}

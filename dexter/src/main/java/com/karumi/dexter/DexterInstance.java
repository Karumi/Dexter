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
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.PermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Inner implementation of a dexter instance
 */
final class DexterInstance {

  private Collection<String> pendingPermissions;
  private PermissionsReport permissionsReport;
  private Context context;
  private Activity activity;
  private PermissionsListener listener;
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
    PermissionsListener adapter = new PermissionsListenerToPermissionListenerAdapter(listener);
    checkPermissions(Collections.singleton(permission), adapter);
  }

  /**
   * Checks the state of a group of permissions reporting it when ready to the listener
   *
   * @param permissions Collection of values found in {@link android.Manifest.permission}
   * @param listener The class that will be reported when the state of the permissions are ready
   */
  void checkPermissions(Collection<String> permissions, PermissionsListener listener) {
    assertNoDexterRequestOngoing();
    assertRequestSomePermission(permissions);

    this.pendingPermissions = new TreeSet<>(permissions);
    this.permissionsReport = new PermissionsReport();
    this.listener = listener;

    startBackgroundActivity();
  }

  /**
   * Method called whenever the inner activity has been created and is ready to be used
   */
  void onActivityCreated(Activity activity) {
    this.activity = activity;

    Collection<String> deniedRequests = new LinkedList<>();
    Collection<String> grantedRequests = new LinkedList<>();

    for (String permission : pendingPermissions) {
      int permissionState = ContextCompat.checkSelfPermission(activity, permission);
      switch (permissionState) {
        case PackageManager.PERMISSION_DENIED:
          deniedRequests.add(permission);
          break;
        case PackageManager.PERMISSION_GRANTED:
        default:
          grantedRequests.add(permission);
          break;
      }
    }

    handleDeniedPermissions(deniedRequests);
    updatePermissionsAsGranted(grantedRequests);
  }

  /**
   * Method called whenever the permission has been granted by the user
   */
  void onPermissionRequestGranted(Collection<String> permissions) {
    updatePermissionsAsGranted(permissions);
  }

  /**
   * Method called whenever the permission has been denied by the user
   */
  void onPermissionRequestDenied(Collection<String> permissions) {
    updatePermissionsAsDenied(permissions);
  }

  /**
   * Method called when the user has been informed with the rationale and agrees to continue
   * with the permission request process
   */
  void onContinuePermissionRequest() {
    requestPermissionsToSystem(pendingPermissions);
  }

  /**
   * Method called when the user has been informed with the rationale and decides to cancel
   * the permission request process
   */
  void onCancelPermissionRequest() {
    updatePermissionsAsDenied(pendingPermissions);
  }

  /**
   * Starts the native request permissions process
   */
  void requestPermissionsToSystem(Collection<String> permissions) {
    int requestCode = getRequestCodeForPermissions(permissions);
    ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]),
        requestCode);
  }

  private void startBackgroundActivity() {
    Intent intent = new Intent(context, DexterActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
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
      requestPermissionsToSystem(permissions);
    } else {
      PermissionRationaleToken permissionToken = new PermissionRationaleToken(this);
      listener.onPermissionRationaleShouldBeShown(shouldShowRequestRationalePermissions,
          permissionToken);
    }
  }

  private void updatePermissionsAsGranted(Collection<String> permissions) {
    for (String permission : permissions) {
      PermissionGrantedResponse response = PermissionGrantedResponse.from(permission);
      permissionsReport.addGrantedPermissionResponse(response);
    }
    onPermissionsChecked(permissions);
  }

  private void updatePermissionsAsDenied(Collection<String> permissions) {
    for (String permission : permissions) {
      PermissionDeniedResponse response = PermissionDeniedResponse.from(permission,
          !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission));
      permissionsReport.addDeniedPermissionResponse(response);
    }
    onPermissionsChecked(permissions);
  }

  private void onPermissionsChecked(Collection<String> permissions) {
    pendingPermissions.removeAll(permissions);
    if (pendingPermissions.isEmpty()) {
      activity.finish();
      isRequestingPermission.set(false);
      listener.onPermissionsChecked(permissionsReport);
    }
  }

  private int getRequestCodeForPermissions(Collection<String> permissions) {
    return Math.abs(permissions.hashCode() % Integer.MAX_VALUE);
  }

  private void assertNoDexterRequestOngoing() {
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

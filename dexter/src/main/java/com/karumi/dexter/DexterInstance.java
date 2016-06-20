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
import android.support.v4.content.ContextCompat;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.EmptyMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Inner implementation of a dexter instance holding the state of the permissions request
 */
final class DexterInstance {

  private static final int PERMISSIONS_REQUEST_CODE = 42;
  private static final MultiplePermissionsListener EMPTY_LISTENER =
      new EmptyMultiplePermissionsListener();

  private final Context context;
  private final AndroidPermissionService androidPermissionService;
  private final IntentProvider intentProvider;
  private final Collection<String> pendingPermissions;
  private final MultiplePermissionsReport multiplePermissionsReport;
  private final AtomicBoolean isRequestingPermission;
  private final AtomicBoolean rationaleAccepted;
  private final Object pendingPermissionsMutex = new Object();

  private Activity activity;
  private MultiplePermissionsListener listener = EMPTY_LISTENER;

  DexterInstance(Context context, AndroidPermissionService androidPermissionService,
      IntentProvider intentProvider) {
    this.context = context.getApplicationContext();
    this.androidPermissionService = androidPermissionService;
    this.intentProvider = intentProvider;
    this.pendingPermissions = new TreeSet<>();
    this.multiplePermissionsReport = new MultiplePermissionsReport();
    this.isRequestingPermission = new AtomicBoolean();
    this.rationaleAccepted = new AtomicBoolean();
  }

  /**
   * Checks the state of a specific permission reporting it when ready to the listener.
   *
   * @param listener The class that will be reported when the state of the permission is ready
   * @param permission One of the values found in {@link android.Manifest.permission}
   * @param thread thread the Listener methods will be called on
   */
  void checkPermission(PermissionListener listener, String permission, Thread thread) {
    checkSinglePermission(listener, permission, thread);
  }

  /**
   * Checks the state of a collection of permissions reporting their state to the listener when all
   * of them are resolved
   *
   * @param listener The class that will be reported when the state of all the permissions is ready
   * @param permissions Array of values found in {@link android.Manifest.permission}
   * @param thread thread the Listener methods will be called on
   */
  void checkPermissions(MultiplePermissionsListener listener, Collection<String> permissions,
      Thread thread) {
    checkMultiplePermissions(listener, permissions, thread);
  }

  /**
   * Check if there is a permission pending to be confirmed by the user and restarts the
   * request for permission process.
   */
  void continuePendingRequestIfPossible(PermissionListener listener, Thread thread) {
    MultiplePermissionsListenerToPermissionListenerAdapter adapter =
        new MultiplePermissionsListenerToPermissionListenerAdapter(listener);
    continuePendingRequestsIfPossible(adapter, thread);
  }

  /**
   * Check if there are some permissions pending to be confirmed by the user and restarts the
   * request for permission process.
   */
  void continuePendingRequestsIfPossible(MultiplePermissionsListener listener, Thread thread) {
    if (!pendingPermissions.isEmpty()) {
      this.listener = new MultiplePermissionListenerThreadDecorator(listener, thread);
      if (!rationaleAccepted.get()) {
        onActivityReady(activity);
      }
    }
  }

  /**
   * Method called whenever the inner activity has been created or restarted and is ready to be
   * used.
   */
  void onActivityReady(Activity activity) {
    this.activity = activity;

    PermissionStates permissionStates = null;
    synchronized (pendingPermissionsMutex) {
      if (activity != null) {
        permissionStates = getPermissionStates(pendingPermissions);
      }
    }

    if (permissionStates != null) {
      handleDeniedPermissions(permissionStates.getDeniedPermissions());
      updatePermissionsAsGranted(permissionStates.getGrantedPermissions());
    }
  }

  /**
   * Method called whenever the permissions has been granted by the user
   */
  void onPermissionRequestGranted(Collection<String> permissions) {
    updatePermissionsAsGranted(permissions);
  }

  /**
   * Method called whenever the permissions has been denied by the user
   */
  void onPermissionRequestDenied(Collection<String> permissions) {
    updatePermissionsAsDenied(permissions);
  }

  /**
   * Method called when the user has been informed with a rationale and agrees to continue
   * with the permission request process
   */
  void onContinuePermissionRequest() {
    rationaleAccepted.set(true);
    requestPermissionsToSystem(pendingPermissions);
  }

  /**
   * Method called when the user has been informed with a rationale and decides to cancel
   * the permission request process
   */
  void onCancelPermissionRequest() {
    rationaleAccepted.set(false);
    updatePermissionsAsDenied(pendingPermissions);
  }

  /**
   * Is a request for permission currently ongoing?
   * If so, state of permissions must not be checked until the request is resolved
   * or it will cause an exception
   */
  boolean isRequestOngoing() {
    return isRequestingPermission.get();
  }

  /**
   * Starts the native request permissions process
   */
  void requestPermissionsToSystem(Collection<String> permissions) {
    androidPermissionService.requestPermissions(activity,
        permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST_CODE);
  }

  private PermissionStates getPermissionStates(Collection<String> pendingPermissions) {
    PermissionStates permissionStates = new PermissionStates();

    for (String permission : pendingPermissions) {
      int permissionState = androidPermissionService.checkSelfPermission(activity, permission);
      switch (permissionState) {
        case PackageManager.PERMISSION_DENIED:
          permissionStates.addDeniedPermission(permission);
          break;
        case PackageManager.PERMISSION_GRANTED:
        default:
          permissionStates.addGrantedPermission(permission);
          break;
      }
    }

    return permissionStates;
  }

  private void startTransparentActivityIfNeeded() {
    Intent intent = intentProvider.get(context, DexterActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  private void handleDeniedPermissions(Collection<String> permissions) {
    if (permissions.isEmpty()) {
      return;
    }

    List<PermissionRequest> shouldShowRequestRationalePermissions = new LinkedList<>();

    for (String permission : permissions) {
      if (androidPermissionService.shouldShowRequestPermissionRationale(activity, permission)) {
        shouldShowRequestRationalePermissions.add(new PermissionRequest(permission));
      }
    }

    if (shouldShowRequestRationalePermissions.isEmpty()) {
      requestPermissionsToSystem(permissions);
    } else if (!rationaleAccepted.get()) {
      PermissionRationaleToken permissionToken = new PermissionRationaleToken(this);
      listener.onPermissionRationaleShouldBeShown(shouldShowRequestRationalePermissions,
          permissionToken);
    }
  }

  private void updatePermissionsAsGranted(Collection<String> permissions) {
    for (String permission : permissions) {
      PermissionGrantedResponse response = PermissionGrantedResponse.from(permission);
      multiplePermissionsReport.addGrantedPermissionResponse(response);
    }
    onPermissionsChecked(permissions);
  }

  private void updatePermissionsAsDenied(Collection<String> permissions) {
    for (String permission : permissions) {
      PermissionDeniedResponse response = PermissionDeniedResponse.from(permission,
          !androidPermissionService.shouldShowRequestPermissionRationale(activity, permission));
      multiplePermissionsReport.addDeniedPermissionResponse(response);
    }
    onPermissionsChecked(permissions);
  }

  private void onPermissionsChecked(Collection<String> permissions) {
    if (pendingPermissions.isEmpty()) {
      return;
    }

    synchronized (pendingPermissionsMutex) {
      pendingPermissions.removeAll(permissions);
      if (pendingPermissions.isEmpty()) {
        activity.finish();
        activity = null;
        isRequestingPermission.set(false);
        rationaleAccepted.set(false);
        MultiplePermissionsListener currentListener = listener;
        listener = EMPTY_LISTENER;
        currentListener.onPermissionsChecked(multiplePermissionsReport);
      }
    }
  }

  private void checkNoDexterRequestOngoing() {
    if (isRequestingPermission.getAndSet(true)) {
      throw new IllegalStateException("Only one Dexter request at a time is allowed");
    }
  }

  private void checkRequestSomePermission(Collection<String> permissions) {
    if (permissions.isEmpty()) {
      throw new IllegalStateException("Dexter has to be called with at least one permission");
    }
  }

  private void checkSinglePermission(PermissionListener listener, String permission,
      Thread thread) {
    MultiplePermissionsListener adapter =
        new MultiplePermissionsListenerToPermissionListenerAdapter(listener);
    checkMultiplePermissions(adapter, Collections.singleton(permission), thread);
  }

  private void checkMultiplePermissions(final MultiplePermissionsListener listener,
      final Collection<String> permissions, Thread thread) {
    checkNoDexterRequestOngoing();
    checkRequestSomePermission(permissions);

    pendingPermissions.clear();
    pendingPermissions.addAll(permissions);
    multiplePermissionsReport.clear();
    this.listener = new MultiplePermissionListenerThreadDecorator(listener, thread);
    if (!everyPermissionIsGranted(permissions) || !contextIsInstanceOfActivity()) {
      startTransparentActivityIfNeeded();
    } else {
      thread.execute(new Runnable() {
        @Override public void run() {
          MultiplePermissionsReport report = new MultiplePermissionsReport();
          for (String permission : permissions) {
            report.addGrantedPermissionResponse(PermissionGrantedResponse.from(permission));
          }
          isRequestingPermission.set(false);
          listener.onPermissionsChecked(report);
        }
      });
    }
    thread.loop();
  }

  /**
   * To be able to provide backward compatibility with the Dexter's 2.X.X version we need to check
   * at some point if the context instance is an instance of {@link android.app.Activity}.
   */
  private boolean contextIsInstanceOfActivity() {
    return context instanceof Activity;
  }

  private boolean everyPermissionIsGranted(Collection<String> permissions) {
    for (String permission : permissions) {
      int permissionState = androidPermissionService.checkSelfPermission(context, permission);
      if (permissionState != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private final class PermissionStates {
    private final Collection<String> deniedPermissions = new LinkedList<>();
    private final Collection<String> grantedPermissions = new LinkedList<>();

    private void addDeniedPermission(String permission) {
      deniedPermissions.add(permission);
    }

    private void addGrantedPermission(String permission) {
      grantedPermissions.add(permission);
    }

    private Collection<String> getDeniedPermissions() {
      return deniedPermissions;
    }

    private Collection<String> getGrantedPermissions() {
      return grantedPermissions;
    }
  }
}
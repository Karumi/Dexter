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
import android.os.Build;

import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.Arrays;
import java.util.Collection;

/**
 * Class to simplify the management of Android runtime permissions
 * Dexter needs to be initialized before checking for a permission using {@link
 * #initialize(Context)}
 */
public final class Dexter {

  private static DexterInstance instance;

  /**
   * Initializes the library
   *
   * @param context Context used by Dexter. Use your {@link android.app.Application} to make sure
   * the instance is not cleaned up during your app lifetime
   */
  public static void initialize(Context context) {
    if (instance == null) {
      AndroidPermissionService androidPermissionService = new AndroidPermissionService(Build.VERSION.SDK_INT);
      IntentProvider intentProvider = new IntentProvider();
      instance = new DexterInstance(context, androidPermissionService, intentProvider);
    }
  }

  /**
   * Checks the permission and notifies the listener of its state
   * It is important to note that permissions still have to be declared in the manifest
   *
   * @param listener The class that will be reported when the state of the permission is ready
   * @param permission One of the values found in {@link android.Manifest.permission}
   */
  public static void checkPermission(PermissionListener listener, String permission) {
    instance.checkPermission(listener, permission);
  }

  /**
   * Checks the permissions and notifies the listener of its state
   * It is important to note that permissions still have to be declared in the manifest
   *
   * @param listener The class that will be reported when the state of the permissions are ready
   * @param permissions Array of values found in {@link android.Manifest.permission}
   */
  public static void checkPermissions(MultiplePermissionsListener listener, String... permissions) {
    instance.checkPermissions(listener, Arrays.asList(permissions));
  }

  /**
   * Checks the permissions and notifies the listener of its state
   * It is important to note that permissions still have to be declared in the manifest
   *
   * @param listener The class that will be reported when the state of the permissions are ready
   * @param permissions Collection of values found in {@link android.Manifest.permission}
   */
  public static void checkPermissions(MultiplePermissionsListener listener,
      Collection<String> permissions) {
    instance.checkPermissions(listener, permissions);
  }

  /**
   * Method called whenever the DexterActivity has been created and is ready to be used
   */
  static void onActivityCreated(Activity activity) {
    instance.onActivityCreated(activity);
  }

  /**
   * Method called when all the permissions has been requested to the user
   *
   * @param grantedPermissions Collection with all the permissions the user has granted. Contains
   * values from {@link android.Manifest.permission}
   * @param deniedPermissions Collection with all the permissions the user has denied. Contains
   * values from {@link android.Manifest.permission}
   */
  static void onPermissionsRequested(Collection<String> grantedPermissions,
      Collection<String> deniedPermissions) {
    instance.onPermissionRequestGranted(grantedPermissions);
    instance.onPermissionRequestDenied(deniedPermissions);
  }
}

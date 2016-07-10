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
import com.karumi.dexter.listener.EmptyPermissionRequestErrorListener;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.EmptyMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Class to simplify the management of Android runtime permissions
 * Dexter needs to be initialized before checking for a permission using {@link
 * #initialize(Activity)}
 */
public final class Dexter
    implements DexterBuilder, DexterBuilder.Permission, DexterBuilder.SinglePermissionListener,
    DexterBuilder.MultiPermissionListener {

  private static DexterInstance instance;

  private Collection<String> permissions;
  private MultiplePermissionsListener listener = new EmptyMultiplePermissionsListener();
  private PermissionRequestErrorListener errorListener = new EmptyPermissionRequestErrorListener();
  private boolean shouldExecuteOnSameThread = false;

  private Dexter(Activity activity) {
    initialize(activity);
  }

  public static DexterBuilder.Permission withActivity(Activity activity) {
    return new Dexter(activity);
  }

  @Override public DexterBuilder.SinglePermissionListener withPermission(String permission) {
    permissions = Collections.singletonList(permission);
    return this;
  }

  @Override public DexterBuilder.MultiPermissionListener withPermissions(String... permissions) {
    this.permissions = Arrays.asList(permissions);
    return this;
  }

  @Override
  public DexterBuilder.MultiPermissionListener withPermissions(Collection<String> permissions) {
    this.permissions = new ArrayList<>(permissions);
    return this;
  }

  @Override public void continueRequestingPendingPermissions(PermissionListener listener) {
    instance.continuePendingRequestIfPossible(listener, ThreadFactory.makeMainThread());
  }

  @Override public void continueRequestingPendingPermissions(MultiplePermissionsListener listener) {
    instance.continuePendingRequestsIfPossible(listener, ThreadFactory.makeMainThread());
  }

  @Override public DexterBuilder withListener(PermissionListener listener) {
    this.listener = new MultiplePermissionsListenerToPermissionListenerAdapter(listener);
    return this;
  }

  @Override public DexterBuilder withListener(MultiplePermissionsListener listener) {
    this.listener = listener;
    return this;
  }

  @Override public DexterBuilder onSameThread() {
    shouldExecuteOnSameThread = true;
    return this;
  }

  @Override public DexterBuilder withErrorListener(PermissionRequestErrorListener errorListener) {
    this.errorListener = errorListener;
    return this;
  }

  @Override public void check() {
    try {
      Thread thread = getThread();
      instance.checkPermissions(listener, permissions, thread);
    } catch (DexterException e) {
      errorListener.onError(e.error);
    }
  }

  private Thread getThread() {
    Thread thread;

    if (shouldExecuteOnSameThread) {
      thread = ThreadFactory.makeSameThread();
    } else {
      thread = ThreadFactory.makeMainThread();
    }

    return thread;
  }

  /**
   * Initializes the library.
   *
   * @param context Context used by Dexter. Use your {@link android.app.Application} to make sure
   * the instance is not cleaned up during your app lifetime.
   * @deprecated use the initialize overloaded method using an {@link android.app.Activity} instance
   * as param instead of this one. This method will be removed in the next major release.
   */
  @Deprecated public static void initialize(Context context) {
    if (instance == null) {
      AndroidPermissionService androidPermissionService = new AndroidPermissionService();
      IntentProvider intentProvider = new IntentProvider();
      instance = new DexterInstance(context, androidPermissionService, intentProvider);
    }
  }

  /**
   * Initializes the library.
   *
   * @param activity context used by Dexter. Remember to invoke {@link com.karumi.dexter.Dexter}
   * stop method to avoid memory leaks.
   * @deprecated Use the non static constructor: new Dexter(Activity activity)
   */
  @Deprecated public static void initialize(Activity activity) {
    if (instance == null) {
      AndroidPermissionService androidPermissionService = new AndroidPermissionService();
      IntentProvider intentProvider = new IntentProvider();
      instance = new DexterInstance(activity, androidPermissionService, intentProvider);
    }
  }

  /**
   * Checks the permission and notifies the listener of its state.
   * It is important to note that permissions still have to be declared in the manifest.
   * Calling this method will result in an exception if {@link #isRequestOngoing()} returns true.
   * All listener methods are called on the same thread that fired the permission request.
   *
   * @param listener The class that will be reported when the state of the permission is ready
   * @param permission One of the values found in {@link android.Manifest.permission}
   * @deprecated
   */
  @Deprecated public static void checkPermissionOnSameThread(PermissionListener listener,
      String permission) {
    checkInstanceNotNull();
    instance.checkPermission(listener, permission, ThreadFactory.makeSameThread());
  }

  /**
   * Checks the permission and notifies the listener of its state.
   * It is important to note that permissions still have to be declared in the manifest.
   * Calling this method will result in an exception if {@link #isRequestOngoing()} returns true.
   * All listener methods are called on the main thread that fired the permission request.
   *
   * @param listener The class that will be reported when the state of the permission is ready
   * @param permission One of the values found in {@link android.Manifest.permission}
   * @deprecated
   */
  @Deprecated public static void checkPermission(PermissionListener listener, String permission) {
    checkInstanceNotNull();
    instance.checkPermission(listener, permission, ThreadFactory.makeMainThread());
  }

  /**
   * Checks the permissions and notifies the listener of its state.
   * It is important to note that permissions still have to be declared in the manifest.
   * Calling this method will result in an exception if {@link #isRequestOngoing()} returns true.
   * All listener methods are called on the same thread that fired the permission request.
   *
   * @param listener The class that will be reported when the state of the permissions are ready
   * @param permissions Array of values found in {@link android.Manifest.permission}
   * @deprecated
   */
  @Deprecated public static void checkPermissionsOnSameThread(MultiplePermissionsListener listener,
      String... permissions) {
    checkInstanceNotNull();
    instance.checkPermissions(listener, Arrays.asList(permissions), ThreadFactory.makeSameThread());
  }

  /**
   * Checks the permissions and notifies the listener of its state.
   * It is important to note that permissions still have to be declared in the manifest.
   * Calling this method will result in an exception if {@link #isRequestOngoing()} returns true.
   * All listener methods are called on the main thread that fired the permission request.
   *
   * @param listener The class that will be reported when the state of the permissions are ready
   * @param permissions Array of values found in {@link android.Manifest.permission}
   * @deprecated
   */
  @Deprecated public static void checkPermissions(MultiplePermissionsListener listener,
      String... permissions) {
    checkInstanceNotNull();
    instance.checkPermissions(listener, Arrays.asList(permissions), ThreadFactory.makeMainThread());
  }

  /**
   * Checks the permissions and notifies the listener of its state
   * It is important to note that permissions still have to be declared in the manifest
   *
   * @param listener The class that will be reported when the state of the permissions are ready
   * @param permissions Collection of values found in {@link android.Manifest.permission}
   * @deprecated
   */
  @Deprecated public static void checkPermissions(MultiplePermissionsListener listener,
      Collection<String> permissions) {
    checkInstanceNotNull();
    instance.checkPermissions(listener, permissions, ThreadFactory.makeMainThread());
  }

  /**
   * Checks is there is any permission request still ongoing.
   * If so, state of permissions must not be checked until it is resolved
   * or it will cause an exception.
   *
   * @deprecated
   */
  @Deprecated public static boolean isRequestOngoing() {
    checkInstanceNotNull();
    return instance.isRequestOngoing();
  }

  /**
   * Requests pending permissions if there were permissions lost. This method can be used to
   * recover the Dexter state during a configuration change, for example when the device is
   * rotated.
   *
   * @deprecated
   */
  @Deprecated public static void continuePendingRequestsIfPossible(
      MultiplePermissionsListener listener) {
    checkInstanceNotNull();
    instance.continuePendingRequestsIfPossible(listener, ThreadFactory.makeMainThread());
  }

  /**
   * Requests pending permission if there was a permissions lost. This method can be used to
   * recover the Dexter state during a configuration change, for example when the device is
   * rotated.
   *
   * @deprecated
   */
  @Deprecated public static void continuePendingRequestIfPossible(PermissionListener listener) {
    checkInstanceNotNull();
    instance.continuePendingRequestIfPossible(listener, ThreadFactory.makeMainThread());
  }

  @Deprecated private static void checkInstanceNotNull() {
    if (instance == null) {
      throw new NullPointerException("context == null \n Must call \"initialize\" on Dexter");
    }
  }

  /**
   * Method called whenever the DexterActivity has been created or recreated and is ready to be
   * used.
   */
  static void onActivityReady(Activity activity) {
    instance.onActivityReady(activity);
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
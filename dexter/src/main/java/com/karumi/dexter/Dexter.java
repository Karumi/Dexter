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
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Class to simplify the management of Android runtime permissions
 * You can use this class directly using the provided fluent API like:
 *
 * Dexter.withActivity(activity)
 *       .withPermission(permission)
 *       .withListener(listener)
 *       .onSameThread()
 *       .check()
 */
public final class Dexter
    implements DexterBuilder, DexterBuilder.Permission, DexterBuilder.SinglePermissionListener,
    DexterBuilder.MultiPermissionListener {

  private static DexterInstance instance;

  private Collection<String> permissions;
  private MultiplePermissionsListener listener = new BaseMultiplePermissionsListener();
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

  private static void initialize(Context context) {
    if (instance == null) {
      AndroidPermissionService androidPermissionService = new AndroidPermissionService();
      IntentProvider intentProvider = new IntentProvider();
      instance = new DexterInstance(context, androidPermissionService, intentProvider);
    } else {
      instance.setContext(context);
    }
  }

  /**
   * Method called whenever the DexterActivity has been created or recreated and is ready to be
   * used.
   */
  static void onActivityReady(Activity activity) {
    /* Check against null values because sometimes the DexterActivity can call these internal
       methods when the DexterInstance has been cleaned up.
       Refer to this commit message for a more detailed explanation of the issue.
     */
    if (instance != null) {
      instance.onActivityReady(activity);
    }
  }

  /**
   * Method called whenever the DexterActivity has been destroyed.
   */
  static void onActivityDestroyed() {
    /* Check against null values because sometimes the DexterActivity can call these internal
       methods when the DexterInstance has been cleaned up.
       Refer to this commit message for a more detailed explanation of the issue.
     */
    if (instance != null) {
      instance.onActivityDestroyed();
    }
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
    /* Check against null values because sometimes the DexterActivity can call these internal
       methods when the DexterInstance has been cleaned up.
       Refer to this commit message for a more detailed explanation of the issue.
     */
    if (instance != null) {
      instance.onPermissionRequestGranted(grantedPermissions);
      instance.onPermissionRequestDenied(deniedPermissions);
    }
  }
}
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
import com.karumi.dexter.listener.PermissionListener;

/**
 * Class to simplify the management of Android runtime permissions
 * Dexter needs to be initialized before checking for a permission using {@link
 * #initialize(Context)}
 */
public final class Dexter {

  private static DexterInstance instance;

  public static void initialize(Context context) {
    if (instance == null) {
      instance = new DexterInstance(context);
    }
  }

  /**
   * Checks the permission and notifies the listener of its state
   * It is important to note that permissions still have to be declared in the manifest
   *
   * @param permission One of the values found in {@link android.Manifest.permission}
   */
  public static void checkPermission(String permission, PermissionListener listener) {
    instance.checkPermission(permission, listener);
  }

  static void onActivityCreated(Activity activity) {
    instance.onActivityCreated(activity);
  }

  static void onPermissionRequestGranted() {
    instance.onPermissionRequestGranted();
  }

  static void onPermissionRequestDenied() {
    instance.onPermissionRequestDenied();
  }
}

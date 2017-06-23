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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.WindowManager;
import java.util.Collection;
import java.util.LinkedList;

public final class DexterActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Dexter.onActivityReady(this);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Dexter.onActivityDestroyed();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Dexter.onActivityReady(this);
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    Collection<String> grantedPermissions = new LinkedList<>();
    Collection<String> deniedPermissions = new LinkedList<>();

    for (int i = 0; i < permissions.length; i++) {
      String permission = permissions[i];
      if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
        deniedPermissions.add(permission);
      } else {
        grantedPermissions.add(permission);
      }
    }

    Dexter.onPermissionsRequested(grantedPermissions, deniedPermissions);
  }
}

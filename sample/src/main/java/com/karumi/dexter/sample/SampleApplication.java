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

package com.karumi.dexter.sample;

import android.Manifest;
import android.app.Application;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.EmptyPermissionListener;

/**
 * Sample application that initializes the Dexter library.
 */
public class SampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Dexter.initialize(this);
    boolean launchDexterOnApplication = DexterApplicationMode.isEnable(this);
    if (launchDexterOnApplication) {
      DexterApplicationMode.clear(this);
      requestDexterOnApplication();
    }
  }

  private void requestDexterOnApplication() {
    Dexter.checkPermission(new EmptyPermissionListener(), Manifest.permission.RECORD_AUDIO);
  }
}

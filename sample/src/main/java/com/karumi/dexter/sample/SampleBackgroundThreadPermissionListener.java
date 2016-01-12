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

import android.os.Handler;
import android.os.Looper;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;

/**
 * Sample listener that shows how to handle permission request callbacks on a background thread
 */
public class SampleBackgroundThreadPermissionListener extends SamplePermissionListener {

  private Handler handler = new Handler(Looper.getMainLooper());

  public SampleBackgroundThreadPermissionListener(SampleActivity activity) {
    super(activity);
  }

  @Override public void onPermissionGranted(final PermissionGrantedResponse response) {
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionGranted(response);
      }
    });
  }

  @Override public void onPermissionDenied(final PermissionDeniedResponse response) {
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionDenied(response);
      }
    });
  }

  @Override public void onPermissionRationaleShouldBeShown(final PermissionRequest permission,
      final PermissionToken token) {
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionRationaleShouldBeShown(
            permission, token);
      }
    });
  }
}

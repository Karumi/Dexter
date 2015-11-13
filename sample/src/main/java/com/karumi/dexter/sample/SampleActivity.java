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
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.karumi.dexter.Dexter;

public class SampleActivity extends Activity implements Dexter.Listener {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sample_activity);
    Dexter.INSTANCE.initialize(this);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.camera_permission_button) public void onCameraPermissionButtonClicked() {
    Dexter.INSTANCE.checkPermission(Manifest.permission.CAMERA, this);
  }

  @OnClick(R.id.contacts_permission_button) public void onContactsPermissionButtonClicked() {
    Dexter.INSTANCE.checkPermission(Manifest.permission.READ_CONTACTS, this);
  }

  @OnClick(R.id.audio_permission_button) public void onAudioPermissionButtonClicked() {
    Dexter.INSTANCE.checkPermission(Manifest.permission.RECORD_AUDIO, this);
  }

  @Override public void onPermissionGranted(String permission) {
    Log.d("Gersio", "onPermissionGranted [" + permission + "]");
  }

  @Override public void onPermissionDenied(String permission) {
    Log.d("Gersio", "onPermissionDenied [" + permission + "]");
  }

  @Override public void onPermissionDialogShown(String permission) {
    Log.d("Gersio", "onPermissionDialogShown [" + permission + "]");
  }

  @Override
  public void onPermissionRationaleShouldBeShown(String permission, Dexter.PermissionToken token) {
    Log.d("Gersio", "onPermissionRationaleShouldBeShown [" + permission + "]");
    token.continuePermissionRequest();
  }
}

/*
 * Copyright (C) 2016 Karumi.
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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

/**
 * Activity to show if Dexter works when have been invoked from onCreate.
 */
public class OnCreateSampleActivity extends Activity {

  @Bind(android.R.id.content) ViewGroup rootView;
  @Bind(R.id.contacts_permission_feedback) TextView feedbackView;

  private PermissionListener cameraPermissionListener;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.oncreate_sample_activity);
    ButterKnife.bind(this);
    createPermissionListeners();
    requestPermission();
  }

  private void requestPermission() {
    if (Dexter.isRequestOngoing()) {
      return;
    }
    Dexter.checkPermission(cameraPermissionListener, Manifest.permission.CAMERA);
  }

  private void createPermissionListeners() {

    cameraPermissionListener =
        new CompositePermissionListener(feedbackViewPermissionListener,
            SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                R.string.camera_permission_denied_feedback)
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .build());
  }

  private PermissionListener feedbackViewPermissionListener = new PermissionListener() {
    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
      setFeedback(getString(R.string.permission_granted_feedback));
    }

    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
      setFeedback(getString(R.string.permission_denied_feedback));
    }

    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
        PermissionToken token) {
    }
  };

  private void setFeedback(String feedback) {
    String feedbackCompose = getString(R.string.feedback_permission_on_create) + " "
        + feedback;
    feedbackView.setText(feedbackCompose);
  }

  public static void open(Context context) {
    Intent intent = new Intent(context, OnCreateSampleActivity.class);
    context.startActivity(intent);
  }
}

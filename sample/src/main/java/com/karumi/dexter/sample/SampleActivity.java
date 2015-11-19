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
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.MultiPermissionListener;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionListener;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.SnackbarOnDeniedPermissionListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Sample activity showing the permission request process with Dexter.
 */
public class SampleActivity extends Activity implements PermissionListener {

  @Bind(R.id.audio_permission_feedback) TextView audioPermissionFeedbackView;
  @Bind(R.id.camera_permission_feedback) TextView cameraPermissionFeedbackView;
  @Bind(R.id.contacts_permission_feedback) TextView contactsPermissionFeedbackView;
  @Bind(android.R.id.content) ViewGroup rootView;

  private PermissionListener cameraPermissionListener;
  private PermissionListener contactsPermissionListener;
  private PermissionListener audioPermissionListener;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sample_activity);
    ButterKnife.bind(this);
    createPermissionListeners();
  }

  @OnClick(R.id.camera_permission_button) public void onCameraPermissionButtonClicked() {
    Dexter.checkPermission(Manifest.permission.CAMERA, cameraPermissionListener);
  }

  @OnClick(R.id.contacts_permission_button) public void onContactsPermissionButtonClicked() {
    Dexter.checkPermission(Manifest.permission.READ_CONTACTS, contactsPermissionListener);
  }

  @OnClick(R.id.audio_permission_button) public void onAudioPermissionButtonClicked() {
    Dexter.checkPermission(Manifest.permission.RECORD_AUDIO, audioPermissionListener);
  }

  @Override public void onPermissionGranted(PermissionGrantedResponse response) {
    switch (response.getRequestedPermission().getPermission()) {
      case Manifest.permission.CAMERA:
        showPermissionGranted(cameraPermissionFeedbackView);
        break;
      case Manifest.permission.READ_CONTACTS:
        showPermissionGranted(contactsPermissionFeedbackView);
        break;
      case Manifest.permission.RECORD_AUDIO:
        showPermissionGranted(audioPermissionFeedbackView);
        break;
    }
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    switch (response.getRequestedPermission().getPermission()) {
      case Manifest.permission.CAMERA:
        showPermissionDenied(cameraPermissionFeedbackView, response.isPermanentlyDenied());
        break;
      case Manifest.permission.READ_CONTACTS:
        showPermissionDenied(contactsPermissionFeedbackView, response.isPermanentlyDenied());
        break;
      case Manifest.permission.RECORD_AUDIO:
        showPermissionDenied(audioPermissionFeedbackView, response.isPermanentlyDenied());
        break;
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) @Override
  public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
    new AlertDialog.Builder(this)
        .setTitle(R.string.permission_rationale_title)
        .setMessage(R.string.permission_rationale_message)
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            token.cancelPermissionRequest();
          }
        })
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            token.continuePermissionRequest();
          }
        })
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override public void onDismiss(DialogInterface dialog) {
            token.cancelPermissionRequest();
          }
        })
        .show();
  }

  private void showPermissionGranted(TextView feedbackView) {
    feedbackView.setText(R.string.permission_granted_feedback);
    feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_granted));
  }

  private void showPermissionDenied(TextView feedbackView, boolean isPermanentlyDenied) {
    feedbackView.setText(isPermanentlyDenied
        ? R.string.permission_permanently_denied_feedback
        : R.string.permission_denied_feedback);
    feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_denied));
  }

  private void createPermissionListeners() {
    cameraPermissionListener = new MultiPermissionListener(this,
        SnackbarOnDeniedPermissionListener.Builder
            .with(rootView, R.string.camera_permission_denied_feedback)
            .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
            .build());
    contactsPermissionListener = new MultiPermissionListener(this,
        SnackbarOnDeniedPermissionListener.Builder
            .with(rootView, R.string.contacts_permission_denied_feedback)
            .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
            .build());
    PermissionListener dialogOnDeniedPermissionListener =
        DialogOnDeniedPermissionListener.Builder
            .withContext(this)
            .withTitle(R.string.audio_permission_denied_dialog_title)
            .withMessage(R.string.audio_permission_denied_dialog_feedback)
            .withButtonText(android.R.string.ok)
            .withIcon(R.mipmap.ic_logo_karumi)
            .build();
    audioPermissionListener = new MultiPermissionListener(this, dialogOnDeniedPermissionListener);
  }
}

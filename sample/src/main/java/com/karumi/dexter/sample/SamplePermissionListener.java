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
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class SamplePermissionListener implements PermissionListener {

  private final Context context;
  private final TextView audioPermissionFeedbackView;
  private final TextView cameraPermissionFeedbackView;
  private final TextView contactsPermissionFeedbackView;

  public SamplePermissionListener(Context context, TextView audioPermissionFeedbackView,
      TextView cameraPermissionFeedbackView, TextView contactsPermissionFeedbackView) {
    this.context = context;
    this.audioPermissionFeedbackView = audioPermissionFeedbackView;
    this.cameraPermissionFeedbackView = cameraPermissionFeedbackView;
    this.contactsPermissionFeedbackView = contactsPermissionFeedbackView;
  }

  @Override public void onPermissionGranted(PermissionGrantedResponse response) {
    switch (response.getPermissionName()) {
      case Manifest.permission.CAMERA:
        showPermissionGranted(cameraPermissionFeedbackView);
        break;
      case Manifest.permission.READ_CONTACTS:
        showPermissionGranted(contactsPermissionFeedbackView);
        break;
      case Manifest.permission.RECORD_AUDIO:
        showPermissionGranted(audioPermissionFeedbackView);
        break;
      default:
        throw new RuntimeException("We didn't request this permission!");
    }
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    switch (response.getPermissionName()) {
      case Manifest.permission.CAMERA:
        showPermissionDenied(cameraPermissionFeedbackView, response.isPermanentlyDenied());
        break;
      case Manifest.permission.READ_CONTACTS:
        showPermissionDenied(contactsPermissionFeedbackView, response.isPermanentlyDenied());
        break;
      case Manifest.permission.RECORD_AUDIO:
        showPermissionDenied(audioPermissionFeedbackView, response.isPermanentlyDenied());
        break;
      default:
        throw new RuntimeException("We didn't request this permission!");
    }
  }

  @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
      PermissionToken token) {

  }

  private void showPermissionGranted(TextView feedbackView) {
    feedbackView.setText(R.string.permission_granted_feedback);
    feedbackView.setTextColor(ContextCompat.getColor(context, R.color.permission_granted));
  }

  private void showPermissionDenied(TextView feedbackView, boolean isPermanentlyDenied) {
    feedbackView.setText(isPermanentlyDenied ? R.string.permission_permanently_denied_feedback
        : R.string.permission_denied_feedback);
    feedbackView.setTextColor(ContextCompat.getColor(context, R.color.permission_denied));
  }
}

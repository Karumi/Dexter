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
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

/**
 * Sample activity showing the permission request process with Dexter.
 */
public class SampleActivity extends Activity {

  @BindView(R.id.audio_permission_feedback) TextView audioPermissionFeedbackView;
  @BindView(R.id.camera_permission_feedback) TextView cameraPermissionFeedbackView;
  @BindView(R.id.contacts_permission_feedback) TextView contactsPermissionFeedbackView;
  @BindView(android.R.id.content) View contentView;

  private MultiplePermissionsListener allPermissionsListener;
  private PermissionListener cameraPermissionListener;
  private PermissionListener contactsPermissionListener;
  private PermissionListener audioPermissionListener;
  private PermissionRequestErrorListener errorListener;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sample_activity);
    ButterKnife.bind(this);
    createPermissionListeners();
  }

  @OnClick(R.id.all_permissions_button) public void onAllPermissionsButtonClicked() {
    Dexter.withActivity(this)
        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO)
        .withListener(allPermissionsListener)
        .withErrorListener(errorListener)
        .check();
  }

  @OnClick(R.id.camera_permission_button) public void onCameraPermissionButtonClicked() {
    new Thread(new Runnable() {
      @Override public void run() {
        Dexter.withActivity(SampleActivity.this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(cameraPermissionListener)
            .withErrorListener(errorListener)
            .onSameThread()
            .check();
      }
    }).start();
  }

  @OnClick(R.id.contacts_permission_button) public void onContactsPermissionButtonClicked() {
    Dexter.withActivity(this)
        .withPermission(Manifest.permission.READ_CONTACTS)
        .withListener(contactsPermissionListener)
        .withErrorListener(errorListener)
        .check();
  }

  @OnClick(R.id.audio_permission_button) public void onAudioPermissionButtonClicked() {
    Dexter.withActivity(this)
        .withPermission(Manifest.permission.RECORD_AUDIO)
        .withListener(audioPermissionListener)
        .withErrorListener(errorListener)
        .check();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  public void showPermissionRationale(final PermissionToken token) {
    new AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
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

  public void showPermissionGranted(String permission) {
    TextView feedbackView = getFeedbackViewForPermission(permission);
    feedbackView.setText(R.string.permission_granted_feedback);
    feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_granted));
  }

  public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
    TextView feedbackView = getFeedbackViewForPermission(permission);
    feedbackView.setText(isPermanentlyDenied ? R.string.permission_permanently_denied_feedback
        : R.string.permission_denied_feedback);
    feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_denied));
  }

  private void createPermissionListeners() {
    PermissionListener feedbackViewPermissionListener = new SamplePermissionListener(this);
    MultiplePermissionsListener feedbackViewMultiplePermissionListener =
        new SampleMultiplePermissionListener(this);

    allPermissionsListener =
        new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(contentView,
                R.string.all_permissions_denied_feedback)
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .build());
    contactsPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
        SnackbarOnDeniedPermissionListener.Builder.with(contentView,
            R.string.contacts_permission_denied_feedback)
            .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
            .withCallback(new Snackbar.Callback() {
              @Override public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
              }

              @Override public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
              }
            })
            .build());

    PermissionListener dialogOnDeniedPermissionListener =
        DialogOnDeniedPermissionListener.Builder.withContext(this)
            .withTitle(R.string.audio_permission_denied_dialog_title)
            .withMessage(R.string.audio_permission_denied_dialog_feedback)
            .withButtonText(android.R.string.ok)
            .withIcon(R.mipmap.ic_logo_karumi)
            .build();
    audioPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
        dialogOnDeniedPermissionListener);
    cameraPermissionListener = new SampleBackgroundThreadPermissionListener(this);

    errorListener = new SampleErrorListener();
  }

  private TextView getFeedbackViewForPermission(String name) {
    TextView feedbackView;

    switch (name) {
      case Manifest.permission.CAMERA:
        feedbackView = cameraPermissionFeedbackView;
        break;
      case Manifest.permission.READ_CONTACTS:
        feedbackView = contactsPermissionFeedbackView;
        break;
      case Manifest.permission.RECORD_AUDIO:
        feedbackView = audioPermissionFeedbackView;
        break;
      default:
        throw new RuntimeException("No feedback view for this permission");
    }

    return feedbackView;
  }
}

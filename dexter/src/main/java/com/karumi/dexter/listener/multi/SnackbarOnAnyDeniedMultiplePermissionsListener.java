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

package com.karumi.dexter.listener.multi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import com.karumi.dexter.MultiplePermissionsReport;

/**
 * Utility listener that shows a {@link Snackbar} with a custom text whenever a permission has been
 * denied
 */
public class SnackbarOnAnyDeniedMultiplePermissionsListener
    extends BaseMultiplePermissionsListener {

  private final View view;
  private final String text;
  private final String buttonText;
  private final View.OnClickListener onButtonClickListener;
  private final Snackbar.Callback snackbarCallback;
  private final int duration;

  /**
   * @param view The view to find a parent from
   * @param text Message displayed in the snackbar
   * @param buttonText Message displayed in the snackbar button
   * @param onButtonClickListener Action performed when the user clicks the snackbar button
   */
  private SnackbarOnAnyDeniedMultiplePermissionsListener(View view, String text,
      String buttonText, View.OnClickListener onButtonClickListener,
      Snackbar.Callback snackbarCallback, int duration) {
    this.view = view;
    this.text = text;
    this.buttonText = buttonText;
    this.onButtonClickListener = onButtonClickListener;
    this.snackbarCallback = snackbarCallback;
    this.duration = duration;
  }

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
    super.onPermissionsChecked(report);

    if (!report.areAllPermissionsGranted()) {
      showSnackbar();
    }
  }

  private void showSnackbar() {
    Snackbar snackbar = Snackbar.make(view, text, duration);
    if (buttonText != null && onButtonClickListener != null) {
      snackbar.setAction(buttonText, onButtonClickListener);
    }
    if (snackbarCallback != null) {
      snackbar.addCallback(snackbarCallback);
    }
    snackbar.show();
  }

  /**
   * Builder class to configure the displayed snackbar
   * Non set fields will not be shown
   */
  public static class Builder {
    private final View view;
    private final String text;
    private String buttonText;
    private View.OnClickListener onClickListener;
    private Snackbar.Callback snackbarCallback;
    private int duration = Snackbar.LENGTH_LONG;

    private Builder(View view, String text) {
      this.view = view;
      this.text = text;
    }

    public static Builder with(View view, String text) {
      return new Builder(view, text);
    }

    public static Builder with(View view, @StringRes int textResourceId) {
      return Builder.with(view, view.getContext().getString(textResourceId));
    }

    /**
     * Adds a text button with the provided click listener
     */
    public Builder withButton(String buttonText, View.OnClickListener onClickListener) {
      this.buttonText = buttonText;
      this.onClickListener = onClickListener;
      return this;
    }

    /**
     * Adds a text button with the provided click listener
     */
    public Builder withButton(@StringRes int buttonTextResourceId,
        View.OnClickListener onClickListener) {
      return withButton(view.getContext().getString(buttonTextResourceId), onClickListener);
    }

    /**
     * Adds a button that opens the application settings when clicked
     */
    public Builder withOpenSettingsButton(String buttonText) {
      this.buttonText = buttonText;
      this.onClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
          Context context = view.getContext();
          Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
              Uri.parse("package:" + context.getPackageName()));
          myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
          myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(myAppSettings);
        }
      };
      return this;
    }

    /**
     * Adds a button that opens the application settings when clicked
     */
    public Builder withOpenSettingsButton(@StringRes int buttonTextResourceId) {
      return withOpenSettingsButton(view.getContext().getString(buttonTextResourceId));
    }

    /**
     * Adds a callback to handle the snackbar {@code onDismissed} and {@code onShown} events.
     */
    public Builder withCallback(Snackbar.Callback callback) {
      this.snackbarCallback = callback;
      return this;
    }

    public Builder withDuration(int duration) {
      this.duration = duration;
      return this;
    }

    /**
     * Builds a new instance of {@link SnackbarOnAnyDeniedMultiplePermissionsListener}
     */
    public SnackbarOnAnyDeniedMultiplePermissionsListener build() {
      return new SnackbarOnAnyDeniedMultiplePermissionsListener(view, text, buttonText,
          onClickListener, snackbarCallback, duration);
    }
  }
}

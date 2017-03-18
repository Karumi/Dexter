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

package com.karumi.dexter.listener.single;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import com.karumi.dexter.listener.PermissionDeniedResponse;

/**
 * Utility listener that shows a {@link Snackbar} with a custom text whenever a permission has been
 * denied
 */
public class SnackbarOnDeniedPermissionListener extends BasePermissionListener {

  private final ViewGroup rootView;
  private final String text;
  private final String buttonText;
  private final View.OnClickListener onButtonClickListener;
  private final Snackbar.Callback snackbarCallback;
  private int duration = 5_000;

  /**
   * @param rootView Parent view to show the snackbar
   * @param text Message displayed in the snackbar
   * @param buttonText Message displayed in the snackbar button
   * @param onButtonClickListener Action performed when the user clicks the snackbar button
   */
  private SnackbarOnDeniedPermissionListener(ViewGroup rootView, String text, String buttonText,
      View.OnClickListener onButtonClickListener, Snackbar.Callback snackbarCallback, int duration) {
    this.rootView = rootView;
    this.text = text;
    this.buttonText = buttonText;
    this.onButtonClickListener = onButtonClickListener;
    this.snackbarCallback = snackbarCallback;
    this.duration = duration;
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    super.onPermissionDenied(response);

    Snackbar snackbar = Snackbar.make(rootView, text, Snackbar.LENGTH_LONG);
    if (buttonText != null && onButtonClickListener != null) {
      snackbar.setAction(buttonText, onButtonClickListener);
    }
    if (snackbarCallback != null) {
      snackbar.setCallback(snackbarCallback);
    }
    
    snackbar.setDuration(duration);
    snackbar.show();
  }

  /**
   * Builder class to configure the displayed snackbar
   * Non set fields will not be shown
   */
  public static class Builder {
    private final ViewGroup rootView;
    private final String text;
    private String buttonText;
    private View.OnClickListener onClickListener;
    private Snackbar.Callback snackbarCallback;
    private int duration = 5_000;

    private Builder(ViewGroup rootView, String text) {
      this.rootView = rootView;
      this.text = text;
    }
    private Builder(ViewGroup rootView, String text, int duration) {
      this.rootView = rootView;
      this.text = text;
      this.duration = duration;
    }
    
    public static Builder with(ViewGroup rootView, String text) {
      return new Builder(rootView, text);
    }
    public static Builder
    with(ViewGroup rootView, String text, int duration) {
      return new Builder(rootView, text, duration);
    }
    
    public static Builder with(ViewGroup rootView, @StringRes int textResourceId) {
      return Builder.with(rootView, rootView.getContext().getString(textResourceId));
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
      return withButton(rootView.getContext().getString(buttonTextResourceId), onClickListener);
    }

    /**
     * Adds a button that opens the application settings when clicked
     */
    public Builder withOpenSettingsButton(String buttonText) {
      this.buttonText = buttonText;
      this.onClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
          Context context = rootView.getContext();
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
      return withOpenSettingsButton(rootView.getContext().getString(buttonTextResourceId));
    }

    /**
     * Adds a callback to handle the snackbar {@code onDismissed} and {@code onShown} events.
     */
    public Builder withCallback(Snackbar.Callback callback) {
      this.snackbarCallback = callback;
      return this;
    }

    /**
     * Builds a new instance of {@link SnackbarOnDeniedPermissionListener}
     */
    public SnackbarOnDeniedPermissionListener build() {
      return new SnackbarOnDeniedPermissionListener(rootView, text, buttonText, onClickListener,
          snackbarCallback, duration);
    }
  }
}

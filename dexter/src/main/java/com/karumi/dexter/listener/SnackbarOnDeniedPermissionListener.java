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

package com.karumi.dexter.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.karumi.dexter.R;

/**
 * Utility listener that shows a {@link Snackbar} with a custom text whenever a permission has been
 * denied
 */
public class SnackbarOnDeniedPermissionListener extends EmptyPermissionListener {

  private final Context context;
  private final ViewGroup rootView;
  private final String text;
  private final String buttonText;
  private final View.OnClickListener onButtonClickListener;

  /**
   * @param context Context to inflate the snackbar
   * @param rootView Parent view to show the snackbar
   * @param text Message displayed in the snackbar
   * @param buttonText Message displayed in the snackbar button
   * @param onButtonClickListener Action performed when the user clicks the snackbar button
   */
  private SnackbarOnDeniedPermissionListener(Context context, ViewGroup rootView, String text,
      String buttonText, View.OnClickListener onButtonClickListener) {
    this.context = context;
    this.rootView = rootView;
    this.text = text;
    this.buttonText = buttonText;
    this.onButtonClickListener = onButtonClickListener;
  }

  @Override public void onPermissionDenied(String permission) {
    super.onPermissionDenied(permission);

    LayoutInflater inflater = LayoutInflater.from(context);
    View snackbarView = inflater.inflate(R.layout.snackbar, rootView);
    Snackbar snackbar = Snackbar.make(snackbarView, text, Snackbar.LENGTH_LONG);
    if (buttonText != null && onButtonClickListener != null) {
      snackbar.setAction(buttonText, onButtonClickListener);
    }
    snackbar.show();
  }

  /**
   * Builder class to configure the displayed snackbar
   * Non set fields will not be shown
   */
  public static class Builder {
    private final Context context;
    private final ViewGroup rootView;
    private final String text;
    private String buttonText;
    private View.OnClickListener onClickListener;

    private Builder(Context context, ViewGroup rootView, String text) {
      this.context = context;
      this.rootView = rootView;
      this.text = text;
    }

    public static Builder with(Context context, ViewGroup rootView, String text) {
      return new Builder(context, rootView, text);
    }

    public static Builder with(Context context, ViewGroup rootView, @StringRes int textResourceId) {
      return Builder.with(context, rootView, context.getString(textResourceId));
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
      return withButton(context.getString(buttonTextResourceId), onClickListener);
    }

    /**
     * Adds a button that opens the application settings when clicked
     */
    public Builder withOpenSettingsButton(String buttonText) {
      this.buttonText = buttonText;
      this.onClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
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
      return withOpenSettingsButton(context.getString(buttonTextResourceId));
    }

    /**
     * Builds a new instance of {@link SnackbarOnDeniedPermissionListener}
     */
    public SnackbarOnDeniedPermissionListener build() {
      return new SnackbarOnDeniedPermissionListener(context, rootView, text, buttonText,
          onClickListener);
    }
  }
}

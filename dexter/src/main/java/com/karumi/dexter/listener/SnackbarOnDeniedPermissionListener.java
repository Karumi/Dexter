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
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.karumi.dexter.R;

/**
 * Utility listener that shows a {@link Snackbar} with a custom text whenever a permission has been
 * denied.
 */
public class SnackbarOnDeniedPermissionListener extends EmptyPermissionListener {

  private final Context context;
  private final ViewGroup rootView;
  private final String text;

  /**
   * @param context Context to inflate the snackbar
   * @param rootView Parent view to show the snackbar
   * @param text Message displayed in the snackbar
   */
  public SnackbarOnDeniedPermissionListener(Context context, ViewGroup rootView, String text) {
    this.context = context;
    this.rootView = rootView;
    this.text = text;
  }

  /**
   * @param context Context to inflate the snackbar
   * @param rootView Parent view to show the snackbar
   * @param resId Resource id of the string displayed in the snackbar
   */
  public SnackbarOnDeniedPermissionListener(Context context, ViewGroup rootView,
      @StringRes int resId) {
    this.context = context;
    this.rootView = rootView;
    this.text = context.getString(resId);
  }

  @Override public void onPermissionDenied(String permission) {
    super.onPermissionDenied(permission);

    LayoutInflater inflater = LayoutInflater.from(context);
    View snackbarView = inflater.inflate(R.layout.snackbar, rootView);
    Snackbar.make(snackbarView, text, Snackbar.LENGTH_LONG).show();
  }
}

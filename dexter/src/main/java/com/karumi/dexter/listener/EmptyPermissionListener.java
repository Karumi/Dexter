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

import com.karumi.dexter.PermissionToken;

/**
 * Empty implementation of {@link PermissionListener} to allow extensions to implement only the
 * required methods.
 */
public class EmptyPermissionListener implements PermissionListener {

  @Override public void onPermissionGranted(String permission) {

  }

  @Override public void onPermissionDenied(String permission) {

  }

  @Override public void onPermissionPermanentlyDenied(String permission) {

  }

  @Override
  public void onPermissionRationaleShouldBeShown(String permission, PermissionToken token) {

  }
}

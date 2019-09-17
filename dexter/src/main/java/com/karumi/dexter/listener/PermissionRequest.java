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

import androidx.annotation.NonNull;

/**
 * Wrapper class for a permission request
 */
public final class PermissionRequest {

  private final String name;

  public PermissionRequest(@NonNull String name) {
    this.name = name;
  }

  /**
   * One of the values found in {@link android.Manifest.permission}
   */
  public String getName() {
    return name;
  }

  @Override public String toString() {
    return "Permission name: " + name;
  }
}

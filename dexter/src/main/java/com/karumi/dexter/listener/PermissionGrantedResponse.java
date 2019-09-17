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
 * If a permission was granted, an instance of this class will be returned
 * in the callback.
 */
public final class PermissionGrantedResponse {

  private final PermissionRequest requestedPermission;

  public PermissionGrantedResponse(@NonNull PermissionRequest requestedPermission) {
    this.requestedPermission = requestedPermission;
  }

  /**
   * Builds a new instance of PermissionGrantedResponse from a given permission string
   */
  public static PermissionGrantedResponse from(@NonNull String permission) {
    return new PermissionGrantedResponse(new PermissionRequest(permission));
  }

  public PermissionRequest getRequestedPermission() {
    return requestedPermission;
  }

  public String getPermissionName() {
    return requestedPermission.getName();
  }
}

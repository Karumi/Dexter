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
 * If a permission was denied, an instance of this class will be returned
 * in the callback.
 */
public final class PermissionDeniedResponse {

  private final PermissionRequest requestedPermission;
  private final boolean permanentlyDenied;

  public PermissionDeniedResponse(@NonNull PermissionRequest requestedPermission,
      boolean permanentlyDenied) {
    this.requestedPermission = requestedPermission;
    this.permanentlyDenied = permanentlyDenied;
  }

  /**
   * Builds a new instance of PermissionDeniedResponse from a given permission string
   * and a permanently-denied boolean flag
   */
  public static PermissionDeniedResponse from(@NonNull String permission,
      boolean permanentlyDenied) {
    return new PermissionDeniedResponse(new PermissionRequest(permission), permanentlyDenied);
  }

  public PermissionRequest getRequestedPermission() {
    return requestedPermission;
  }

  public String getPermissionName() {
    return requestedPermission.getName();
  }

  public boolean isPermanentlyDenied() {
    return permanentlyDenied;
  }
}

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

package com.karumi.dexter;

import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

/**
 * PermissionListener implementation that will perform yet another check when denied for the first
 * time. It will register the provided listener when retrying the check.
 */
public class RetryCheckPermissionOnDeniedPermissionListener extends BasePermissionListener {

  private final PermissionListener listener;
  private final CheckPermissionAction checkPermission;

  public RetryCheckPermissionOnDeniedPermissionListener(PermissionListener listener,
      CheckPermissionAction checkPermission) {
    this.listener = listener;
    this.checkPermission = checkPermission;
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    checkPermission.check(listener, response.getPermissionName());
  }

  @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
      PermissionToken token) {
    token.continuePermissionRequest();
  }

  public interface CheckPermissionAction {
    void check(PermissionListener listener, String permission);
  }
}

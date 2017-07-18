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
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.List;

/**
 * Adapter to translate calls to a {@link MultiplePermissionsListener} into @{PermissionListener}
 * methods
 */
final class MultiplePermissionsListenerToPermissionListenerAdapter
    implements MultiplePermissionsListener {

  private final PermissionListener listener;

  MultiplePermissionsListenerToPermissionListenerAdapter(PermissionListener listener) {
    this.listener = listener;
  }

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
    List<PermissionDeniedResponse> deniedResponses = report.getDeniedPermissionResponses();
    List<PermissionGrantedResponse> grantedResponses = report.getGrantedPermissionResponses();

    if (!deniedResponses.isEmpty()) {
      PermissionDeniedResponse response = deniedResponses.get(0);
      listener.onPermissionDenied(response);
    } else {
      PermissionGrantedResponse response = grantedResponses.get(0);
      listener.onPermissionGranted(response);
    }
  }

  @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> requests,
      PermissionToken token) {
    PermissionRequest firstRequest = requests.get(0);
    listener.onPermissionRationaleShouldBeShown(firstRequest, token);
  }
}

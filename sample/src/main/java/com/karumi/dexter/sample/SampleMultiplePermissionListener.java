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

package com.karumi.dexter.sample;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.List;

public class SampleMultiplePermissionListener implements MultiplePermissionsListener {

  private final SampleActivity activity;

  public SampleMultiplePermissionListener(SampleActivity activity) {
    this.activity = activity;
  }

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
    for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
      activity.showPermissionGranted(response.getPermissionName());
    }

    for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
      activity.showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
    }
  }

  @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
      PermissionToken token) {
    activity.showPermissionRationale(token);
  }
}

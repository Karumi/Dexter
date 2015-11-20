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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An in detail report of the request permission process.
 */
public final class PermissionsReport {

  private List<PermissionGrantedResponse> grantedPermissionResponses;
  private List<PermissionDeniedResponse> deniedPermissionResponses;

  public PermissionsReport() {
    grantedPermissionResponses = new LinkedList<>();
    deniedPermissionResponses = new LinkedList<>();
  }

  public boolean addGrantedPermissionResponse(PermissionGrantedResponse response) {
    return grantedPermissionResponses.add(response);
  }

  public boolean addDeniedPermissionResponse(PermissionDeniedResponse response) {
    return deniedPermissionResponses.add(response);
  }

  public Collection<PermissionGrantedResponse> getGrantedPermissionResponses() {
    return grantedPermissionResponses;
  }

  public Collection<PermissionDeniedResponse> getDeniedPermissionResponses() {
    return deniedPermissionResponses;
  }

  public boolean hasUserGrantedAllPermissions() {
    return deniedPermissionResponses.isEmpty();
  }

  public boolean hasPermanentlyDeniedAnyPermission() {
    boolean hasPermanentlyDeniedAnyPermission = false;
    for (PermissionDeniedResponse deniedResponse : deniedPermissionResponses) {
      if (deniedResponse.isPermanentlyDenied()) {
        hasPermanentlyDeniedAnyPermission = true;
        break;
      }
    }
    return hasPermanentlyDeniedAnyPermission;
  }
}

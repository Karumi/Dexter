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

package com.karumi.dexter.listener.single;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;

/**
 * Interface that listens to updates to the permission requests
 */
public interface PermissionListener {

  /**
   * Method called whenever a requested permission has been granted
   *
   * @param response A response object that contains the permission that has been requested and
   * any additional flags relevant to this response
   */
  void onPermissionGranted(PermissionGrantedResponse response);

  /**
   * Method called whenever a requested permission has been denied
   *
   * @param response A response object that contains the permission that has been requested and
   * any additional flags relevant to this response
   */
  void onPermissionDenied(PermissionDeniedResponse response);

  /**
   * Method called whenever Android asks the application to inform the user of the need for the
   * requested permission. The request process won't continue until the token is properly used
   *
   * @param permission The permission that has been requested
   * @param token Token used to continue or cancel the permission request process. The permission
   * request process will remain blocked until one of the token methods is called
   */
  void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token);
}

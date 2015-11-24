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
import java.util.Arrays;
import java.util.Collection;

/**
 * Listener that composes multiple listeners into one
 * All inner listeners will be called for a given event unless one of them throws an exception or
 * is blocked
 */
public class CompositePermissionListener implements PermissionListener {

  private final Collection<PermissionListener> listeners;

  /**
   * Creates a {@link CompositePermissionListener} containing all the provided listeners.
   * This constructor does not guaranty any calling order on inner listeners.
   */
  public CompositePermissionListener(PermissionListener... listeners) {
    this(Arrays.asList(listeners));
  }

  /**
   * Creates a {@link CompositePermissionListener} containing all the provided listeners.
   * This constructor will guaranty that inner listeners are called following the iterator order
   * of the collection.
   */
  public CompositePermissionListener(Collection<PermissionListener> listeners) {
    this.listeners = listeners;
  }

  @Override public void onPermissionGranted(PermissionGrantedResponse response) {
    for (PermissionListener listener : listeners) {
      listener.onPermissionGranted(response);
    }
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    for (PermissionListener listener : listeners) {
      listener.onPermissionDenied(response);
    }
  }

  @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
      PermissionToken token) {
    for (PermissionListener listener : listeners) {
      listener.onPermissionRationaleShouldBeShown(permission, token);
    }
  }
}

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

package com.karumi.dexter.listener.multi;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Listener that composes multiple listeners into one
 * All inner listeners will be called for a given event unless one of them throws an exception or
 * is blocked
 */
public class CompositeMultiplePermissionsListener implements MultiplePermissionsListener {

  private final Collection<MultiplePermissionsListener> listeners;

  /**
   * Creates a {@link CompositeMultiplePermissionsListener} containing all the provided listeners.
   * This constructor does not guaranty any calling order on inner listeners.
   */
  public CompositeMultiplePermissionsListener(MultiplePermissionsListener... listeners) {
    this(Arrays.asList(listeners));
  }

  /**
   * Creates a {@link CompositeMultiplePermissionsListener} containing all the provided listeners.
   * This constructor will guaranty that inner listeners are called following the iterator order
   * of the collection.
   */
  public CompositeMultiplePermissionsListener(Collection<MultiplePermissionsListener> listeners) {
    this.listeners = listeners;
  }

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
    for (MultiplePermissionsListener listener : listeners) {
      listener.onPermissionsChecked(report);
    }
  }

  @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
      PermissionToken token) {
    for (MultiplePermissionsListener listener : listeners) {
      listener.onPermissionRationaleShouldBeShown(permissions, token);
    }
  }
}

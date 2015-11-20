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

import com.karumi.dexter.PermissionToken;
import java.util.Arrays;
import java.util.Collection;

/**
 * Listener that composes multiple listeners into one.
 * All inner listeners will be called for a given event unless one of them throws an exception or
 * is blocked.
 */
public class MultiPermissionListener implements PermissionListener {

  private final Collection<PermissionListener> listeners;

  /**
   * Creates a {@link MultiPermissionListener} containing all the provided listeners.
   * This constructor does not guaranty any calling order on inner listeners.
   */
  public MultiPermissionListener(PermissionListener... listeners) {
    this(Arrays.asList(listeners));
  }

  /**
   * Creates a {@link MultiPermissionListener} containing all the provided listeners.
   * This constructor will guaranty that inner listeners are called following the iterator order
   * of the collection.
   */
  public MultiPermissionListener(Collection<PermissionListener> listeners) {
    this.listeners = listeners;
  }

  @Override public void onPermissionGranted(String permission) {
    for (PermissionListener listener : listeners) {
      listener.onPermissionGranted(permission);
    }
  }

  @Override
  public void onFirstTimePermissionDenied(String permission, PermissionToken token) {
    for (PermissionListener listener : listeners) {
      listener.onFirstTimePermissionDenied(permission, token);
    }
  }

  @Override public void onPermissionDenied(String permission) {
    for (PermissionListener listener : listeners) {
      listener.onPermissionDenied(permission);
    }
  }

  @Override
  public void onPermissionRationaleShouldBeShown(String permission, PermissionToken token) {
    for (PermissionListener listener : listeners) {
      listener.onPermissionRationaleShouldBeShown(permission, token);
    }
  }
}

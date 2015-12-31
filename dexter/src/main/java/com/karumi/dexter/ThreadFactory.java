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

import android.os.Looper;

/**
 * Factory to create the different thread implementations
 */
final class ThreadFactory {

  /**
   * Create a thread to execute on the main thread
   */
  public static Thread makeMainThread() {
    return new MainThread();
  }

  /**
   * Create a thread to execute on the same thread that this method is executed on
   */
  public static Thread makeSameThread() {
    if (runningMainThread()) {
      return new MainThread();
    } else {
      return new WorkerThread();
    }
  }

  private static boolean runningMainThread() {
    return Looper.getMainLooper().getThread() == java.lang.Thread.currentThread();
  }
}

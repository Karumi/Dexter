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

import android.os.Handler;
import android.os.Looper;

/**
 * A thread to execute passed runnable objects in the main thread
 */
final class MainThread implements Thread {

  MainThread() {
  }

  @Override public void execute(Runnable runnable) {
    if (runningMainThread()) {
      runnable.run();
    } else {
      new Handler(Looper.getMainLooper()).post(runnable);
    }
  }

  @Override public void loop() {
  }

  private static boolean runningMainThread() {
    return Looper.getMainLooper() == Looper.myLooper();
  }
}

package com.karumi.dexter.listener.threaddecorator;

import android.os.Looper;

public class ThreadSpecFactory {

  public static ThreadSpec makeThreadSpec() {
    if (runningMainThread()) {
      return new MainThreadSpec();
    } else {
      return new WorkerThreadSpec();
    }
  }

  private static boolean runningMainThread() {
    return Looper.getMainLooper() != Looper.myLooper();
  }
}

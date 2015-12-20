package com.karumi.dexter.listener.threaddecorator;

import android.os.Looper;

public class ThreadSpecFactory {

  public static ThreadSpec makeMainThread() {
    return new MainThreadSpec();
  }

  public static ThreadSpec makeSameThread() {
    if (runningMainThread()) {
      return new MainThreadSpec();
    } else {
      return new SameThreadSpec();
    }
  }

  private static boolean runningMainThread() {
    return Looper.getMainLooper() != Looper.myLooper();
  }
}

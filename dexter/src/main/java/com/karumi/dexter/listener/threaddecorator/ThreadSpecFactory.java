package com.karumi.dexter.listener.threaddecorator;

import android.os.Looper;

/**
 * Factory to create the different thread specifications
 */
public class ThreadSpecFactory {

  /**
   * Create a thread spec to execute on the main thread
   */
  public static ThreadSpec makeMainThreadSpec() {
    return new MainThreadSpec();
  }

  /**
   * Create a thread spec to execute on the same thread that this method is executed on
   */
  public static ThreadSpec makeSameThreadSpec() {
    if (runningMainThread()) {
      return new MainThreadSpec();
    } else {
      return new WorkerThreadSpec();
    }
  }

  private static boolean runningMainThread() {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }
}

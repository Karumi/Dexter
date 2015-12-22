package com.karumi.dexter.listener.threaddecorator;

import android.os.Handler;
import android.os.Looper;

public class MainThreadSpec implements ThreadSpec {

  private Handler handler;

  MainThreadSpec() {
    handler = new Handler(Looper.getMainLooper());
  }

  @Override public void execute(Runnable runnable) {
    if (runningMainThread()) {
      runnable.run();
    } else {
      handler.post(runnable);
    }
  }

  private boolean runningMainThread() {
    return Looper.getMainLooper() == Looper.myLooper();
  }
}

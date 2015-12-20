package com.karumi.dexter.listener.threaddecorator;

import android.os.Handler;
import android.os.Looper;

public class SameThreadSpec implements ThreadSpec {

  private Handler handler;

  SameThreadSpec() {
    prepareLooperIfNotMainThread();
    handler = new Handler(Looper.myLooper());
  }

  @Override public void execute(Runnable runnable) {
    if (runningBackgroundThread()) {
      handler.post(runnable);
    } else {
      runnable.run();
    }
  }

  @Override public void onChangingThread() {
    loopLooperIfNotMainThread();
  }

  private void prepareLooperIfNotMainThread() {
    if (runningBackgroundThread()) {
      Looper.prepare();
    }
  }

  private boolean runningBackgroundThread() {
    return Looper.getMainLooper() != Looper.myLooper();
  }

  private void loopLooperIfNotMainThread() {
    if (runningBackgroundThread()) {
      Looper.loop();
    }
  }
}

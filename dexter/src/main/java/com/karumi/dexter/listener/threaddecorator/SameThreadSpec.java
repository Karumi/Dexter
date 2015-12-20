package com.karumi.dexter.listener.threaddecorator;

import android.os.Handler;
import android.os.Looper;

public class SameThreadSpec implements ThreadSpec {

  private Handler handler;

  public SameThreadSpec() {
    prepareLooperIfNotMainThread();
    handler = new Handler();
  }

  @Override public void execute(Runnable runnable) {
    handler.post(runnable);
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

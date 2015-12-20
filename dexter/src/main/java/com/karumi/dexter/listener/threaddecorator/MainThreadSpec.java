package com.karumi.dexter.listener.threaddecorator;

import android.os.Handler;
import android.os.Looper;

public class MainThreadSpec implements ThreadSpec {

  private Handler handler;

  public MainThreadSpec() {
    handler = new Handler(Looper.getMainLooper());
  }

  @Override public void execute(Runnable runnable) {
    handler.post(runnable);
  }

  @Override public void onChangingThread() {

  }
}

package com.karumi.dexter.listener.threaddecorator;

import android.os.Handler;
import android.os.Looper;

public class WorkerThreadSpec implements ThreadSpec {

  private final Handler handler;

  WorkerThreadSpec() {
    handler = new Handler(Looper.myLooper());
  }

  @Override public void execute(Runnable runnable) {
    Looper.prepare();
    handler.post(runnable);
    Looper.loop();
  }
}

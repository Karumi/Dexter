package com.karumi.dexter.listener.threaddecorator;

import android.os.Handler;
import android.os.Looper;

public class WorkerThreadSpec implements ThreadSpec {

  private final Handler handler;

  WorkerThreadSpec() {
    Looper.prepare();
    handler = new Handler(Looper.myLooper());
  }

  @Override public void execute(Runnable runnable) {
    handler.post(runnable);
    Looper.loop();
  }
}

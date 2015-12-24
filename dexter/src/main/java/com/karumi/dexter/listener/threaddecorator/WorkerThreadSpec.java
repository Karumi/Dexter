package com.karumi.dexter.listener.threaddecorator;

import android.os.Handler;
import android.os.Looper;

/**
 * A thread specification to execute passed runnable objects in a worker thread
 */
public class WorkerThreadSpec implements ThreadSpec {

  private final Handler handler;

  WorkerThreadSpec() {
    Looper.prepare();
    handler = new Handler();
  }

  @Override public void execute(final Runnable runnable) {
    handler.post(runnable);
  }

  @Override public void loop() {
    Looper.loop();
  }
}

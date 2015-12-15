/*
 * Copyright (C) 2015 Karumi.
 */

package com.karumi.dexter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class to execute tasks in a background thread with the ability to wait for all the tasks to
 * finish.
 */
public class AsyncExecutor {

  private static final int MAX_NUMBER_OF_WAITING_SECONDS = 5;

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  public void execute(Runnable runnable) {
    if (!executor.isShutdown()) {
      executor.execute(runnable);
    }
  }

  public void waitForExecution() throws InterruptedException {
    executor.shutdown();
    executor.awaitTermination(MAX_NUMBER_OF_WAITING_SECONDS, TimeUnit.SECONDS);
  }
}

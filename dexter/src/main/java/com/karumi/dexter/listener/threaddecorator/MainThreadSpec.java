package com.karumi.dexter.listener.threaddecorator;

/**
 * A thread specification to execute passed runnable objects in the main thread
 */
public class MainThreadSpec implements ThreadSpec {

  MainThreadSpec() {
  }

  @Override public void execute(Runnable runnable) {
    runnable.run();
  }

  @Override public void loop() {
  }

}

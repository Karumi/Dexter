package com.karumi.dexter.listener.threaddecorator;

public class TestThreadSpec implements ThreadSpec {
  @Override public void execute(Runnable runnable) {
    runnable.run();
  }

  @Override public void loop() {

  }
}

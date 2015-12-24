package com.karumi.dexter.listener.threaddecorator;

public class MainThreadSpec implements ThreadSpec {

  MainThreadSpec() {
  }

  @Override public void execute(Runnable runnable) {
    runnable.run();
  }

  @Override public void loop() {
  }

}

package com.karumi.dexter.listener.threaddecorator;

public interface ThreadSpec {

  void execute(Runnable runnable);

  void onChangingThread();
}

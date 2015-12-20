package com.karumi.dexter.listener.threaddecorator;

/**
 * Interface that represent a thread spec to execute passed runnable objects
 */
public interface ThreadSpec {

  void execute(Runnable runnable);

  void onChangingThread();
}

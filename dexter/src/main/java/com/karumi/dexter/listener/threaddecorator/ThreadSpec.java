package com.karumi.dexter.listener.threaddecorator;

/**
 * A thread specification to execute passed runnable objects in a certain thread
 */
public interface ThreadSpec {
  void execute(Runnable runnable);

  void loop();
}

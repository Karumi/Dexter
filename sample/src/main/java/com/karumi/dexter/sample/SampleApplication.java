/*
 * Copyright (C) 2015 Karumi.
 */

package com.karumi.dexter.sample;

import android.app.Application;
import com.karumi.dexter.Dexter;

/**
 * Sample application that initializes the Dexter library.
 */
public class SampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Dexter.initialize(this);
  }
}

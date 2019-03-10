package com.karumi.dexter.sample;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

public class SampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    configureLeakCanary();
  }

  private void configureLeakCanary() {
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return;
    }
    LeakCanary.install(this);
  }
}

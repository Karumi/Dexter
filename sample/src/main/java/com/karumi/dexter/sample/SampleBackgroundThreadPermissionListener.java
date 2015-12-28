package com.karumi.dexter.sample;

import android.os.Handler;
import android.os.Looper;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;

/**
 * Sample listener that shows how to handle permission request callbacks on a background thread
 */
public class SampleBackgroundThreadPermissionListener extends SamplePermissionListener {

  private static final int HEAVY_WORK_DURATION = 2000;

  private Handler handler = new Handler(Looper.getMainLooper());

  public SampleBackgroundThreadPermissionListener(SampleActivity activity) {
    super(activity);
  }

  @Override public void onPermissionGranted(final PermissionGrantedResponse response) {
    doSomeHeavyWork();
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionGranted(response);
      }
    });
  }

  @Override public void onPermissionDenied(final PermissionDeniedResponse response) {
    doSomeHeavyWork();
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionDenied(response);
      }
    });
  }

  private void doSomeHeavyWork() {
    try {
      Thread.sleep(HEAVY_WORK_DURATION);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

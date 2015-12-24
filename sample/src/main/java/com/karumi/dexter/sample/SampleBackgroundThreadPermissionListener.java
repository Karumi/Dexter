package com.karumi.dexter.sample;

import android.os.Handler;
import android.os.Looper;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;

public class SampleBackgroundThreadPermissionListener extends SamplePermissionListener {

  private Handler handler = new Handler(Looper.getMainLooper());

  public SampleBackgroundThreadPermissionListener(SampleActivity activity) {
    super(activity);
  }

  @Override public void onPermissionGranted(final PermissionGrantedResponse response) {
    //DO SOME HEAVY WORK
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionGranted(response);
      }
    });
  }

  @Override public void onPermissionDenied(final PermissionDeniedResponse response) {
    //DO SOME HEAVY WORK
    handler.post(new Runnable() {
      @Override public void run() {
        SampleBackgroundThreadPermissionListener.super.onPermissionDenied(response);
      }
    });
  }
}

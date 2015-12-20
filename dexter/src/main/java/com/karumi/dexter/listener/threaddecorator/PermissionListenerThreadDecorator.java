package com.karumi.dexter.listener.threaddecorator;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.List;

public class PermissionListenerThreadDecorator implements MultiplePermissionsListener {

  private final MultiplePermissionsListener listener;
  private final ThreadSpec threadSpec;

  public PermissionListenerThreadDecorator(MultiplePermissionsListener listener,
      ThreadSpec threadSpec) {
    this.threadSpec = threadSpec;
    this.listener = listener;
  }

  @Override
  public void onPermissionsChecked(final MultiplePermissionsReport multiplePermissionsReport) {
    threadSpec.execute(new Runnable() {
      @Override public void run() {
        listener.onPermissionsChecked(multiplePermissionsReport);
      }
    });
  }

  @Override
  public void onPermissionRationaleShouldBeShown(final List<PermissionRequest> permissions,
      final PermissionToken token) {
    listener.onPermissionRationaleShouldBeShown(permissions, token);
  }

  public void onChangingThread() {
    threadSpec.onChangingThread();
  }
}

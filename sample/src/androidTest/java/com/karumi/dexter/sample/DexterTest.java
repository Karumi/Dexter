package com.karumi.dexter.sample;

import android.Manifest;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DexterTest {

  private static final String TAG = "DexterTest";
  private final AtomicBoolean unblock = new AtomicBoolean(false);

  private final PermissionListener permissionListener = new BasePermissionListener() {
    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
      unblock();
    }

    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
      unblock();
    }
  };
  private final PermissionRequestErrorListener errorListener =
      new PermissionRequestErrorListener() {
        @Override public void onError(DexterError error) {
          Log.i(TAG, error.toString());
          unblock();
        }
      };

  @Rule public ActivityTestRule<SampleActivity> activityTestRule =
      new ActivityTestRule<>(SampleActivity.class);
  @Rule public GrantPermissionRule grantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.CAMERA);

  private Handler handler;
  private HandlerThread handlerThread;

  @Before public void setUp() throws Exception {
    handlerThread = new HandlerThread(TAG);
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @After public void tearDown() throws Exception {
    handlerThread.quit();
  }

  @Test public void testWithLooper() {
    AtomicBoolean milestone = new AtomicBoolean(false);

    requestAndAcceptPermissionOnHandlerThread(milestone, Manifest.permission.CAMERA);

    block();
    assertThat(milestone.get(), is(true));
  }

  private void getPermission(String permission) {
    Dexter.withActivity(activityTestRule.getActivity())
        .withPermission(permission)
        .withListener(permissionListener)
        .withErrorListener(errorListener)
        .onSameThread()
        .check();
  }

  private void requestAndAcceptPermissionOnHandlerThread(final AtomicBoolean milestone,
      final String permission) {
    handler.post(new Runnable() {
      @Override public void run() {
        getPermission(permission);
        milestone.set(true);
      }
    });
  }

  private void unblock() {
    unblock.set(true);
  }

  private void block() {
    await().atMost(20, TimeUnit.SECONDS).untilTrue(unblock);
    unblock.set(false);
  }
}

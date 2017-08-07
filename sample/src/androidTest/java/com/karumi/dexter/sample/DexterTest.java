package com.karumi.dexter.sample;

import android.Manifest;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class DexterTest {

    private static final String TAG = "DexterTest";
    private final AtomicBoolean unblock = new AtomicBoolean(false);
    private final PermissionListener permissionListener = new BasePermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {
            unblock();
        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {
            unblock();
        }
    };
    private final PermissionRequestErrorListener errorListener = new PermissionRequestErrorListener() {
        @Override
        public void onError(DexterError error) {
            Log.i(TAG, error.toString());
            unblock();
        }
    };
    @Rule
    public ActivityTestRule<SampleActivity> activityTestRule = new ActivityTestRule<>(SampleActivity.class);
    private Handler handler;
    private HandlerThread handlerThread;

    @Before
    public void setUp() throws Exception {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @After
    public void tearDown() throws Exception {
        handlerThread.quit();
    }

    @Test
    public void testWithLooper() {
        final AtomicBoolean milestone = new AtomicBoolean(false);
        handler.post(new Runnable() {
            @Override
            public void run() {
                getPermission(Manifest.permission.CAMERA);
                Log.d(TAG, "Permission are asked");
                milestone.set(true);
            }
        });
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

    private void unblock() {
        unblock.set(true);
    }

    private void block() {
        await().atMost(10, TimeUnit.MINUTES).untilTrue(unblock);
        unblock.set(false);
    }
}
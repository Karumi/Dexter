/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.dexter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.karumi.dexter.RetryCheckPermissionOnDeniedPermissionListener.CheckPermissionAction;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class DexterInstanceTest {

  private static final String ANY_PERMISSION = "noissimrep yna";
  private static final Thread THREAD = new TestThread();

  @Mock AndroidPermissionService androidPermissionService;
  @Mock Context context;
  @Mock Intent intent;
  @Mock Activity activity;
  @Mock MultiplePermissionsListener multiplePermissionsListener;
  @Mock PermissionListener permissionListener;

  private DexterInstance dexter;
  private AsyncExecutor asyncExecutor;

  @Before public void setUp() {
    IntentProvider intentProvider = new IntentMockProvider(intent);
    asyncExecutor = new AsyncExecutor();
    dexter = new DexterInstance(context, androidPermissionService, intentProvider);
  }

  @Test(expected = DexterException.class) public void onNoPermissionCheckedThenThrowException() {
    dexter.checkPermissions(multiplePermissionsListener, Collections.<String>emptyList(), THREAD);
  }

  @Test(expected = DexterException.class)
  public void onCheckPermissionMoreThanOnceThenThrowException() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    dexter.checkPermission(permissionListener, ANY_PERMISSION, THREAD);
    dexter.checkPermission(permissionListener, ANY_PERMISSION, THREAD);
  }

  @Test public void onPermissionAlreadyGrantedThenNotifiesListener() {
    givenPermissionIsAlreadyGranted(ANY_PERMISSION);

    dexter.checkPermission(permissionListener, ANY_PERMISSION, THREAD);

    thenPermissionIsGranted(ANY_PERMISSION);
  }

  @Test public void onShouldShowRationaleThenNotifiesListener() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    givenShouldShowRationaleForPermission(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);

    thenShouldShowRationaleForPermission(ANY_PERMISSION);
  }

  @Test public void onPermissionDeniedThenNotifiesListener() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    givenShouldShowRationaleForPermission(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);
    dexter.onPermissionRequestDenied(Collections.singletonList(ANY_PERMISSION));

    thenPermissionIsDenied(ANY_PERMISSION);
  }

  @Test public void onPermissionDeniedDoSequentialCheckPermissionThenNotifiesListener()
      throws InterruptedException {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    givenShouldShowRationaleForPermission(ANY_PERMISSION);
    PermissionListener checkPermissionOnDeniedPermissionListener =
        givenARetryCheckPermissionOnDeniedPermissionListener(permissionListener);

    whenCheckPermission(checkPermissionOnDeniedPermissionListener, ANY_PERMISSION);
    dexter.onPermissionRequestDenied(Collections.singletonList(ANY_PERMISSION));

    asyncExecutor.waitForExecution();
    thenPermissionIsDenied(ANY_PERMISSION);
  }

  @Test public void onPermissionPermanentlyDeniedThenNotifiesListener() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    givenPermissionIsPermanentlyDenied(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);
    dexter.onPermissionRequestDenied(Collections.singletonList(ANY_PERMISSION));

    thenPermissionIsPermanentlyDenied(ANY_PERMISSION);
  }

  @Test public void onPermissionFailedByRuntimeExceptionThenNotifiesListener() {
    givenPermissionIsChecked(ANY_PERMISSION, PackageManager.PERMISSION_DENIED);
    givenARuntimeExceptionIsThrownWhenPermissionIsChecked(ANY_PERMISSION);
    givenShouldShowRationaleForPermission(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);
    dexter.onPermissionRequestDenied(Collections.singletonList(ANY_PERMISSION));

    thenPermissionIsDenied(ANY_PERMISSION);
  }

  @Test(expected = DexterException.class) public void onCheckPermissionTwiceThenThrowException() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);
    whenCheckPermission(permissionListener, ANY_PERMISSION);

    verifyRequestPermissions(new String[]{ANY_PERMISSION}, 1);
  }

  @Test public void onCheckPermissionAgainAfterActivityDestroyedThenRequestedTwice() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);
    dexter.onActivityDestroyed();
    whenCheckPermission(permissionListener, ANY_PERMISSION);

    verifyRequestPermissions(new String[]{ANY_PERMISSION}, 2);
  }

  private void givenPermissionIsAlreadyDenied(String permission) {
    givenPermissionIsChecked(permission, PackageManager.PERMISSION_DENIED);
  }

  private void givenPermissionIsAlreadyGranted(String permission) {
    givenPermissionIsChecked(permission, PackageManager.PERMISSION_GRANTED);
  }

  private void givenPermissionIsChecked(String permission, int permissionState) {
    when(androidPermissionService.checkSelfPermission(
        any(Context.class),
        eq(permission))
    ).thenReturn(permissionState);
  }

  private void givenShouldShowRationaleForPermission(String permission) {
    when(androidPermissionService.shouldShowRequestPermissionRationale(
        any(Activity.class),
        eq(permission))
    ).thenReturn(true);
  }

  private void givenShouldNotShowRationaleForPermission(String permission) {
    when(androidPermissionService.shouldShowRequestPermissionRationale(
        any(Activity.class),
        eq(permission))
    ).thenReturn(false);
  }

  private void givenPermissionIsPermanentlyDenied(String permission) {
    when(androidPermissionService.isPermissionPermanentlyDenied(
        any(Activity.class),
        eq(permission))
    ).thenReturn(true);
  }

  private PermissionListener givenARetryCheckPermissionOnDeniedPermissionListener(
      PermissionListener permissionListener) {
    return new RetryCheckPermissionOnDeniedPermissionListener(permissionListener,
        new CheckPermissionWithOnActivityReadyInBackground());
  }

  private void givenARuntimeExceptionIsThrownWhenPermissionIsChecked(String permission) {
    when(androidPermissionService.checkSelfPermission(activity, permission)).thenThrow(
        new RuntimeException());
  }

  private void whenCheckPermission(PermissionListener permissionListener, String permission) {
    dexter.checkPermission(permissionListener, permission, THREAD);
    dexter.onActivityReady(activity);
  }

  private void verifyRequestPermissions(String[] permissions, int nTimes) {
    verify(androidPermissionService, times(nTimes)).requestPermissions(eq(activity), eq(permissions), anyInt());
  }

  private void thenPermissionIsGranted(String permission) {
    verify(permissionListener).onPermissionGranted(
        argThat(getPermissionGrantedResponseMatcher(permission)));
  }

  private void thenPermissionIsDenied(String permission) {
    verify(permissionListener).onPermissionDenied(
        argThat(getPermissionDeniedResponseMatcher(permission, false)));
  }

  private void thenPermissionIsPermanentlyDenied(String permission) {
    verify(permissionListener).onPermissionDenied(
        argThat(getPermissionDeniedResponseMatcher(permission, true)));
  }

  private void thenShouldShowRationaleForPermission(String permission) {
    verify(permissionListener).onPermissionRationaleShouldBeShown(
        argThat(getPermissionRequestShouldShowTokenMatcher(permission)),
        isA(PermissionToken.class));
  }

  private void thenPermissionRationaleIsShown(int times) {
    verify(permissionListener, times(times)).onPermissionRationaleShouldBeShown(
        isA(PermissionRequest.class), isA(PermissionToken.class));
  }

  private static ArgumentMatcher<PermissionGrantedResponse> getPermissionGrantedResponseMatcher(
      final String permission) {
    return new ArgumentMatcher<PermissionGrantedResponse>() {
      @Override public boolean matches(PermissionGrantedResponse response) {
        return permission.equals(response.getPermissionName());
      }
    };
  }

  private static ArgumentMatcher<PermissionDeniedResponse> getPermissionDeniedResponseMatcher(
      final String permission, final boolean isPermanentlyDenied) {
    return new ArgumentMatcher<PermissionDeniedResponse>() {
      @Override public boolean matches(PermissionDeniedResponse response) {
        return permission.equals(response.getPermissionName())
            && response.isPermanentlyDenied() == isPermanentlyDenied;
      }
    };
  }

  private static ArgumentMatcher<PermissionRequest> getPermissionRequestShouldShowTokenMatcher(
      final String permission) {
    return new ArgumentMatcher<PermissionRequest>() {
      @Override public boolean matches(PermissionRequest request) {
        return permission.equals(request.getName());
      }
    };
  }

  private static class IntentMockProvider extends IntentProvider {
    private final Intent intent;

    IntentMockProvider(Intent intent) {
      this.intent = intent;
    }

    @Override public Intent get(Context context, Class<?> clazz) {
      return intent;
    }
  }

  private class CheckPermissionWithOnActivityReadyInBackground implements CheckPermissionAction {
    @Override public void check(final PermissionListener listener, final String permission) {
      dexter.checkPermission(listener, permission, THREAD);
      asyncExecutor.execute(new Runnable() {
        @Override public void run() {
          dexter.onActivityReady(activity);
          dexter.onPermissionRequestDenied(Collections.singletonList(permission));
        }
      });
    }
  }

  private static class TestThread implements Thread {

    @Override public void execute(Runnable runnable) {
      runnable.run();
    }

    @Override public void loop() {

    }
  }
}

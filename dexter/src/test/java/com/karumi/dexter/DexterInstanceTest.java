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
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class DexterInstanceTest {

  private static final String ANY_PERMISSION = "noissimrep yna";

  @Mock AndroidPermissionService androidPermissionService;
  @Mock Context context;
  @Mock Intent intent;
  @Mock Activity activity;
  @Mock MultiplePermissionsListener multiplePermissionsListener;
  @Mock PermissionListener permissionListener;

  protected DexterInstance dexter;
  protected AsyncExecutor asyncExecutor;

  @Before public void setUp() {
    IntentProvider intentProvider = new IntentMockProvider(intent);
    Context mockApplicationContext = mock(Context.class);
    when(context.getApplicationContext()).thenReturn(mockApplicationContext);
    asyncExecutor = new AsyncExecutor();
    dexter = new DexterInstance(context, androidPermissionService, intentProvider);
  }

  @Test(expected = IllegalStateException.class)
  public void onNoPermissionCheckedThenThrowException() {
    dexter.checkPermissions(multiplePermissionsListener, Collections.<String>emptyList());
  }

  @Test(expected = IllegalStateException.class)
  public void onCheckPermissionMoreThanOnceThenThrowException() {
    dexter.checkPermission(permissionListener, ANY_PERMISSION);
    dexter.checkPermission(permissionListener, ANY_PERMISSION);
  }

  @Test public void onPermissionAlreadyGrantedThenNotifiesListener() {
    givenPermissionIsAlreadyGranted(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);

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
    givenShouldNotShowRationaleForPermission(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);
    dexter.onPermissionRequestDenied(Collections.singletonList(ANY_PERMISSION));

    thenPermissionIsPermanentlyDenied(ANY_PERMISSION);
  }

  @Test public void onPermissionsPendingThenShouldNotShowPermissionRationaleTwice() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    givenShouldShowRationaleForPermission(ANY_PERMISSION);

    whenCheckPermission(permissionListener, ANY_PERMISSION);
    whenContinueWithTheCheckPermissionProcess(permissionListener);

    thenPermissionRationaleIsShown(2);
  }

  private void givenPermissionIsAlreadyDenied(String permission) {
    givenPermissionIsChecked(permission, PackageManager.PERMISSION_DENIED);
  }

  private void givenPermissionIsAlreadyGranted(String permission) {
    givenPermissionIsChecked(permission, PackageManager.PERMISSION_GRANTED);
  }

  private void givenPermissionIsChecked(String permission, int permissionState) {
    when(androidPermissionService.checkSelfPermission(activity, permission)).thenReturn(
        permissionState);
  }

  private void givenShouldShowRationaleForPermission(String permission) {
    when(androidPermissionService.shouldShowRequestPermissionRationale(activity,
        permission)).thenReturn(true);
  }

  private void givenShouldNotShowRationaleForPermission(String permission) {
    when(androidPermissionService.shouldShowRequestPermissionRationale(activity,
        permission)).thenReturn(false);
  }

  private PermissionListener givenARetryCheckPermissionOnDeniedPermissionListener(
      PermissionListener permissionListener) {
    return new RetryCheckPermissionOnDeniedPermissionListener(permissionListener,
        new CheckPermissionWithOnActivityReadyInBackground());
  }

  private void whenCheckPermission(PermissionListener permissionListener, String permission) {
    dexter.checkPermission(permissionListener, permission);
    dexter.onActivityReady(activity);
  }

  private void whenContinueWithTheCheckPermissionProcess(PermissionListener permissionListener) {
    dexter.continuePendingRequestIfPossible(permissionListener);
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
      @Override public boolean matches(Object argument) {
        PermissionGrantedResponse response = (PermissionGrantedResponse) argument;
        return permission.equals(response.getPermissionName());
      }
    };
  }

  private static ArgumentMatcher<PermissionDeniedResponse> getPermissionDeniedResponseMatcher(
      final String permission, final boolean isPermanentlyDenied) {
    return new ArgumentMatcher<PermissionDeniedResponse>() {
      @Override public boolean matches(Object argument) {
        PermissionDeniedResponse response = (PermissionDeniedResponse) argument;
        return permission.equals(response.getPermissionName())
            && response.isPermanentlyDenied() == isPermanentlyDenied;
      }
    };
  }

  private static ArgumentMatcher<PermissionRequest> getPermissionRequestShouldShowTokenMatcher(
      final String permission) {
    return new ArgumentMatcher<PermissionRequest>() {
      @Override public boolean matches(Object argument) {
        PermissionRequest request = (PermissionRequest) argument;
        return permission.equals(request.getName());
      }
    };
  }

  private static class IntentMockProvider extends IntentProvider {
    private final Intent intent;

    public IntentMockProvider(Intent intent) {
      this.intent = intent;
    }

    @Override public Intent get(Context context, Class<?> clazz) {
      return intent;
    }
  }

  private class CheckPermissionWithOnActivityReadyInBackground implements CheckPermissionAction {
    @Override public void check(final PermissionListener listener, final String permission) {
      dexter.checkPermission(listener, permission);
      asyncExecutor.execute(new Runnable() {
        @Override public void run() {
          dexter.onActivityReady(activity);
          dexter.onPermissionRequestDenied(Collections.singletonList(permission));
        }
      });
    }
  }
}

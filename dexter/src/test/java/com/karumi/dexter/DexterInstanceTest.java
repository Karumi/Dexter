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
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DexterInstanceTest {

  private static final String ANY_PERMISSION = "noissimrep yna";

  @Mock AndroidPermissionService androidPermissionService;
  @Mock Context context;
  @Mock Intent intent;
  @Mock Activity activity;
  @Mock MultiplePermissionsListener multiplePermissionsListener;
  @Mock PermissionListener permissionListener;

  private DexterInstance dexter;

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    IntentProvider intentProvider = new IntentMockProvider(intent);
    dexter = new DexterInstance(context, androidPermissionService, intentProvider);
  }

  @Test(expected = IllegalStateException.class)
  public void onNoPermissionCheckedThenThrowException() {
    dexter.checkPermissions(Collections.<String>emptyList(), multiplePermissionsListener);
  }

  @Test(expected = IllegalStateException.class)
  public void onCheckPermissionMoreThanOnceThenThrowException() {
    dexter.checkPermission(ANY_PERMISSION, permissionListener);
    dexter.checkPermission(ANY_PERMISSION, permissionListener);
  }

  @Test public void onPermissionAlreadyGrantedThenNotifiesListener() {
    givenPermissionIsAlreadyGranted(ANY_PERMISSION);

    dexter.checkPermission(ANY_PERMISSION, permissionListener);
    dexter.onActivityCreated(activity);

    verify(permissionListener).onPermissionGranted(isA(PermissionGrantedResponse.class));
  }

  @Test public void onShouldShowRationaleThenNotifiesListener() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    givenShouldShowRationaleForPermission(ANY_PERMISSION);

    dexter.checkPermission(ANY_PERMISSION, permissionListener);
    dexter.onActivityCreated(activity);

    verify(permissionListener).onPermissionRationaleShouldBeShown(isA(PermissionRequest.class),
        isA(PermissionToken.class));
  }

  @Test public void onPermissionDeniedThenNotifiesListener() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);

    dexter.checkPermission(ANY_PERMISSION, permissionListener);
    dexter.onActivityCreated(activity);
    dexter.onPermissionRequestDenied(Collections.singletonList(ANY_PERMISSION));

    verify(permissionListener).onPermissionDenied(isA(PermissionDeniedResponse.class));
  }

  @Test public void onPermissionPermanentlyDeniedThenNotifiesListener() {
    givenPermissionIsAlreadyDenied(ANY_PERMISSION);
    givenShouldShowNotRationaleForPermission(ANY_PERMISSION);

    dexter.checkPermission(ANY_PERMISSION, permissionListener);
    dexter.onActivityCreated(activity);
    dexter.onPermissionRequestDenied(Collections.singletonList(ANY_PERMISSION));

    thenPermissionIsPermanentlyDenied();
  }

  private void givenPermissionIsAlreadyDenied(String permission) {
    givenPermissionIsChecked(permission, PackageManager.PERMISSION_DENIED);
  }

  private void givenPermissionIsAlreadyGranted(String permission) {
    givenPermissionIsChecked(permission, PackageManager.PERMISSION_GRANTED);
  }

  private void givenPermissionIsChecked(String permission, int permissionState) {
    when(androidPermissionService.checkSelfPermission(activity, permission)).thenReturn(permissionState);
  }

  private void givenShouldShowRationaleForPermission(String permission) {
    when(androidPermissionService.shouldShowRequestPermissionRationale(activity, permission)).thenReturn(
        true);
  }

  private void givenShouldShowNotRationaleForPermission(String permission) {
    when(androidPermissionService.shouldShowRequestPermissionRationale(activity, permission)).thenReturn(
        false);
  }

  private void thenPermissionIsPermanentlyDenied() {
    verify(permissionListener).onPermissionDenied(
        argThat(getPermissionPermanentlyDeniedResponse()));
  }

  private static ArgumentMatcher<PermissionDeniedResponse> getPermissionPermanentlyDeniedResponse() {
    return new ArgumentMatcher<PermissionDeniedResponse>() {
      @Override public boolean matches(Object argument) {
        PermissionDeniedResponse response = (PermissionDeniedResponse) argument;
        return response.isPermanentlyDenied();
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
}

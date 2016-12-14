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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.LOCATION_HARDWARE;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Test AndroidPermissionService
 */
@RunWith(MockitoJUnitRunner.class)
public class AndroidPermissionServiceTest {

    @Mock
    Context context;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Test
    public void testCheckSelfPermissionUnknownPermission() {
        AndroidPermissionService permissionService = spy(new AndroidPermissionService(ICE_CREAM_SANDWICH));
        final int permissionResult = permissionService.checkSelfPermission(context, LOCATION_HARDWARE);
        assertEquals(PackageManager.PERMISSION_GRANTED, permissionResult);
        verify(permissionService, never()).supportCheckSelfPermission(any(Context.class), anyString());
    }

    @Test
    public void testCheckSelfPermissionKnownPermissionGranted() {
        AndroidPermissionService permissionService = spy(new AndroidPermissionService(ICE_CREAM_SANDWICH));
        doReturn(PackageManager.PERMISSION_GRANTED).when(permissionService)
                .supportCheckSelfPermission(context, ACCESS_COARSE_LOCATION);
        final int permissionResult = permissionService.checkSelfPermission(context, ACCESS_COARSE_LOCATION);
        assertEquals(PackageManager.PERMISSION_GRANTED, permissionResult);
        verify(permissionService).supportCheckSelfPermission(any(Context.class), anyString());
    }

    @Test
    public void testCheckSelfPermissionKnownPermissionDenied() {
        AndroidPermissionService permissionService = spy(new AndroidPermissionService(ICE_CREAM_SANDWICH));
        doReturn(PackageManager.PERMISSION_DENIED).when(permissionService)
                .supportCheckSelfPermission(context, ACCESS_COARSE_LOCATION);
        final int permissionResult = permissionService.checkSelfPermission(context, ACCESS_COARSE_LOCATION);
        assertEquals(PackageManager.PERMISSION_DENIED, permissionResult);
        verify(permissionService).supportCheckSelfPermission(any(Context.class), anyString());
    }
}
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

import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class MultiplePermissionsReportTest {

    private static final PermissionGrantedResponse GRANTED_RESPONSE =
            PermissionGrantedResponse.from("CAMERA");
    private static final PermissionGrantedResponse OTHER_GRANTED_RESPONSE =
            PermissionGrantedResponse.from("CONTACTS");
    private static final PermissionDeniedResponse DENIED_RESPONSE =
            PermissionDeniedResponse.from("MICROPHONE", true);
    private static final PermissionDeniedResponse OTHER_DENIED_RESPONSE =
            PermissionDeniedResponse.from("STORAGE", false);

    @Test
    public void shouldReplaceOldPermissionGrantedReportsWithTheNewOnes() {
        MultiplePermissionsReport report = new MultiplePermissionsReport();

        report.addGrantedPermissionResponse(GRANTED_RESPONSE);
        report.addGrantedPermissionResponse(OTHER_GRANTED_RESPONSE);
        report.addGrantedPermissionResponse(GRANTED_RESPONSE);

        List<PermissionGrantedResponse> expectedPermissions = new LinkedList<>();
        expectedPermissions.add(GRANTED_RESPONSE);
        expectedPermissions.add(OTHER_GRANTED_RESPONSE);
        assertEquals(expectedPermissions, report.getGrantedPermissionResponses());
    }

    @Test
    public void shouldReplaceOldPermissionDeniedReportsWithTheNewOnes() {
        MultiplePermissionsReport report = new MultiplePermissionsReport();

        report.addDeniedPermissionResponse(DENIED_RESPONSE);
        report.addDeniedPermissionResponse(OTHER_DENIED_RESPONSE);
        report.addDeniedPermissionResponse(DENIED_RESPONSE);

        List<PermissionDeniedResponse> expectedPermissions = new LinkedList<>();
        expectedPermissions.add(DENIED_RESPONSE);
        expectedPermissions.add(OTHER_DENIED_RESPONSE);
        assertEquals(expectedPermissions, report.getDeniedPermissionResponses());
    }
}

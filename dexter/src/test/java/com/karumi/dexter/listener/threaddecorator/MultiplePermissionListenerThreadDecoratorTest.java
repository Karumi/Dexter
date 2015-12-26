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

package com.karumi.dexter.listener.threaddecorator;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class) public class MultiplePermissionListenerThreadDecoratorTest {

  private static final DummyPermissionToken TOKEN = new DummyPermissionToken();
  private static final List<PermissionRequest> PERMISSIONS =
      singletonList(new PermissionRequest(""));

  @Mock MultiplePermissionsListener listener;

  private SpyThreadSpec threadSpec;
  private MultiplePermissionListenerThreadDecorator decorator;

  @Before public void setUp() {
    threadSpec = new SpyThreadSpec();
    decorator = new MultiplePermissionListenerThreadDecorator(listener, threadSpec);
  }

  @Test public void onPermissionCheckedThenDecorateListenerOnPermissionCheckedWithThreadSpec() {
    decorator.onPermissionsChecked(null);

    thenDecorateOnPermissionsCheckedWithThreadSpec();
  }

  @Test public void onPermissionRationaleShouldBeShownThenDoNoDecorateListener() {
    decorator.onPermissionRationaleShouldBeShown(PERMISSIONS, TOKEN);

    thenNoDecorateOnPermissionRationaleShouldBeShown();
  }

  private void thenDecorateOnPermissionsCheckedWithThreadSpec() {
    verify(listener).onPermissionsChecked(any(MultiplePermissionsReport.class));
    assertTrue(threadSpec.decorated);
  }

  private void thenNoDecorateOnPermissionRationaleShouldBeShown() {
    verify(listener).onPermissionRationaleShouldBeShown(PERMISSIONS, TOKEN);
    assertFalse(threadSpec.decorated);
  }

  private static class DummyPermissionToken implements PermissionToken {

    @Override public void continuePermissionRequest() {

    }

    @Override public void cancelPermissionRequest() {

    }
  }

  private static class SpyThreadSpec implements ThreadSpec {

    public boolean decorated;

    @Override public void execute(Runnable runnable) {
      decorated = true;
      runnable.run();
    }

    @Override public void loop() {

    }
  }
}
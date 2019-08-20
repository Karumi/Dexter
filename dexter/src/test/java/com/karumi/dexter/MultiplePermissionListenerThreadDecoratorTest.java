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

import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class) public class MultiplePermissionListenerThreadDecoratorTest {

  private static final DummyPermissionToken TOKEN = new DummyPermissionToken();
  private static final List<PermissionRequest> PERMISSIONS =
      singletonList(new PermissionRequest(""));

  @Mock MultiplePermissionsListener listener;

  private SpyThread thread;
  private MultiplePermissionListenerThreadDecorator decorator;

  @Before public void setUp() {
    thread = new SpyThread();
    decorator = new MultiplePermissionListenerThreadDecorator(listener, thread);
  }

  @Test public void onPermissionCheckedThenListenerIsDecorated() {
    decorator.onPermissionsChecked(null);

    verify(listener).onPermissionsChecked(null);
    assertTrue(thread.decorated);
  }

  @Test public void onPermissionRationaleShouldBeShownThenListenerIsDecorated() {
    decorator.onPermissionRationaleShouldBeShown(PERMISSIONS, TOKEN);

    verify(listener).onPermissionRationaleShouldBeShown(PERMISSIONS, TOKEN);
    assertTrue(thread.decorated);
  }

  private static class DummyPermissionToken implements PermissionToken {

    @Override public void continuePermissionRequest() {

    }

    @Override public void cancelPermissionRequest() {

    }
  }

  private static class SpyThread implements Thread {

    public boolean decorated;

    @Override public void execute(Runnable runnable) {
      decorated = true;
      runnable.run();
    }

    @Override public void loop() {

    }
  }
}
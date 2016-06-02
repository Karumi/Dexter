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

package com.karumi.dexter.sample;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity to choose applications options.
 */
public class ChooserActivity extends Activity {

  private static final int TIME_TO_RESTART = 50;
  private static final int INTENT_RESTART_ID = 1234;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chooser_activity);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.bt_common_sample)
  public void openCommonSample() {
    SampleActivity.open(this);
  }

  @OnClick(R.id.bt_oncreate_sample)
  public void openOncreateSample() {
    OnCreateSampleActivity.open(this);
  }

  @OnClick(R.id.bt_application_sample)
  public void openApplicationSample() {
    DexterApplicationMode.enable(this);
    relaunchApplicationToSample();
  }

  private void relaunchApplicationToSample() {
    Intent intentRestart = new Intent(this, ApplicationSampleActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, INTENT_RESTART_ID, intentRestart,
        PendingIntent.FLAG_CANCEL_CURRENT);
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + TIME_TO_RESTART, pendingIntent);
    closeApplication();
  }

  private void closeApplication() {
    System.exit(0);
  }
}

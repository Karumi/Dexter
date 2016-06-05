/*
 * Copyright (C) 2016 Karumi.
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

import android.content.Context;
import android.content.SharedPreferences;

public class DexterApplicationMode {

  public static final String APPLICATION_SETTING = "applicationSetting";
  private static final String IS_ENABLE = "IS_ENABLE";

  public static boolean isEnable(Context context) {
    SharedPreferences sharedpreferences = getSharedPreferences(context);
    return sharedpreferences.getBoolean(IS_ENABLE, false);
  }

  public static void clear(Context context) {
    SharedPreferences sharedpreferences = getSharedPreferences(context);
    sharedpreferences.edit().clear().commit();
  }

  public static void enable(Context context) {
    SharedPreferences sharedpreferences = getSharedPreferences(context);
    SharedPreferences.Editor editor = sharedpreferences.edit();
    editor.putBoolean(IS_ENABLE, true);
    editor.commit();
  }

  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(APPLICATION_SETTING, Context.MODE_PRIVATE);
  }
}

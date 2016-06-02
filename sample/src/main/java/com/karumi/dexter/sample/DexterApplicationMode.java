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

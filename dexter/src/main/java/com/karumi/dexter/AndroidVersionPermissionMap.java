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

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Maps the permission of the permissions with the Android versions that started including them to consider
 * unknown permissions as granted to solve a bug in the support library for versions older than Marshmallow.
 * <p/>
 * https://commonsware.com/blog/2015/11/09/you-cannot-hold-nonexistent-permissions.html
 */
public class AndroidVersionPermissionMap {

    /**
     * Check if a specific platform can handle certain permission.
     *
     * @param sdkVersion Specific version of Android SDK
     * @param permission Permission to check.
     * @return True if the platform can handle it, false otherwise.
     */
    @SuppressWarnings("deprecation")
    static boolean isHandledPermission(int sdkVersion, @NonNull String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_CHECKIN_PROPERTIES:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS:
            case Manifest.permission.ACCESS_NETWORK_STATE:
            case Manifest.permission.ACCESS_WIFI_STATE:
            case Manifest.permission.BATTERY_STATS:
            case Manifest.permission.BLUETOOTH:
            case Manifest.permission.BLUETOOTH_ADMIN:
            case Manifest.permission.BROADCAST_PACKAGE_REMOVED:
            case Manifest.permission.BROADCAST_STICKY:
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.CALL_PRIVILEGED:
            case Manifest.permission.CAMERA:
            case Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE:
            case Manifest.permission.CHANGE_CONFIGURATION:
            case Manifest.permission.CHANGE_NETWORK_STATE:
            case Manifest.permission.CHANGE_WIFI_STATE:
            case Manifest.permission.CLEAR_APP_CACHE:
            case Manifest.permission.CONTROL_LOCATION_UPDATES:
            case Manifest.permission.DELETE_CACHE_FILES:
            case Manifest.permission.DELETE_PACKAGES:
            case Manifest.permission.DIAGNOSTIC:
            case Manifest.permission.DISABLE_KEYGUARD:
            case Manifest.permission.DUMP:
            case Manifest.permission.EXPAND_STATUS_BAR:
            case Manifest.permission.FACTORY_TEST:
            case Manifest.permission.FLASHLIGHT:
            case Manifest.permission.GET_ACCOUNTS:
            case Manifest.permission.GET_PACKAGE_SIZE:
            case Manifest.permission.GET_TASKS:
            case Manifest.permission.INSTALL_PACKAGES:
            case Manifest.permission.INTERNET:
            case Manifest.permission.MASTER_CLEAR:
            case Manifest.permission.MODIFY_AUDIO_SETTINGS:
            case Manifest.permission.MODIFY_PHONE_STATE:
            case Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS:
            case Manifest.permission.PERSISTENT_ACTIVITY:
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.READ_FRAME_BUFFER:
            case Manifest.permission.READ_INPUT_STATE:
            case Manifest.permission.READ_LOGS:
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.READ_SMS:
            case Manifest.permission.READ_SYNC_SETTINGS:
            case Manifest.permission.READ_SYNC_STATS:
            case Manifest.permission.REBOOT:
            case Manifest.permission.RECEIVE_BOOT_COMPLETED:
            case Manifest.permission.RECEIVE_MMS:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
            case Manifest.permission.RECORD_AUDIO:
            case Manifest.permission.REORDER_TASKS:
            case Manifest.permission.RESTART_PACKAGES:
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.SET_ALWAYS_FINISH:
            case Manifest.permission.SET_ANIMATION_SCALE:
            case Manifest.permission.SET_DEBUG_APP:
            case Manifest.permission.SET_PREFERRED_APPLICATIONS:
            case Manifest.permission.SET_PROCESS_LIMIT:
            case Manifest.permission.SET_TIME_ZONE:
            case Manifest.permission.SET_WALLPAPER:
            case Manifest.permission.SET_WALLPAPER_HINTS:
            case Manifest.permission.SIGNAL_PERSISTENT_PROCESSES:
            case Manifest.permission.STATUS_BAR:
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
            case Manifest.permission.VIBRATE:
            case Manifest.permission.WAKE_LOCK:
            case Manifest.permission.WRITE_APN_SETTINGS:
            case Manifest.permission.WRITE_CALENDAR:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.WRITE_GSERVICES:
            case Manifest.permission.WRITE_SETTINGS:
            case Manifest.permission.WRITE_SYNC_SETTINGS:
                return true; //API 1
            case Manifest.permission.BROADCAST_SMS:
            case Manifest.permission.BROADCAST_WAP_PUSH:
                return sdkVersion >= Build.VERSION_CODES.BASE_1_1;
            case Manifest.permission.BIND_APPWIDGET:
            case Manifest.permission.BIND_INPUT_METHOD:
            case Manifest.permission.MOUNT_FORMAT_FILESYSTEMS:
            case Manifest.permission.UPDATE_DEVICE_STATS:
            case Manifest.permission.WRITE_SECURE_SETTINGS:
                return sdkVersion >= Build.VERSION_CODES.CUPCAKE;
            case Manifest.permission.CHANGE_WIFI_MULTICAST_STATE:
            case Manifest.permission.GLOBAL_SEARCH:
            case Manifest.permission.INSTALL_LOCATION_PROVIDER:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return sdkVersion >= Build.VERSION_CODES.DONUT;
            case Manifest.permission.ACCOUNT_MANAGER:
                return sdkVersion >= Build.VERSION_CODES.ECLAIR;
            case Manifest.permission.BIND_DEVICE_ADMIN:
            case Manifest.permission.BIND_WALLPAPER:
            case Manifest.permission.KILL_BACKGROUND_PROCESSES:
            case Manifest.permission.SET_TIME:
                return sdkVersion >= Build.VERSION_CODES.FROYO;
            case Manifest.permission.NFC:
            case Manifest.permission.SET_ALARM:
            case Manifest.permission.USE_SIP:
                return sdkVersion >= Build.VERSION_CODES.GINGERBREAD;
            case Manifest.permission.BIND_REMOTEVIEWS:
                return sdkVersion >= Build.VERSION_CODES.HONEYCOMB;
            case Manifest.permission.ADD_VOICEMAIL:
            case Manifest.permission.BIND_TEXT_SERVICE:
            case Manifest.permission.BIND_VPN_SERVICE:
                return sdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            case Manifest.permission.BIND_ACCESSIBILITY_SERVICE:
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
                return sdkVersion >= Build.VERSION_CODES.JELLY_BEAN;
            case Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE:
            case Manifest.permission.LOCATION_HARDWARE:
            case Manifest.permission.SEND_RESPOND_VIA_MESSAGE:
                return sdkVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2;
            case Manifest.permission.BIND_NFC_SERVICE:
            case Manifest.permission.BIND_PRINT_SERVICE:
            case Manifest.permission.BLUETOOTH_PRIVILEGED:
            case Manifest.permission.CAPTURE_AUDIO_OUTPUT:
            case Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT:
            case Manifest.permission.CAPTURE_VIDEO_OUTPUT:
            case Manifest.permission.INSTALL_SHORTCUT:
            case Manifest.permission.MANAGE_DOCUMENTS:
            case Manifest.permission.MEDIA_CONTENT_CONTROL:
            case Manifest.permission.TRANSMIT_IR:
            case Manifest.permission.UNINSTALL_SHORTCUT:
                return sdkVersion >= Build.VERSION_CODES.KITKAT;
            case Manifest.permission.BODY_SENSORS:
                return sdkVersion >= Build.VERSION_CODES.KITKAT_WATCH;
            case Manifest.permission.BIND_DREAM_SERVICE:
            case Manifest.permission.BIND_TV_INPUT:
            case Manifest.permission.BIND_VOICE_INTERACTION:
            case Manifest.permission.READ_VOICEMAIL:
            case Manifest.permission.WRITE_VOICEMAIL:
                return sdkVersion >= Build.VERSION_CODES.LOLLIPOP;
            case Manifest.permission.BIND_CARRIER_MESSAGING_SERVICE:
            case Manifest.permission.BIND_CARRIER_SERVICES:
                return sdkVersion >= Build.VERSION_CODES.LOLLIPOP_MR1;
            default:
                return true; //API 23+
        }
    }
}

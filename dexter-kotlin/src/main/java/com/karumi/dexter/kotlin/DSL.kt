package com.karumi.dexter.kotlin

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

inline fun Fragment.runtimePermission(permission: Permission.() -> Unit) {
    activity.runtimePermission(permission)
}

inline fun FragmentActivity.runtimePermission(permission: Permission.() -> Unit) {
    val dexter = Dexter.withActivity(this)
    Permission(dexter).apply(permission)
}

inline fun Activity.runtimePermission(permission: Permission.() -> Unit) {
    val dexter = Dexter.withActivity(this)
    Permission(dexter).apply(permission)
}

class Permission(val dexter: DexterBuilder.Permission) {

    fun permission(permission: String, listener: Listener.() -> Unit) {
        dexter.withPermission(permission)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        Listener().apply(listener).granted(response)
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        Listener().apply(listener).rationaleShouldBeShown(permission, token)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Listener().apply(listener).denied(response)
                    }
                })
                .withErrorListener { error ->
                    Listener().apply(listener).error(error)
                }.check()
    }

    fun permissions(vararg permissions: String, listener: MultipleListener.() -> Unit) {
        dexter.withPermissions(permissions.asList())
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        MultipleListener().apply(listener).checked(report)
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                        MultipleListener().apply(listener).rationaleShouldBeShown(permissions, token)
                    }
                })
                .withErrorListener { error ->
                    MultipleListener().apply(listener).error(error)
                }.check()
    }

    fun permissions(permissions: List<String>, listener: MultipleListener.() -> Unit) {
        dexter.withPermissions(permissions)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        MultipleListener().apply(listener).checked(report)
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                        MultipleListener().apply(listener).rationaleShouldBeShown(permissions, token)
                    }
                })
                .withErrorListener { error ->
                    MultipleListener().apply(listener).error(error)
                }.check()
    }
}

class Listener(var granted: (response: PermissionGrantedResponse) -> Unit = {},
               var denied: (response: PermissionDeniedResponse) -> Unit = {},
               var rationaleShouldBeShown: (permission: PermissionRequest, token: PermissionToken) -> Unit = { _, token -> token.continuePermissionRequest() },
               var error: (error: DexterError) -> Unit = {}) {

    fun granted(onGranted: (response: PermissionGrantedResponse) -> Unit) {
        granted = onGranted
    }

    fun denied(onDenied: (response: PermissionDeniedResponse) -> Unit) {
        denied = onDenied
    }

    fun rationaleShouldBeShown(onRationaleShouldBeShown: (permission: PermissionRequest, token: PermissionToken) -> Unit) {
        rationaleShouldBeShown = onRationaleShouldBeShown
    }

    fun error(onError: (error: DexterError) -> Unit = {}) {
        error = onError
    }
}

class MultipleListener(var checked: (report: MultiplePermissionsReport) -> Unit = {},
                       var rationaleShouldBeShown: (permissions: MutableList<PermissionRequest>, token: PermissionToken) -> Unit = { _, token -> token.continuePermissionRequest() },
                       var error: (error: DexterError) -> Unit = {}) {

    fun checked(onChecked: (report: MultiplePermissionsReport) -> Unit) {
        checked = onChecked
    }

    fun rationaleShouldBeShown(onRationaleShouldBeShown: (permissions: MutableList<PermissionRequest>, token: PermissionToken) -> Unit) {
        rationaleShouldBeShown = onRationaleShouldBeShown
    }

    fun error(onError: (error: DexterError) -> Unit = {}) {
        error = onError
    }
}

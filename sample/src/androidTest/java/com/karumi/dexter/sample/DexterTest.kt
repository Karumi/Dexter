package com.karumi.dexter.sample

import android.Manifest
import android.os.Handler
import android.os.HandlerThread
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.single.BasePermissionListener
import org.awaitility.Awaitility.await
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(AndroidJUnit4::class)
class DexterTest {
    private val unblock = AtomicBoolean(false)

    private val permissionListener = object : BasePermissionListener() {
        override fun onPermissionGranted(response: PermissionGrantedResponse) {
            unblock()
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {
            unblock()
        }
    }
    private val errorListener = PermissionRequestErrorListener { error ->
        Log.i(TAG, error.toString())
        unblock()
    }

    @Rule
    var activityTestRule = ActivityTestRule<SampleActivity>(SampleActivity::class.java)
    @Rule
    var grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        handlerThread = HandlerThread(TAG)
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        handlerThread!!.quit()
    }

    @Test
    fun testWithLooper() {
        val milestone = AtomicBoolean(false)

        requestAndAcceptPermissionOnHandlerThread(milestone, Manifest.permission.CAMERA)

        block()
        assertThat(milestone.get(), `is`(true))
    }

    private fun getPermission(permission: String) {
        Dexter.withActivity(activityTestRule.activity)
                .withPermission(permission)
                .withListener(permissionListener)
                .withErrorListener(errorListener)
                .onSameThread()
                .check()
    }

    private fun requestAndAcceptPermissionOnHandlerThread(milestone: AtomicBoolean,
                                                          permission: String) {
        handler!!.post {
            getPermission(permission)
            milestone.set(true)
        }
    }

    private fun unblock() {
        unblock.set(true)
    }

    private fun block() {
        await().atMost(20, TimeUnit.SECONDS).untilTrue(unblock)
        unblock.set(false)
    }

    companion object {

        private val TAG = "DexterTest"
    }
}

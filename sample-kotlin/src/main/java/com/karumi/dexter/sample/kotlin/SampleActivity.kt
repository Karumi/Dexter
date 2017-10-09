package com.karumi.dexter.sample.kotlin

import android.Manifest
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.karumi.dexter.kotlin.runtimePermission
import com.karumi.dexter.listener.DexterError
import kotlinx.android.synthetic.main.sample_activity.*

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_activity)

        all_permissions_button.setOnClickListener {
            runtimePermission {
                permissions(Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO) {
                    checked {
                        Toast.makeText(this@SampleActivity, "All granted", Toast.LENGTH_LONG).show()
                    }

                    rationaleShouldBeShown { _, token ->
                        token.continuePermissionRequest()
                    }
                }
            }
        }

        camera_permission_button.setOnClickListener {
            runtimePermission {
                permission(Manifest.permission.CAMERA) {
                    granted {
                        with(camera_permission_feedback) {
                            setText(R.string.permission_granted_feedback)
                            setTextColor(ContextCompat.getColor(this@SampleActivity, R.color.permission_granted))
                        }
                    }

                    denied {
                        with(camera_permission_feedback) {
                            setText(R.string.permission_denied_feedback)
                            setTextColor(ContextCompat.getColor(this@SampleActivity, R.color.permission_denied))
                        }
                    }

                    rationaleShouldBeShown { _, token ->
                        token.continuePermissionRequest()
                    }

                    error { error ->
                        when (error) {
                            DexterError.NO_PERMISSIONS_REQUESTED -> {
                                Toast.makeText(this@SampleActivity, "No permissions requested", Toast.LENGTH_LONG).show()
                            }
                            DexterError.REQUEST_ONGOING -> {
                                Toast.makeText(this@SampleActivity, "Request on going", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }

        contacts_permission_button.setOnClickListener {
            runtimePermission {
                permission(Manifest.permission.READ_CONTACTS) {
                    granted {
                        with(contacts_permission_feedback) {
                            setText(R.string.permission_granted_feedback)
                            setTextColor(ContextCompat.getColor(this@SampleActivity, R.color.permission_granted))
                        }
                    }

                    denied {
                        with(contacts_permission_feedback) {
                            setText(R.string.permission_denied_feedback)
                            setTextColor(ContextCompat.getColor(this@SampleActivity, R.color.permission_denied))
                        }
                    }
                }
            }
        }

        audio_permission_button.setOnClickListener {
            runtimePermission {
                permission(Manifest.permission.RECORD_AUDIO) {
                    granted {
                        with(audio_permission_feedback) {
                            setText(R.string.permission_granted_feedback)
                            setTextColor(ContextCompat.getColor(this@SampleActivity, R.color.permission_granted))
                        }
                    }

                    denied {
                        with(audio_permission_feedback) {
                            setText(R.string.permission_denied_feedback)
                            setTextColor(ContextCompat.getColor(this@SampleActivity, R.color.permission_denied))
                        }
                    }

                    rationaleShouldBeShown { _, token ->
                        token.continuePermissionRequest()
                    }
                }
            }
        }
    }
}

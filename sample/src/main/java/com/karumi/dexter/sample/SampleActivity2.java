package com.karumi.dexter.sample;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.karumi.dexter.sample.permission.BackgroundPermissionListener;
import com.karumi.dexter.sample.permission.PermissionConnector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : hafiq on 21/03/2017.
 */

public class SampleActivity2 extends BaseActivity implements PermissionConnector {


    @BindView(R.id.contacts_permission_button)
    Button contactsPermissionButton;
    @BindView(R.id.contacts_permission_feedback)
    TextView contactsPermissionFeedback;
    @BindView(R.id.camera_permission_button)
    Button cameraPermissionButton;
    @BindView(R.id.camera_permission_feedback)
    TextView cameraPermissionFeedback;
    @BindView(R.id.audio_permission_button)
    Button audioPermissionButton;
    @BindView(R.id.audio_permission_feedback)
    TextView audioPermissionFeedback;
    @BindView(R.id.all_permissions_button)
    Button allPermissionsButton;

    private MultiplePermissionsListener allPermissionsListener;
    private PermissionListener cameraPermissionListener;
    private PermissionListener contactsPermissionListener;
    private PermissionListener audioPermissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity);

        ButterKnife.bind(this);
        setPermission(this);
        initPermissionLister();
    }

    @OnClick(R.id.all_permissions_button)
    public void onAllPermissionsButtonClicked() {
        Log.d("Dexter","allpermissionclicked");
        setDexterMultiplePermissions(this,allPermissionsListener, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO);
    }

    @OnClick(R.id.camera_permission_button)
    public void onCameraPermissionButtonClicked() {
        Log.d("Dexter","camera permission clicked");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setDexterPermission(SampleActivity2.this,cameraPermissionListener,Manifest.permission.CAMERA);
            }
        });
    }

    @OnClick(R.id.contacts_permission_button)
    public void onContactsPermissionButtonClicked() {
        Log.d("Dexter","contact permission clicked");
        setDexterPermission(SampleActivity2.this,contactsPermissionListener,Manifest.permission.READ_CONTACTS);
    }

    @OnClick(R.id.audio_permission_button)
    public void onAudioPermissionButtonClicked() {
        Log.d("Dexter","audio permission clicked");
        setDexterPermission(SampleActivity2.this,audioPermissionListener,Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void showPermissionGranted(String permissionName) {
        TextView feedbackView = getFeedbackViewForPermission(permissionName);
        feedbackView.setText(R.string.permission_granted_feedback);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_granted));
    }

    @Override
    public void isAllPermissionGranted(boolean isAllGranted) {
        if (isAllGranted)
            Toast.makeText(this, "All Permission Granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        TextView feedbackView = getFeedbackViewForPermission(permission);
        feedbackView.setText(isPermanentlyDenied ? R.string.permission_permanently_denied_feedback : R.string.permission_denied_feedback);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_denied));
    }

    @Override
    public void showPermissionRationale(PermissionRequest permissions, PermissionToken token) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            showPermissionRationale(token);
        }
    }

    @Override
    public void showPermissionError(DexterError error) {

    }

    private TextView getFeedbackViewForPermission(String name) {
        TextView feedbackView;

        switch (name) {
            case Manifest.permission.CAMERA:
                feedbackView = cameraPermissionFeedback;
                break;
            case Manifest.permission.READ_CONTACTS:
                feedbackView = contactsPermissionFeedback;
                break;
            case Manifest.permission.RECORD_AUDIO:
                feedbackView = audioPermissionFeedback;
                break;
            default:
                throw new RuntimeException("No feedback view for this permission");
        }

        return feedbackView;
    }

    private void initPermissionLister(){
        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
        allPermissionsListener = new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView, R.string.all_permissions_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());

        contactsPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener, SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                R.string.contacts_permission_denied_feedback)
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .withCallback(new Snackbar.Callback() {
                    @Override public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }

                    @Override public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                    }
                })
                .build());

        PermissionListener dialogOnDeniedPermissionListener = DialogOnDeniedPermissionListener.Builder.withContext(this)
                .withTitle(R.string.audio_permission_denied_dialog_title)
                .withMessage(R.string.audio_permission_denied_dialog_feedback)
                .withButtonText(android.R.string.ok)
                .withIcon(R.mipmap.ic_logo_karumi)
                .build();

        audioPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener, dialogOnDeniedPermissionListener);
        cameraPermissionListener = new BackgroundPermissionListener(this);
    }
}

package com.karumi.dexter.sample;

import android.Manifest;
import android.widget.TextView;


public class PermissionsViews {
    TextView audioPermissionFeedbackView;
    TextView cameraPermissionFeedbackView;
    TextView contactsPermissionFeedbackView;

    PermissionsViews(TextView audioView, TextView cameraView, TextView contactView) {
        audioPermissionFeedbackView = audioView;
        cameraPermissionFeedbackView = cameraView;
        contactsPermissionFeedbackView = contactView;
    }

    public void showPermissionDenied(String permission, int color, boolean isPermanentlyDenied) {
        final int message = isPermanentlyDenied ? R.string.permission_permanently_denied_feedback
                : R.string.permission_denied_feedback;
        updatePermissionMessage(permission, color, message);
    }

    private void updatePermissionMessage(String permission, int color, int message) {
        TextView feedbackView = getFeedbackViewForPermission(permission);
        feedbackView.setText(message);
        feedbackView.setTextColor(color);
    }

    private TextView getFeedbackViewForPermission(String name) {
        switch (name) {
            case Manifest.permission.CAMERA:
                return cameraPermissionFeedbackView;
            case Manifest.permission.READ_CONTACTS:
                return contactsPermissionFeedbackView;
            case Manifest.permission.RECORD_AUDIO:
                return audioPermissionFeedbackView;
            default:
                throw new RuntimeException("No feedback view for this permission");
        }
    }

    void showPermissionGranted(String permission, int color) {
        updatePermissionMessage(permission, color, R.string.permission_granted_feedback);
    }
}

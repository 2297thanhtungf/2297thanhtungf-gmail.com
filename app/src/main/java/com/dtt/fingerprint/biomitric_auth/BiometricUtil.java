package com.dtt.fingerprint.biomitric_auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;


/*import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;*/


@SuppressLint({"MissingPermission"})
public class BiometricUtil {


    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }


    /*
     * Condition I: Check if the android version in device is greater than
     * Marshmallow, since fingerprint authentication is only supported
     * from Android 6.0.
     * Note: If your project's minSdkversion is 23 or higher,
     * then you won't need to perform this check.
     *
     * */
    public static boolean isSdkVersionSupported() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }


    /*
     * Condition II: Check if the device has fingerprint sensors.
     * Note: If you marked android.hardware.fingerprint as something that
     * your app requires (android:required="true"), then you don't need
     * to perform this check.
     *
     * */
    public static boolean isHardwareSupported(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }


    /*
     * Condition III: Fingerprint authentication can be matched with a
     * registered fingerprint of the user. So we need to perform this check
     * in order to enable fingerprint authentication
     *
     * */
    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }


    /*
     * Condition IV: Check if the permission has been added to
     * the app. This permission will be granted as soon as the user
     * installs the app on their device.
     *
     * */
    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }


    @SuppressLint("NewApi")
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void showKeyboard(final Activity activity, final EditText yourEditText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                yourEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);
                yourEditText.requestFocus();
            }
        }, 200);
    }
}

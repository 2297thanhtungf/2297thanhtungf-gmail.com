package com.dtt.fingerprint.biomitric_auth;

import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class BiometricManager extends BiometricManagerV23 {
    protected CancellationSignal mCancellationSignal = new CancellationSignal();

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected BiometricManager(final BiometricBuilder biometricBuilder) {
        this.context = biometricBuilder.context;
        this.title = biometricBuilder.title;
        this.description = biometricBuilder.description;
        this.negativeButtonText = biometricBuilder.negativeButtonText;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void authenticate(@NonNull final BiometricCallback biometricCallback) {

        if (title == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog title cannot be null");
            return;
        }


        if (description == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog description cannot be null");
            return;
        }

        if (negativeButtonText == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog negative button text cannot be null");
            return;
        }


        if (!BiometricUtil.isSdkVersionSupported()) {
            biometricCallback.onSdkVersionNotSupported();
            return;
        }

        if (!BiometricUtil.isPermissionGranted(context)) {
            biometricCallback.onBiometricAuthenticationPermissionNotGranted();
            return;
        }

        if (!BiometricUtil.isHardwareSupported(context)) {
            biometricCallback.onBiometricAuthenticationNotSupported();
            return;
        }

        if (!BiometricUtil.isFingerprintAvailable(context)) {
            biometricCallback.onBiometricAuthenticationNotAvailable();
            return;
        }

        displayBiometricDialog(biometricCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void cancelAuthentication() {
        if (BiometricUtil.isBiometricPromptEnabled()) {
            if (!mCancellationSignal.isCanceled())
                mCancellationSignal.cancel();
        } else {
            if (!mCancellationSignalV23.isCanceled())
                mCancellationSignalV23.cancel();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayBiometricDialog(BiometricCallback biometricCallback) {
        //Đoạn này check xem là Android P hay không. Tất cả các phiên bản bên dưới Android P không hỗ trợ api BiometricPrompt.
        // May mắn thay, chúng tôi vẫn có thể sử dụng API FingerprintManagerCompat để xác thực người dùng của mình
       /* if(BiometricUtils.isBiometricPromptEnabled()) {
            displayBiometricPrompt(biometricCallback);
        } else {
            displayBiometricPromptV23(biometricCallback);
        }*/
        // Hiện tại có thể bỏ đoạn check đi sử dụng cùng với android dưới Android 9. Đã test trên Sam Sang A4, Nokia 6, Xiaomi 9 SE chạy OK
        displayBiometricV23(biometricCallback);
    }


    public static class BiometricBuilder {

        private String title;
        private String description;
        private String negativeButtonText;

        private Context context;

        public BiometricBuilder(Context context) {
            this.context = context;
        }

        public BiometricBuilder setTitle(@NonNull final String title) {
            this.title = title;
            return this;
        }


        public BiometricBuilder setDescription(@NonNull final String description) {
            this.description = description;
            return this;
        }


        public BiometricBuilder setNegativeButtonText(@NonNull final String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public BiometricManager build() {
            return new BiometricManager(this);
        }
    }
}

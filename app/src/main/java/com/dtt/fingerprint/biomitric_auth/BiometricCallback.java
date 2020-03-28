package com.dtt.fingerprint.biomitric_auth;

public interface BiometricCallback {
    void onSdkVersionNotSupported();

    void onBiometricAuthenticationNotSupported();

    void onBiometricAuthenticationNotAvailable();

    void onBiometricAuthenticationPermissionNotGranted();

    void onBiometricAuthenticationInternalError(String error);

    void onAuthenticationFailed();

    void onAuthenticationCancelled();

    void onAuthenticationSuccessful(String result);

    void onAuthenticationHelp(int helpCode, CharSequence helpString);

    void onAuthenticationError(int errorCode, CharSequence errString);
}

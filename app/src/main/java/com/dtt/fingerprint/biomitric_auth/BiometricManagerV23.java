package com.dtt.fingerprint.biomitric_auth;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static androidx.core.hardware.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import static androidx.core.hardware.fingerprint.FingerprintManagerCompat.CryptoObject;
import static com.dtt.fingerprint.biomitric_auth.BiometricDialogV23.DIALOG_ONE_BUTTON;

@RequiresApi(api = Build.VERSION_CODES.M)
public class BiometricManagerV23 {
    private static final String KEY_NAME = UUID.randomUUID().toString();

    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private CryptoObject cryptoObject;

    protected Context context;
    protected String title;
    protected String description;
    protected String negativeButtonText;
    private BiometricDialogV23 biometricDialogV23;
    protected CancellationSignal mCancellationSignalV23 = new CancellationSignal();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void displayBiometricV23(final BiometricCallback biometricCallback) {
        generateKey();
        if (initCipher()) {
            cryptoObject = new CryptoObject(cipher);
            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(context);
            managerCompat.authenticate(cryptoObject,0,mCancellationSignalV23,
                    new FingerprintManagerCompat.AuthenticationCallback(){
                        @Override
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            super.onAuthenticationError(errMsgId, errString);
                            biometricCallback.onAuthenticationError(errMsgId,errString);
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            dismissDialog();
                            biometricCallback.onAuthenticationFailed();
                        }

                        @Override
                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            super.onAuthenticationHelp(helpMsgId, helpString);
                            biometricCallback.onAuthenticationHelp(helpMsgId,helpString);
                        }

                        @Override
                        public void onAuthenticationSucceeded(AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);

                            Cipher cipher = result.getCryptoObject().getCipher();
                            String encoded = encrypt("abc");
                            String decode = decrypt(encoded);
                            dismissDialog();
                            biometricCallback.onAuthenticationSuccessful(decode);
                            Log.i("abcdefgh", "abcdefgh : " + decode);

                        }
                    },null);
            disPlayBiometriDiaglog(biometricCallback);
        }
    }
    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            byte[] plainText = unencryptedString.getBytes();
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encode(encryptedText, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decode(encryptedString, 0);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    private void dismissDialog(){
        if (biometricDialogV23 != null){
            biometricDialogV23.dismiss();
        }
    }
    private void disPlayBiometriDiaglog(final BiometricCallback callback){
        biometricDialogV23 = new BiometricDialogV23(context,callback);
        biometricDialogV23.setTvitle(title);
        biometricDialogV23.setDescription(description);
        biometricDialogV23.setButtonText(DIALOG_ONE_BUTTON,negativeButtonText, negativeButtonText);
        biometricDialogV23.setCancelable(false);
        biometricDialogV23.show();
    }

    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Fail to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;

        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {

            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeystore");
            keyStore.load(null);

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(new
                        KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }
            keyGenerator.generateKey();
            Log.i("ABCDEF", "QƯERTYUIIO" + keyGenerator.generateKey().getAlgorithm().toString());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException exc) {
            exc.printStackTrace();
        }
    }
}

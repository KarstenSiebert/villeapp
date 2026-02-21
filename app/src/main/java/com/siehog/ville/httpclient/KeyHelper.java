package com.siehog.ville.httpclient;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.spec.RSAKeyGenParameterSpec;

import android.util.Base64;
import java.security.PublicKey;

import java.security.PrivateKey;
import java.security.Signature;

public class KeyHelper {

    private static final String KEY_ALIAS = "ville_app_key";

    public static void ensureKeyPairExists() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKeyPair();
        }
    }

    public static void generateKeyPair() throws Exception {

        KeyPairGenerator keyPairGenerator =
                KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA,
                        "AndroidKeyStore"
                );

        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY
        )
                .setAlgorithmParameterSpec(
                        new RSAKeyGenParameterSpec(
                                2048,
                                RSAKeyGenParameterSpec.F4
                        )
                )
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .build();

        keyPairGenerator.initialize(spec);
        keyPairGenerator.generateKeyPair();
    }

    public static String getPublicKeyBase64() throws Exception {

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        PublicKey publicKey =
                keyStore.getCertificate(KEY_ALIAS).getPublicKey();

        byte[] publicKeyBytes = publicKey.getEncoded();

        return Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP);
    }

    public static String signData(String data) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(KEY_ALIAS, null);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        signature.update(data.getBytes("UTF-8"));
        byte[] signedBytes = signature.sign();

        return Base64.encodeToString(signedBytes, Base64.NO_WRAP);
    }

}
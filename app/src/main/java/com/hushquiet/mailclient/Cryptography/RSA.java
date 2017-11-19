package com.hushquiet.mailclient.Cryptography;
import android.util.Base64;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Алексей on 08.10.2017.
 */
public class RSA {

    public static int SIGNED = 0;
    public static int ENCRYPT = 1;
    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA";


    public static KeyPair generateKey() {

        final KeyPairGenerator keyGen;
        final KeyPair key;
        try {
            keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            key = keyGen.generateKeyPair();
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    public static String decrypt(String str, PrivateKey key) {
        byte [] text = Base64.decode(str, Base64.DEFAULT);
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (dectyptedText == null)
            return null;

        return new String(dectyptedText);
    }

    public static byte[] publicKeyToByte(PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    public static byte[] privateKeyToByte(PrivateKey privateKey) {
        return privateKey.getEncoded();
    }

    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
    }

    public static PublicKey stringToPublicKey(String str) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(str, Base64.DEFAULT)));
    }

    public static PrivateKey getPrivateKey(byte [] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA").generatePrivate( new PKCS8EncodedKeySpec(key));
    }

    public static PublicKey getPublicKey(byte [] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
    }
}

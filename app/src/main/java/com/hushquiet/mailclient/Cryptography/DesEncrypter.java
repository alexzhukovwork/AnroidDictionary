package com.hushquiet.mailclient.Cryptography;

/**
 * Created by Алексей on 18.11.2017.
 */

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import java.io.*;
import java.security.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Алексей on 08.10.2017.
 */
public class DesEncrypter {
    Cipher ecipher;
    Cipher dcipher;


    public DesEncrypter() throws NoSuchAlgorithmException, NoSuchPaddingException {
        ecipher = Cipher.getInstance("DES");
        dcipher = Cipher.getInstance("DES");
    }

    public void setKey(SecretKey secretKey) throws InvalidKeyException {
        SecretKey key = secretKey;
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public String encrypt(String str) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        byte[] utf8 = str.getBytes("UTF8");
        byte[] enc = ecipher.doFinal(utf8);
        byte [] f = new byte[0];
        int i = android.os.Build.VERSION_CODES.O;
        f = Base64.encode(enc, Base64.DEFAULT);
        return new String(f);
    }

    /**
     * Функция расшифрования
     * @param str зашифрованная строка в формате Base64
     * @return расшифрованная строка
     */

    public String decrypt(String str) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] dec = new byte[0];
        dec = Base64.decode(str, Base64.DEFAULT);
        byte[] utf8 = dcipher.doFinal(dec);
        return new String(utf8, "UTF8");
    }


    public static String keyToString(SecretKey key) {
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    public static SecretKey stringToSecretKey(String str) {
        byte[] decodedKey = new byte[0];
        decodedKey = Base64.decode(str.getBytes(), Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
    }

}

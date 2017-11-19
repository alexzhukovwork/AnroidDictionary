package com.hushquiet.mailclient.Cryptography;

import android.util.Base64;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Алексей on 18.11.2017.
 */

public class DSA {
    public static KeyPair generateKey() {
        final KeyPairGenerator keyGen;
        final KeyPair key;
        try {
            keyGen = KeyPairGenerator.getInstance("DSA");
            key = keyGen.generateKeyPair();
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] sign(byte[] message, PrivateKey privateKey) throws GeneralSecurityException {
        if (privateKey == null) {
            throw new IllegalStateException("need to set private key with " + "OAuthConsumer.setProperty when " + "generating RSA-SHA1 signatures.");
        }
        Signature signer = Signature.getInstance("SHA1withDSA");
        signer.initSign(privateKey);
        signer.update(message);
        return signer.sign();
    }

    public static boolean verify(PublicKey publicKey, String signedData, String signature){

        Signature sig;
        try {
            sig = Signature.getInstance("SHA1withDSA");
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.decode(signature, Base64.DEFAULT))) {
                return false;
            }
            return true;
        }
        catch (  NoSuchAlgorithmException e) {

        }
        catch (  InvalidKeyException e) {

        }
        catch (  SignatureException e) {

        }
        return false;
    }

    public static byte[] publicKeyToByte(PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    public static byte[] privateKeyToByte(PrivateKey privateKey) {
        return privateKey.getEncoded();
    }

    public static String byteToString(byte [] key) {
        return Base64.encodeToString(key, Base64.DEFAULT);
    }

    public static PrivateKey getPrivateKey(byte [] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("DSA").generatePrivate( new PKCS8EncodedKeySpec(key));
    }

    public static PublicKey getPublicKey(String str) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte []key = Base64.decode(str, Base64.DEFAULT);
        return KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(key));
    }

}

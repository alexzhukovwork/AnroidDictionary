package com.hushquiet.mailclient.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hushquiet.mailclient.Cryptography.DSA;
import com.hushquiet.mailclient.Cryptography.DesEncrypter;
import com.hushquiet.mailclient.Cryptography.RSA;
import com.hushquiet.mailclient.Cryptography.SHA1;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.Mail.AuthMailTask;
import com.hushquiet.mailclient.Mail.Mail;
import com.hushquiet.mailclient.Mail.ReadMailTask;
import com.hushquiet.mailclient.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AuthActivity extends AppCompatActivity {
    private EditText editTextPassword, editTextLogin;
    private DB db;

    private void startMain() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        db = DB.getInstance(getApplicationContext());
        editTextLogin = (EditText)findViewById(R.id.Login);
        editTextPassword = (EditText)findViewById(R.id.Password);
    /*    KeyPair keyPair = RSA.generateKey();
        String keyString = RSA.publicKeyToString(keyPair.getPublic());
        String str;
        try {
            PublicKey publicKey = RSA.stringToPublicKey(keyString);
            String str2 = RSA.encrypt("test тестип епта", publicKey);
            PrivateKey privateKey = RSA.getPrivateKey(keyPair.getPrivate().getEncoded());
            str = RSA.decrypt(str2, privateKey);

            System.out.println(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }*/
   /*     KeyPair keyPair = DSA.generateKey();
        boolean b;
        try {
          byte [] sign = DSA.sign("ttttt".getBytes(), keyPair.getPrivate());
            byte [] keyB = keyPair.getPublic().getEncoded();
            String strKey = Base64.encodeToString(keyB, Base64.DEFAULT);
            keyB = Base64.decode(strKey, Base64.DEFAULT);
            PublicKey publicKey =
                    KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(keyB));
            b = DSA.verify(publicKey, "ttttf", Base64.encodeToString(sign, Base64.DEFAULT));
            System.out.println(b);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
*/

        if (db.isAuth()) {
            startMain();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SQLiteDatabase database = db.getDb();
        Cursor cursor = database.rawQuery("SELECT * FROM " + DB.USERS_TABLE, null);
        cursor.moveToLast();
    }

    public void buttonEnterClick(View v) {
       /* try {
         //   new SendMailTask(AuthActivity.this).execute(
           //         "alexTestZhukov@yandex.ua", "12345qwerty", "sub", "hello", "alexTestZhukov@yandex.ua", "zhukovfamily15@yandex.ua");
            new SendMailTask(AuthActivity.this).execute(
                             "zkalexzhukov@yandex.ru", "Добро Пожаловать", "Ваш ящик был занесен под учетной записью Alex");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //new AuthMailTask(this).execute(editTextLogin.getText().toString(), editTextPassword.getText().toString());
        try {
            String login = editTextLogin.getText().toString();
            String password = SHA1.SHA1( editTextPassword.getText().toString() );

            if (db.verifyUser(login, password)) {
                db.addToAuthUser(login);
                startMain();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void buttonRegistrationClick(View v) {
        try {
            String password = SHA1.SHA1(editTextPassword.getText().toString());
            String login = editTextLogin.getText().toString();
            KeyPair keyPair = DSA.generateKey();
            PublicKey publicKeyDSA = keyPair.getPublic();
            PrivateKey privateKeyDSA = keyPair.getPrivate();
            keyPair = RSA.generateKey();
            PublicKey publicKeyRSA = keyPair.getPublic();
            PrivateKey privateKeyRSA = keyPair.getPrivate();
            if (db.addToUsers("", "", login, password,
                    privateKeyRSA.getEncoded(), privateKeyDSA.getEncoded(),
                    publicKeyRSA.getEncoded(), publicKeyDSA.getEncoded()) && db.addToSettings(db.getUserByLogin(login))) {
                Toast.makeText(getApplicationContext(), "Вы зарегистрированы, авторизуйтесь.", Toast.LENGTH_LONG).show();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
       // db.dispose();
    }
}

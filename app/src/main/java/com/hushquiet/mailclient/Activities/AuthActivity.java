package com.hushquiet.mailclient.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hushquiet.mailclient.Cryptography.SHA1;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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
            if (db.addToUsers("", "", login, password)) {
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

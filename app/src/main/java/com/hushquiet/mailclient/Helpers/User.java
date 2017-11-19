package com.hushquiet.mailclient.Helpers;

import android.database.Cursor;
import android.util.Log;

import com.hushquiet.mailclient.DB.DB;

/**
 * Created by Алексей on 19.11.2017.
 */

public class User {
    public String name;
    public String lastName;
    public String login;
    public String password;
    public byte[] privateKeyRSA;
    public byte[] publicKeyRSA;
    public byte[] privateKeyDSA;
    public byte[] publicKeyDSA;
    public int id;

    public User(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex(DB.USERS_NAME));
        lastName = cursor.getString(cursor.getColumnIndex(DB.USERS_LAST_NAME));
        login = cursor.getString(cursor.getColumnIndex(DB.USERS_LOGIN));
        password = cursor.getString(cursor.getColumnIndex(DB.USERS_PASSWORD));
        privateKeyRSA = cursor.getBlob(cursor.getColumnIndex(DB.USERS_PRIVATE_KEY_RSA));
        privateKeyDSA = cursor.getBlob(cursor.getColumnIndex(DB.USERS_PRIVATE_KEY_DSA));
        publicKeyRSA = cursor.getBlob(cursor.getColumnIndex(DB.USERS_PUBLIC_KEY_RSA));
        publicKeyDSA = cursor.getBlob(cursor.getColumnIndex(DB.USERS_PUBLIC_KEY_DSA));
        id = cursor.getInt(cursor.getColumnIndex(DB.USERS_ID));
    }

}

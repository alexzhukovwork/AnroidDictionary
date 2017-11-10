package com.hushquiet.mailclient.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Алексей on 06.11.2017.
 */

public class DB extends SQLiteOpenHelper {
    // Таблицы
    public static final String USERS_TABLE = "Users";
    public static final String MAILBOXES_TABLE = "MailBoxes";
    public static final String MESSAGES_TABLE = "Messages";
    public static final String FOLDERS_TABLE = "Folders";
    public static final String FOLDERTYPES_TABLE = "FolderTypes";
    public static final String FILES_TABLE = "Files";
    public static final String AUTHUSER_TABLE = "AuthUser";

    // Поля для таблицы AuthUser
    public static final String AUTHUSER_ID = "_id";
    public static final String AUTHUSER_PASSWORD = "password";
    public static final String AUTHUSER_LOGIN = "login";

    // Поля таблицы users
    public static final String USERS_ID = "_id";
    public static final String USERS_NAME = "name";
    public static final String USERS_LAST_NAME = "last_name";
    public static final String USERS_LOGIN = "login";
    public static final String USERS_PASSWORD = "password";

    // Поля таблицы MailBoxes
    public static final String MAILBOXES_ID = "_id";
    public static final String MAILBOXES_MAIL = "mail";
    public static final String MAILBOXES_PASSWORD = "password";
    public static final String MAILBOXES_USER = "id_user";

    // Поля таблицы Messages
    public static final String MESSAGES_ID = "_id";
    public static final String MESSAGES_SUBJECT = "subject";
    public static final String MESSAGES_BODY = "body";
    public static final String MESSAGES_FROM = "from";
    public static final String MESSAGES_FOLDER = "id_folder";
    public static final String MESSAGES_TO = "to";
    public static final String MESSAGES_DATA = "data";

    // Поля таблицы Folders
    public static final String FOLDERS_ID = "_id";
    public static final String FOLDERS_TYPE = "id_type";
    public static final String FOLDERS_MAILBOX = "id_mailbox";

    // Поля таблицы FolderTypes
    public static final String FOLDERTYPES_ID = "_id";
    public static final String FOLDERTYPES_NAME = "name";

    // Поля таблицы Files
    public static final String FILES_ID = "_id";
    public static final String FILES_DATA = "data";
    public static final String FILES_MESSAGE = "id_message";

    private static String DB_PATH; // полный путь к базе данных
    private static String DB_NAME = "DataBase.db";
    private static final int SCHEMA = 1; // версия базы данных
    private Context myContext;
    private static DB dataBase;
    private Cursor cursor;
    SQLiteDatabase db;

    private DB(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext = context;
        DB_PATH = context.getFilesDir().getPath() + DB_NAME;
        create_db();
        db = open();
    }

    public static DB getInstance(Context context) {
        if (dataBase == null) {
            dataBase = new DB(context);
        }
        return dataBase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void dispose() {
        if (cursor != null)
            cursor.close();
        db.close();

    }

    public void create_db(){
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                String outFileName = DB_PATH;

                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        }
        catch(IOException ex){
            Log.d("DatabaseHelper", ex.getMessage());
        }
    }

    public SQLiteDatabase open()throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public boolean addToUsers(String lastName, String name, String login, String password) {
        ContentValues cv = new ContentValues();
        cv.put(USERS_LAST_NAME, lastName);
        cv.put(USERS_NAME, name);
        cv.put(USERS_LOGIN, login);
        cv.put(USERS_PASSWORD, password);
        return db.insert(USERS_TABLE, null, cv) > -1;
    }

    public boolean addToMailBoxes(String mail, String password, int user) {
        ContentValues cv = new ContentValues();
        cv.put(MAILBOXES_MAIL, mail);
        cv.put(MAILBOXES_PASSWORD, password);
        cv.put(MAILBOXES_USER, user);
        return db.insert(MAILBOXES_TABLE, null, cv) > -1;
    }

    public boolean addToMessages(String subject, String body, String from, String to, int folder) {
        ContentValues cv = new ContentValues();
        cv.put(MESSAGES_SUBJECT, subject);
        cv.put(MESSAGES_BODY, body);
        cv.put(MESSAGES_FROM, from);
        cv.put(MESSAGES_FOLDER, folder);
        cv.put(MESSAGES_TO, to);
        //cv.put(MESSAGES_DATA, data);
        return db.insert(MESSAGES_TABLE, null, cv) > -1;
    }

    public boolean addToFolders(int type, int mailBox) {
        ContentValues cv = new ContentValues();
        cv.put(FOLDERS_TYPE, type);
        cv.put(FOLDERS_MAILBOX, mailBox);
        return db.insert(FOLDERS_TABLE, null, cv) > -1;
    }

    public boolean addToFiles(byte []data, int message) {
        ContentValues cv = new ContentValues();
        cv.put(FILES_DATA, data);
        cv.put(FILES_MESSAGE, message);
        return db.insert(FILES_TABLE, null, cv) > -1;
    }

    public boolean addToAuthUser(String login) {
        cursor = db.query(USERS_TABLE, new String[]{USERS_ID, USERS_LOGIN, USERS_PASSWORD}, USERS_LOGIN + " = ?",
                new String[]{login}, null, null, null);
        cursor.moveToFirst();
        ContentValues cv = new ContentValues();
        cv.put(AUTHUSER_LOGIN, login);
        cv.put(AUTHUSER_PASSWORD, cursor.getString(cursor.getColumnIndex(USERS_PASSWORD)));
        cv.put(AUTHUSER_ID, cursor.getInt(cursor.getColumnIndex(USERS_ID)));
        return db.insert(AUTHUSER_TABLE, null, cv) > -1;
    }

    public boolean logout() {
        cursor = cursor = db.query(AUTHUSER_TABLE, new String[]{AUTHUSER_LOGIN}, null, null, null, null, null);
        cursor.moveToFirst();
        return db.delete(AUTHUSER_TABLE,
                AUTHUSER_LOGIN + " = ?",
                new String[]{cursor.getString(cursor.getColumnIndex(AUTHUSER_LOGIN))}) > 0;
    }

    public boolean isAuth() {
        cursor = db.query(AUTHUSER_TABLE, new String[]{AUTHUSER_ID}, null, null, null, null, null);
        return cursor.getCount() > 0;
    }

    public boolean verifyUser(String login, String password) {
        cursor = db.query(USERS_TABLE, new String[]{USERS_LOGIN, USERS_PASSWORD}, USERS_LOGIN + " = ?",
                new String[]{login}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

          return
                  cursor.getString(cursor.getColumnIndex(USERS_LOGIN)).equals(login) &&
                          cursor.getString(cursor.getColumnIndex(USERS_PASSWORD)).equals(password);

        }
        else
            return false;
    }

}


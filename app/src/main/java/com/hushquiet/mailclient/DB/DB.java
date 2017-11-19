package com.hushquiet.mailclient.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hushquiet.mailclient.Helpers.MailBox;
import com.hushquiet.mailclient.Helpers.MessageContainer;

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
    public static final String SETTINGS_TABLE = "Settings";

    // Поля для таблицы Settings
    public static final String SETTINGS_ID = "_id";
    public static final String SETTINGS_USER = "id_user";
    public static final String SETTINGS_CRYPT = "crypt";
    public static final String SETTINGS_SIGN = "sign";
    public static final String SETTINGS_MAILBOX = "mailbox";
    public static final String SETTINGS_IMAP_SERVER = "imap_server";
    public static final String SETTINGS_IMAP_PORT = "imap_port";
    public static final String SETTINGS_SMTP_SERVER = "smtp_server";
    public static final String SETTINGS_SMTP_PORT = "smtp_port";

    // Поля для таблицы AuthUser
    public static final String AUTHUSER_ID = "_id";
    public static final String AUTHUSER_PASSWORD = "password";
    public static final String AUTHUSER_LOGIN = "login";

    // Поля таблицы users
    public static final String USERS_ID = "_id";
    public static final String USERS_PUBLIC_KEY_RSA = "public_key_rsa";
    public static final String USERS_PRIVATE_KEY_RSA = "private_key_rsa";
    public static final String USERS_PUBLIC_KEY_DSA = "public_key_dsa";
    public static final String USERS_PRIVATE_KEY_DSA = "private_key_dsa";
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
    public static final String MESSAGES_FROM = "from_mail";
    public static final String MESSAGES_FOLDER = "folder";
    public static final String MESSAGES_TO = "to_mail";
    public static final String MESSAGES_DATE = "date";
    public static final String MESSAGES_MAILBOX = "mailbox";
    public static final String MESSAGES_SIGN = "sign";

    // Поля таблицы Folders
    public static final String FOLDERS_ID = "_id";
    public static final String FOLDERS_TYPE = "id_type";
    public static final String FOLDERS_MAILBOX = "id_mailbox";

    // Поля таблицы FolderTypes
    public static final String FOLDERTYPES_ID = "_id";
    public static final String FOLDERTYPES_NAME = "name";

    // Mailbox types
    public static final int INBOX = 1;
    public static final int SENT = 2;
    public static final int DRAFTS = 3;
    public static final int DELETED = 4;


    // Поля таблицы Files
    public static final String FILES_ID = "_id";
    public static final String FILES_DATA = "data";
    public static final String FILES_MESSAGE = "id_message";
    public static final String FILES_NAME = "name";

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

    public MessageContainer getMessages(int folderId) {
        MailBox mailBox = getCurrentMailbox();
        MessageContainer messageContainer = null;
        if (mailBox != null) {
            Cursor cursor = db.query(MESSAGES_TABLE, new String[]{
                            MESSAGES_BODY, MESSAGES_DATE, MESSAGES_FOLDER, MESSAGES_FROM,
                            MESSAGES_ID, MESSAGES_SIGN, MESSAGES_MAILBOX, MESSAGES_TO, MESSAGES_SUBJECT},
                    MESSAGES_MAILBOX + " = ? AND " + MESSAGES_FOLDER + " = ?",
                    new String[]{mailBox.id + "", folderId + ""},
                    null,
                    null,
                    MESSAGES_ID + " DESC");
            messageContainer = new MessageContainer(cursor);
        }
        return messageContainer;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public boolean updateSettings(int userId, int sign, int crypt, int mailbox) {
        ContentValues cv = new ContentValues();
        cv.put(SETTINGS_SIGN, sign);
        cv.put(SETTINGS_CRYPT, crypt);
        cv.put(SETTINGS_MAILBOX, mailbox);
        return db.update(SETTINGS_TABLE, cv, SETTINGS_USER + " = ?", new String[]{userId + ""}) > -1;
    }

    public boolean updateUser(int userId, String name, String lastName) {
        ContentValues cv = new ContentValues();
        cv.put(USERS_LAST_NAME, lastName);
        cv.put(USERS_NAME, name);
        return db.update(USERS_TABLE, cv, USERS_ID + " = ?", new String[]{userId + ""}) > -1;
    }


    public boolean addToUsers(String lastName, String name, String login, String password,
                              byte[] privateKeyRsa, byte[] privateKeyDsa, byte[] publicKeyRsa, byte[] publicKeyDsa) {
        ContentValues cv = new ContentValues();
        cv.put(USERS_LAST_NAME, lastName);
        cv.put(USERS_NAME, name);
        cv.put(USERS_LOGIN, login);
        cv.put(USERS_PASSWORD, password);
        cv.put(USERS_PRIVATE_KEY_DSA, privateKeyDsa);
        cv.put(USERS_PUBLIC_KEY_DSA, publicKeyDsa);
        cv.put(USERS_PRIVATE_KEY_RSA, privateKeyRsa);
        cv.put(USERS_PUBLIC_KEY_RSA, publicKeyRsa);
        return db.insert(USERS_TABLE, null, cv) > -1;
    }

    public boolean addToSettings(int idUser) {
        ContentValues cv = new ContentValues();
        cv.put(SETTINGS_USER, idUser);
        cv.put(SETTINGS_CRYPT, 0);
        cv.put(SETTINGS_SIGN, 0);
        cv.put(SETTINGS_MAILBOX, -1);
        return db.insert(SETTINGS_TABLE, null, cv) > -1;
    }

    public int getAuthUserID() {
        cursor = db.query(AUTHUSER_TABLE, new String[]{AUTHUSER_ID}, null, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(AUTHUSER_ID));
    }

    public Cursor getSettings(int userId) {
        cursor = db.query(SETTINGS_TABLE, new String[]{SETTINGS_CRYPT, SETTINGS_SIGN, SETTINGS_IMAP_PORT, SETTINGS_IMAP_SERVER,
                SETTINGS_MAILBOX, SETTINGS_SMTP_PORT, SETTINGS_SMTP_SERVER}, SETTINGS_USER + " = ?",
                new String[]{userId + ""}, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getUser(int userId) {
        cursor = db.query(USERS_TABLE, new String[]{USERS_LAST_NAME, USERS_NAME, USERS_PASSWORD, USERS_ID, USERS_PRIVATE_KEY_RSA, USERS_PRIVATE_KEY_DSA,
                USERS_PUBLIC_KEY_DSA, USERS_PUBLIC_KEY_RSA, USERS_LOGIN}, USERS_ID + " = ?",
                new String[]{userId + ""}, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getMailBoxes(int userId) {
        cursor = db.query(MAILBOXES_TABLE, new String[]{MAILBOXES_MAIL}, MAILBOXES_USER + " = ?",
                new String[]{userId + ""}, null, null, null);
        return cursor;
    }

    public int getUserByLogin(String login) {
        cursor = db.query(USERS_TABLE, new String[]{USERS_ID}, USERS_LOGIN + " = ?",
                new String[]{login}, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(USERS_ID));
    }

    public int getIdMailBox(String mail) {
        cursor = db.query(MAILBOXES_TABLE, new String[]{MAILBOXES_ID}, MAILBOXES_MAIL + " = ?",
                new String[]{mail}, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(MAILBOXES_ID));
    }

    public String getMailboxById(int id) {
        cursor = db.query(MAILBOXES_TABLE, new String[]{MAILBOXES_MAIL}, MAILBOXES_ID + " = ?",
                new String[]{id + ""}, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MAILBOXES_MAIL));
    }

    public MailBox getCurrentMailbox() {
        MailBox mailBox = null;
        Cursor cursor = db.query(SETTINGS_TABLE, new String []{SETTINGS_MAILBOX}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int i = cursor.getInt(cursor.getColumnIndex(SETTINGS_MAILBOX));
            cursor = db.query(MAILBOXES_TABLE, new String[]{MAILBOXES_MAIL, MAILBOXES_ID, MAILBOXES_PASSWORD}, MAILBOXES_ID + " = ?",
                    new String[]{i + ""}, null, null, null);
            if (cursor.getCount() > 0) {
                mailBox = new MailBox();
                cursor.moveToFirst();
                mailBox.id = cursor.getInt(cursor.getColumnIndex(MAILBOXES_ID));
                mailBox.email = cursor.getString(cursor.getColumnIndex(MAILBOXES_MAIL));
                mailBox.password = cursor.getString(cursor.getColumnIndex(MAILBOXES_PASSWORD));
            }
        }
        return mailBox;
    }

    public int getMailFromSetting(int userId) {
        cursor = db.query(SETTINGS_TABLE, new String[]{SETTINGS_MAILBOX}, SETTINGS_USER + " = ?",
                new String[]{userId +""}, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(SETTINGS_MAILBOX));
    }

    public boolean addToMailBoxes(String mail, String password, int user) {
        ContentValues cv = new ContentValues();
        cv.put(MAILBOXES_MAIL, mail);
        cv.put(MAILBOXES_PASSWORD, password);
        cv.put(MAILBOXES_USER, user);
        return db.insert(MAILBOXES_TABLE, null, cv) > -1;
    }

    public boolean addToMessages(String subject, String body, String from, String to, String date, int folder, int mailbox, String [] fileNames, byte [] data, int sign) {
        ContentValues cv = new ContentValues();
        cv.put(MESSAGES_SUBJECT, subject);
        cv.put(MESSAGES_BODY, body);
        cv.put(MESSAGES_FROM, from);
        cv.put(MESSAGES_FOLDER, folder);
        cv.put(MESSAGES_TO, to);
        cv.put(MESSAGES_DATE, date);
        cv.put(MESSAGES_MAILBOX, mailbox);
        cv.put(MESSAGES_SIGN, sign);

        long id = db.insert(MESSAGES_TABLE, null, cv);

        if (data != null) {
            addToFiles(data, fileNames, id);
        }

        return id > -1;
    }

    public boolean addToFiles(byte [] arr, String [] fileNames, long messageId) {
        ContentValues cv = new ContentValues();
        cv.put(FILES_DATA, arr);
        cv.put(FILES_MESSAGE, messageId);
        cv.put(FILES_NAME, fileNames[0]);
        return db.insert(FILES_TABLE, null, cv) > -1;
    }

    public Cursor getFiles(int messageId) {
        Cursor cursor = db.query(FILES_TABLE, new String[]{FILES_DATA, FILES_NAME}, FILES_MESSAGE + " = ?",
                new String[]{messageId + ""}, null, null, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor;
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

    public boolean deleteAuthUser() {
        cursor = cursor = db.query(AUTHUSER_TABLE, new String[]{AUTHUSER_LOGIN}, null, null, null, null, null);
        cursor.moveToFirst();
        return db.delete(AUTHUSER_TABLE,
                AUTHUSER_LOGIN + " = ?",
                new String[]{cursor.getString(cursor.getColumnIndex(AUTHUSER_LOGIN))}) > 0;
    }

    public boolean deleteMessage(int id) {
        return db.delete(MESSAGES_TABLE,
                MESSAGES_ID + " = ?",
                new String[]{id + ""}) > 0;
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


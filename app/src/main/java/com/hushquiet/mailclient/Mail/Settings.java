package com.hushquiet.mailclient.Mail;

import android.database.Cursor;

import com.hushquiet.mailclient.DB.DB;

/**
 * Created by Алексей on 19.11.2017.
 */

public class Settings {
    public int mailbox;
    public int imapPort;
    public String imapServer;
    public int smtpPort;
    public String smtpServer;
    public boolean crypt;
    public boolean sign;

    public Settings(Cursor cursor) {
        mailbox = cursor.getInt(cursor.getColumnIndex(DB.SETTINGS_MAILBOX));
        imapPort = cursor.getInt(cursor.getColumnIndex(DB.SETTINGS_IMAP_PORT));
        imapServer = cursor.getString(cursor.getColumnIndex(DB.SETTINGS_IMAP_SERVER));
        smtpPort = cursor.getInt(cursor.getColumnIndex(DB.SETTINGS_SMTP_PORT));
        smtpServer = cursor.getString(cursor.getColumnIndex(DB.SETTINGS_SMTP_SERVER));
        crypt = cursor.getInt(cursor.getColumnIndex(DB.SETTINGS_CRYPT)) == 1 ? true : false;
        sign = cursor.getInt(cursor.getColumnIndex(DB.SETTINGS_SIGN)) == 1 ? true : false;
    }
}

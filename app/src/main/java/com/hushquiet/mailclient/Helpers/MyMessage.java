package com.hushquiet.mailclient.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hushquiet.mailclient.DB.DB;

/**
 * Created by Алексей on 13.11.2017.
 */

public class MyMessage {
    public int id;
    public String subject = "";
    public String body;
    public String from;
    public int idFolder;
    public String to;
    public String date = "";
    public int idMailbox;
    public Bitmap bitmap;
    public String fileName;
    public byte []data;
    public int sign;

    public static int TRUE_SIGN = 1;
    public static int FALSE_SIGN = -1;
    public static int NONE_SIGN = 0;

    public MyMessage(String subject, String body, String from, int idFolder, String to, String date, int idMailbox, String[] fileNames, byte [] data, int sign) {
        this.sign = sign;

        if (subject != null)
            this.subject = subject;
        else
            this.subject = "";

        if (body != null)
            this.body = body;
        else
            this.body = "";

        if (from != null) {
            if (from.contains("<"))
                this.from = from.substring(from.indexOf("<") + 1, from.indexOf(">"));
            else
                this.from = from;
        }
        else
            this.from = "";

        if (to != null)
            this.to = to;
        else
            to = "";

        if (date != null)
            this.date = date;
        else
            date = "";

        if (data != null) {
            this.data = data;
            this.fileName = fileNames[0];
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        this.idFolder = idFolder;
        this.idMailbox = idMailbox;
    }

    public boolean writeToDB() {
        DB db = DB.getInstance(null);
        return db.addToMessages(subject, body, from, to, date, idFolder, idMailbox, new String[] {fileName}, data, sign);
    }

    public void setId(int id) {
        this.id = id;
    }
}

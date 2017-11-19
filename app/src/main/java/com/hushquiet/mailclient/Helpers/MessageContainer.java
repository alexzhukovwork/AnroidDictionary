package com.hushquiet.mailclient.Helpers;

import android.database.Cursor;

import com.hushquiet.mailclient.DB.DB;

import java.util.ArrayList;

/**
 * Created by Алексей on 14.11.2017.
 */

public class MessageContainer {
    private ArrayList<MyMessage> messages;

    public MessageContainer(Cursor cursor) {
        DB db = DB.getInstance(null);
        Cursor cursorFile;
        messages = new ArrayList<MyMessage>();
        byte []data;
        String fileName;
        while (cursor.moveToNext()) {
            cursorFile = db.getFiles(cursor.getInt(cursor.getColumnIndex(DB.MESSAGES_ID)));
            if (cursorFile.getCount() > 0) {
                fileName = cursorFile.getString(cursorFile.getColumnIndex(DB.FILES_NAME));
                data = cursorFile.getBlob(cursorFile.getColumnIndex(DB.FILES_DATA));
            } else {
                data = null;
                fileName = null;
            }

            for (int i = 0; i < cursorFile.getCount(); i++) {

                cursorFile.moveToNext();
            }

            MyMessage m = new MyMessage(
                    cursor.getString(cursor.getColumnIndex(DB.MESSAGES_SUBJECT)),
                    cursor.getString(cursor.getColumnIndex(DB.MESSAGES_BODY)),
                    cursor.getString(cursor.getColumnIndex(DB.MESSAGES_FROM)),
                    cursor.getInt(cursor.getColumnIndex(DB.MESSAGES_FOLDER)),
                    cursor.getString(cursor.getColumnIndex(DB.MESSAGES_TO)),
                    cursor.getString(cursor.getColumnIndex(DB.MESSAGES_DATE)),
                    cursor.getInt(cursor.getColumnIndex(DB.MESSAGES_MAILBOX)),
                    new String[] {fileName},
                    data,
                    cursor.getInt(cursor.getColumnIndex(DB.MESSAGES_SIGN)));
            m.setId(cursor.getInt(cursor.getColumnIndex(DB.MESSAGES_ID)));

            messages.add(m);
        }
    }

    public int getCount() {
        return messages.size();
    }

    public MyMessage getMessage(int i) {
        return messages.get(i);
    }

    public void removeMessage(MyMessage object) {
        messages.remove(object);
    }
}

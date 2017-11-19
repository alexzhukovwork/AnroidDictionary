package com.hushquiet.mailclient.Mail;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hushquiet.mailclient.Activities.MailboxesFragment;
import com.hushquiet.mailclient.DB.DB;

/**
 * Created by Алексей on 12.11.2017.
 */

public class AuthMailTask extends AsyncTask{
    private ProgressDialog statusDialog;
    private Activity activity;
    private MailboxesFragment mailboxesFragment;

    public AuthMailTask(Activity activity, MailboxesFragment mailboxesFragment) {
        this.activity = activity;
        this.mailboxesFragment = mailboxesFragment;

    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(activity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            publishProgress("Авторизация...");
            Mail.lastAuth = Mail.isAuth(args[0].toString(), args[1].toString());
            if (Mail.lastAuth) {
                publishProgress("Почтовый ящик авторизован.");
                DB db = DB.getInstance(activity);
                db.addToMailBoxes(args[0].toString(), args[1].toString(), db.getAuthUserID());
            } else {
                publishProgress("Почтовый ящик не корректен.");
            }
            Thread.sleep(500);
        } catch (Exception e) {
            publishProgress(e.getMessage());
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());
    }

    @Override
    public void onPostExecute(Object result) {
        statusDialog.dismiss();
    }
}

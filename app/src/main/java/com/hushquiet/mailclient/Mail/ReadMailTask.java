package com.hushquiet.mailclient.Mail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.hushquiet.mailclient.Activities.Interfaces.IFragment;
import com.hushquiet.mailclient.DB.DB;

/**
 * Created by Алексей on 11.11.2017.
 */

public class ReadMailTask extends AsyncTask {
    private ProgressDialog statusDialog;
    private Activity sendMailActivity;
    private IFragment fragment;

    public ReadMailTask(Activity activity, IFragment fragment) {
        sendMailActivity = activity;
        this.fragment = fragment;
    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Входящие процессы...");
            publishProgress("Получение сообщения...");
            new Mail().read(args[0].toString(), args[1].toString());
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        fragment.setMessageContainer(DB.getInstance(null).getMessages(DB.INBOX));
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

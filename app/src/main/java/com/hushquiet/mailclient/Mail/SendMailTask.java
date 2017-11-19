package com.hushquiet.mailclient.Mail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.hushquiet.mailclient.Activities.Interfaces.IFragmentSend;

/**
 * Created by Алексей on 08.11.2017.
 */

public class SendMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private Activity sendMailActivity;
    private IFragmentSend fragmentSend;

    public SendMailTask(Activity activity, IFragmentSend fragmentSend) {
        sendMailActivity = activity;
        this.fragmentSend = fragmentSend;
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
            publishProgress("Входящие процессы....");
            publishProgress("Подготовка сообщения....");
            publishProgress("Отправка сообщения....");
            Mail sender = new Mail();
            if (args[5] == null)
                sender.send(args[0].toString(),
                    args[1].toString(), args[2].toString(), args[3].toString(), args[4].toString(), null);
            else
                sender.send(args[0].toString(),
                        args[1].toString(), args[2].toString(), args[3].toString(), args[4].toString(), args[5].toString());
            if (sender.isSend()) {
                publishProgress("Письмо отправлено");
                Log.i("SendMailTask", "Mail Sent.");
            } else {
                fragmentSend.addToDrafts();
                publishProgress("Письмо не отправлено, оно было добавлено в черновик");
                Log.i("SendMailTask", "Email didn't sent.");
            }
            fragmentSend.setUI();
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
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

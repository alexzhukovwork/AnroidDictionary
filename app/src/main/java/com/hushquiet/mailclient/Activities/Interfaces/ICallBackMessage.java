package com.hushquiet.mailclient.Activities.Interfaces;

import android.os.Message;

import com.hushquiet.mailclient.Helpers.MyMessage;

/**
 * Created by Алексей on 15.11.2017.
 */

public interface ICallBackMessage {
    public void setMessageFragment(MyMessage m, int state);
}

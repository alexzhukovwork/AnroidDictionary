package com.hushquiet.mailclient.Activities.Interfaces;

import com.hushquiet.mailclient.Helpers.MessageContainer;

/**
 * Created by Алексей on 14.11.2017.
 */

public interface IFragment {
    public void setMessageContainer(MessageContainer messageContainer);
    public void reload();
}

package com.hushquiet.mailclient.Mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by Алексей on 08.11.2017.
 */

public class EmailAuthenticator extends javax.mail.Authenticator
{
    private String login;
    private String password;

    public EmailAuthenticator (final String login, final String password)
    {
        this.login    = login;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(login, password);
    }
}


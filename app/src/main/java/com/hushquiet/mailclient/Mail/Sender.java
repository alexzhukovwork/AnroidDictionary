package com.hushquiet.mailclient.Mail;

import java.util.Properties;

import javax.activation.DataHandler;

import javax.activation.FileDataSource;

import javax.mail.Address;
import javax.mail.Authenticator;

import javax.mail.Message;

import javax.mail.MessagingException;
import javax.mail.Multipart;

import javax.mail.Session;

import javax.mail.Transport;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import javax.mail.internet.MimeBodyPart;

import javax.mail.internet.MimeMessage;

import javax.mail.internet.MimeMultipart;

/**
 * Created by Алексей on 08.11.2017.
 */

public class Sender {
    private Message  message = null;
    protected static String SMTP_SERVER = "smtp.yandex.ru";
    protected static String SMTP_Port = "465";
    protected static String SMTP_AUTH_USER = "zkalexzhukov";
    protected static String SMTP_AUTH_PWD= "k12erst0rm";
    protected static String EMAIL_FROM = "zkalexzhukov@yandex.ru";
    protected static String FILE_PATH = null;
    protected static String REPLY_TO = null;
    private boolean result = false;

    public Sender () {}

    public void send(final String emailTo, final String subject, final String text)
    {
        result = false;
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_SERVER);
        properties.put("mail.smtp.port", SMTP_Port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");

        try {
            Authenticator auth = new EmailAuthenticator(SMTP_AUTH_USER,
                    " 2");
            Session session = Session.getInstance(properties,auth);
            session.setDebug(false);

            InternetAddress email_from = new InternetAddress(EMAIL_FROM);
            InternetAddress email_to   = new InternetAddress(emailTo);
            InternetAddress reply_to   = null;
            try {
                reply_to = (REPLY_TO != null) ?
                        new InternetAddress(REPLY_TO) : null;
            } catch (AddressException e) {
                e.printStackTrace();
            }
            message = new MimeMessage(session);
            message.setFrom(email_from);
            message.setRecipient(Message.RecipientType.TO, email_to);
            message.setSubject(subject);
            if (reply_to != null)
                message.setReplyTo (new Address[] {reply_to});

            Multipart mmp = new MimeMultipart();

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(text, "text/plain; charset=utf-8");
            mmp.addBodyPart(bodyPart);

            if (FILE_PATH != null) {
                MimeBodyPart mbr = createFileAttachment(FILE_PATH);
                mmp.addBodyPart(mbr);
            }

            message.setContent(mmp);
            Transport transport = session.getTransport("smtps");
            transport.send(message);
            result = true;
        } catch (AddressException e) {
//            System.err.println(e.getMessage());
        } catch (MessagingException e) {
//            System.err.println(e.getMessage());
        }
    }

    public boolean isSend()
    {
        return result;
    }

    private MimeBodyPart createFileAttachment(String filepath)
            throws MessagingException
    {
        MimeBodyPart mbp = new MimeBodyPart();

        FileDataSource fds = new FileDataSource(filepath);
        mbp.setDataHandler(new DataHandler(fds));
        mbp.setFileName(fds.getName());
        return mbp;
    }
}
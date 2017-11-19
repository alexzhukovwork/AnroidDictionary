package com.hushquiet.mailclient.Mail;

import android.app.Activity;
import android.graphics.Bitmap;

import com.hushquiet.mailclient.Cryptography.DSA;
import com.hushquiet.mailclient.Cryptography.DesEncrypter;
import com.hushquiet.mailclient.Cryptography.RSA;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.Helpers.MailBox;
import com.hushquiet.mailclient.Helpers.MyMessage;
import com.hushquiet.mailclient.Helpers.User;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.activation.DataHandler;

import javax.activation.FileDataSource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.mail.Address;
import javax.mail.Authenticator;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;

import javax.mail.MessagingException;
import javax.mail.Multipart;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;

import javax.mail.Store;
import javax.mail.Transport;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import javax.mail.internet.MimeBodyPart;

import javax.mail.internet.MimeMessage;

import javax.mail.internet.MimeMultipart;

/**
 * Created by Алексей on 08.11.2017.
 */

public class Mail {
    private Message  message = null;
    protected static String SMTP_SERVER = "smtp.yandex.ru";
    protected static String SMTP_Port = "465";
    protected static String FILE_PATH = null;
    protected static String REPLY_TO = null;
    private boolean result = false;
    public static boolean lastAuth;
    public static Bitmap bitmap;
    public static Activity activity;


    public static boolean isAuth(String email, String password) {
        String   IMAP_AUTH_EMAIL = email;
        String   IMAP_AUTH_PWD   = password;
        String   IMAP_Server     = "imap.yandex.ru";
        String   IMAP_Port       = "993";

        Properties properties = new Properties();
        properties.put("mail.debug"          , "false"  );
        properties.put("mail.store.protocol" , "imaps"  );
        properties.put("mail.imap.ssl.enable", "true"   );
        properties.put("mail.imap.port"      , IMAP_Port);

        Authenticator auth = new EmailAuthenticator(IMAP_AUTH_EMAIL,
                IMAP_AUTH_PWD);
        Session session = Session.getDefaultInstance(properties, auth);
        session.setDebug(false);

        Store store = null;
        try {
            store = session.getStore();
            store.connect(IMAP_Server, IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }

    public void read(String email, String password) {
        MailBox mailBox = DB.getInstance(null).getCurrentMailbox();

        String   IMAP_AUTH_EMAIL = email;
        String   IMAP_AUTH_PWD   = password;
        String   IMAP_Server     = "imap.yandex.ru";
        String   IMAP_Port       = "993"           ;

        Properties properties = new Properties();
        properties.put("mail.debug"          , "false"  );
        properties.put("mail.store.protocol" , "imaps"  );
        properties.put("mail.imap.ssl.enable", "true"   );
        properties.put("mail.imap.port"      , IMAP_Port);

        Authenticator auth = new EmailAuthenticator(IMAP_AUTH_EMAIL,
                IMAP_AUTH_PWD);
        Session session = Session.getDefaultInstance(properties, auth);
        session.setDebug(false);
        try {
            Store store = session.getStore();

            // Подключение к почтовому серверу
            store.connect(IMAP_Server, IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);

            // Папка входящих сообщений
            Folder inbox = store.getFolder("INBOX");
            // Открываем папку в режиме только для чтения
            inbox.open(Folder.READ_WRITE);

            if (inbox.getNewMessageCount() == 0)
                return;

            Message message;
            Multipart mp;

            int count = inbox.getMessageCount() + 1;
            int newCount = inbox.getNewMessageCount();
            String body;
            String subject;

            String fileName = null;
            byte[] arr = null;
            for (int i = count - newCount; i < count; i++) {
                message = inbox.getMessage(i);
                Flags flags = message.getFlags();
                if (flags.contains(Flags.Flag.SEEN))
                    continue;
                message.setFlag(Flags.Flag.SEEN, true);
                arr = null;
                fileName = null;
                body = "";
                if (message.getContent() instanceof String) {
                    body = (String)message.getContent();
                }
                else if (message.getContent() instanceof Multipart) {
                    mp = (Multipart)message.getContent();

                    for (int j = 0; j < mp.getCount(); j++) {
                        MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(j);
                        if (bp.getFileName() == null) {
                            body += bp.getContent() + "";
                        }
                        else {
                            fileName = bp.getContent() + "";
                            arr = saveFile(bp);
                        }
                    }
                }
                subject = message.getSubject();
                if (subject == null)
                    subject = "";


                if (subject.contains("[CRYPT]")) {
                    DB db = DB.getInstance(null);
                    User user = new User(db.getUser(db.getAuthUserID()));
                  //  subject = subject.replace("\r", "");
                    DesEncrypter desEncrypter = new DesEncrypter();
                    String secretKeyStr = body.substring(body.lastIndexOf("[") + 1, body.length());
                    body = body.replace("[" + secretKeyStr, "");
                    subject = subject.replace("[CRYPT]", "");
                    secretKeyStr = RSA.decrypt(secretKeyStr, RSA.getPrivateKey(user.privateKeyRSA));
                    SecretKey secretKey = DesEncrypter.stringToSecretKey(secretKeyStr);
                    desEncrypter.setKey(secretKey);
                    body = desEncrypter.decrypt(body);
                }

                int sign = 0;
                if (subject.contains("[SIGNDSA]")) {
                    body = body.replace("\r", "");
                    subject = subject.replace("[SIGNDSA]", "");
                    String keyString = body.substring(body.length() - 665, body.length() - 65);
                    String signString = body.substring(body.length() - 65, body.length());
                    body = body.substring(0, body.length() - 665);
                    try {
                        sign = DSA.verify(DSA.getPublicKey(keyString), body, signString) ? 1 : -1;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }

                MyMessage myMessage = new MyMessage(subject, body, message.getFrom()[0].toString(),
                        DB.INBOX, mailBox.email, null, mailBox.id, new String[]{fileName}, arr, sign);
                myMessage.writeToDB();
            }
        } catch (NoSuchProviderException e) {
            System.err.println(e.getMessage());
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private byte [] saveFile(MimeBodyPart bp) throws MessagingException, IOException {
        return readAllBytes(bp.getInputStream());
    }

    public static byte[] readAllBytes(InputStream is) throws IOException
    {
        final int BUFFER_SIZE = 64 * 1024;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buffer = new byte [BUFFER_SIZE];
        int len;

        while( (len = bis.read(buffer)) > -1 )
        {
            bos.write(buffer, 0, len);
        }
        return bos.toByteArray();
    }

    public void send(final String emailTo, final String subject, final String body, String email, String password, String pathFile)
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
            Authenticator auth = new EmailAuthenticator(email,
                    password);
            Session session = Session.getInstance(properties,auth);
            session.setDebug(false);

            InternetAddress email_from = new InternetAddress(email);
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

            Multipart mmp = new MimeMultipart("alternative");

            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(body, "text/html; charset=utf-8");
            mmp.addBodyPart(bodyPart);

            if (pathFile != null) {
                BodyPart mbr = createFileAttachment(pathFile);
                mmp.addBodyPart(mbr);
            }

            message.setContent(mmp);
            Transport transport = session.getTransport("smtps");
            transport.send(message);
            result = true;
            DB db = DB.getInstance(null);
            String name = null;
            InputStream in = null;
            if (pathFile != null) {
                in = new FileInputStream(pathFile);
                name = getNameFromPath(pathFile);
            }
            db.addToMessages(subject, body, email, emailTo, "", DB.SENT, db.getCurrentMailbox().id, new String[]{name}, readAllBytes(in), 0);
        } catch (AddressException e) {
            System.err.println(e.getMessage());
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getNameFromPath(String path) {
        return path.substring(path.indexOf("/"), path.length());
    }

    public boolean isSend()
    {
        return result;
    }

    private MimeBodyPart createFileAttachment(String filepath)
            throws MessagingException, UnsupportedEncodingException {
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setDisposition("attachment");
        mbp.setHeader("Content-ID", "<image>");
        FileDataSource fds = new FileDataSource(filepath);
        mbp.setDataHandler(new DataHandler(fds));
        mbp.setFileName(fds.getName());
        return (MimeBodyPart)mbp;
    }
}
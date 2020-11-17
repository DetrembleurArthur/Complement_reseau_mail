package org.bourgedetrembleur;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.util.Properties;

public class MailManager
{
    private final Settings settings = new Settings();
    /*
    *
    * */

    public void send(String destination, String object, String message) throws MessagingException
    {
        Properties prop = System.getProperties();
        prop.put("mail.smtp.auth", getSettings().getAuthentication());
        prop.put("mail.smtp.starttls.enable", getSettings().getTtls());
        prop.put("mail.smtp.host", getSettings().getSmtpServer());
        prop.put("mail.smtp.port", getSettings().getSmtpPort());

        Session session = null;
        MimeMessage msg = null;

        if(getSettings().getAuthentication())
        {
            session = Session.getInstance(prop,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(getSettings().getEmail(), getSettings().getPassword());
                }
            });
            msg = new MimeMessage(session);
        }
        else
        {
            session = Session.getDefaultInstance(prop);
            msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(getSettings().getEmail()));
        }

        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destination));
        msg.setSubject(object);
        msg.setText(message);
        Transport.send(msg);
    }

    public Settings getSettings()
    {
        return settings;
    }
}

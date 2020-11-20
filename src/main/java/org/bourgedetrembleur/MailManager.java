package org.bourgedetrembleur;

import javafx.concurrent.Task;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class MailManager {
    private final Settings settings = new Settings();
    /*
    *
    * */

    public Properties getSmtpProperties() {
        Properties prop = System.getProperties();
        prop.put("mail.smtp.auth", getSettings().getAuthentication());
        prop.put("mail.smtp.starttls.enable", getSettings().getTtls());
        prop.put("mail.smtp.host", getSettings().getSmtpServer());
        prop.put("mail.smtp.port", getSettings().getSmtpPort());
        return prop;
    }

    public Session getSession(Properties prop) {
        Session session = null;
        if (getSettings().getAuthentication()) {
            session = Session.getInstance(prop, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(getSettings().getEmail(), getSettings().getPassword());
                }
            });
        } else {
            session = Session.getDefaultInstance(prop);
        }
        return session;
    }

    public int testSmtpAccess() {
        Properties props = getSmtpProperties();
        Session session = getSession(props);
        
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
            return 1;
        }
        catch(AuthenticationFailedException e)
        {
            return 2;
        }
         catch (MessagingException e) {
            System.err.println("Other");
        }
        return 3;
    }

    public MimeMessage generateMessage(Session session, String destination, String object, String message) throws MessagingException
    {
        MimeMessage msg = new MimeMessage(session);
        if(!getSettings().getAuthentication())
            msg.setFrom(new InternetAddress(getSettings().getEmail()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destination));
        msg.setSubject(object);
        msg.setText(message);
        return msg;
    }

    public Multipart generateMultipart(MimeMessage mimeMessage, List<File> attachedFiles)
    {
        if(attachedFiles != null)
        {
            
        }
        return null;
    }

    public void sendMessage(Message message) throws MessagingException
    {
        Transport.send(message);
    }

    public Settings getSettings()
    {
        return settings;
    }
}

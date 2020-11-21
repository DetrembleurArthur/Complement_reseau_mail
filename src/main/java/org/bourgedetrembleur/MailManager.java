package org.bourgedetrembleur;

import javafx.concurrent.Task;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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

    public MimeMessage generateMessage(Session session, String destination, String object) throws MessagingException
    {
        MimeMessage msg = new MimeMessage(session);
        if(!getSettings().getAuthentication())
            msg.setFrom(new InternetAddress(getSettings().getEmail()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destination));
        msg.setSubject(object);
        return msg;
    }

    public MimeMessage setMessageText(MimeMessage msg, String text)
    {
        try
        {
            msg.setText(text);
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return msg;
    }

    public Multipart generateMultipart(List<File> attachedFiles, String text)
    {
        if(attachedFiles != null)
        {
            try
            {
                Multipart multipart = new MimeMultipart();
                MimeBodyPart attachText = new MimeBodyPart();
                attachText.setText(text);
                multipart.addBodyPart(attachText);
                for (var file : attachedFiles)
                {
                    MimeBodyPart mimeBodyPart = new MimeBodyPart();
                    DataSource dataSource = new FileDataSource(file);
                    mimeBodyPart.setDataHandler(new DataHandler(dataSource));
                    mimeBodyPart.setFileName(file.getName());
                    multipart.addBodyPart(mimeBodyPart);
                }
                return multipart;
            }
            catch (MessagingException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void attachMultipart(MimeMessage mimeMessage, Multipart multipart)
    {
        try
        {
            mimeMessage.setContent(multipart);
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
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

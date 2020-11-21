package org.bourgedetrembleur;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class SendEmailService extends Service<Void>
{
    private MailManager mailManager;
    private String email;
    private String objet;
    private String message;
    private List<File> attachedFiles;

    public SendEmailService(MailManager mailManager)
    {
        this.mailManager = mailManager;
    }

    @Override
    protected Task<Void> createTask()
    {
        return new Task<>()
        {
            @Override
            protected Void call() throws Exception
            {
                updateProgress(0f, 100f);
                Properties properties = mailManager.getSmtpProperties();
                updateProgress(20f, 100f);
                Session session = mailManager.getSmtpSession(properties);
                updateProgress(40f, 100f);
                MimeMessage msg = mailManager.generateMessage(session, email, objet);
                updateProgress(60f, 100f);
                Multipart multipart = mailManager.generateMultipart(attachedFiles, message);
                updateProgress(70f, 100f);
                mailManager.attachMultipart(msg, multipart);
                updateProgress(80f, 100f);
                mailManager.sendMessage(msg);
                updateProgress(100f, 100f);
                attachedFiles = null;
                return null;
            }
        };
    }

    public void setMailManager(MailManager mailManager)
    {
        this.mailManager = mailManager;
    }

    public void setInfos(String email, String objet, String message, List<File> attachedFiles)
    {
        this.email = email;
        this.objet = objet;
        this.message = message;
        this.attachedFiles = attachedFiles;
    }
}

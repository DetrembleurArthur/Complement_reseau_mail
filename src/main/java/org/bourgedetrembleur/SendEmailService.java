package org.bourgedetrembleur;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Message;
import javax.mail.Session;
import java.util.Properties;

public class SendEmailService extends Service<Void>
{
    private MailManager mailManager;
    private String email;
    private String objet;
    private String message;

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
                updateProgress(25f, 100f);
                Session session = mailManager.getSession(properties);
                updateProgress(50f, 100f);
                Message msg = mailManager.generateMessage(session, email, objet, message);
                updateProgress(75f, 100f);
                mailManager.sendMessage(msg);
                updateProgress(100f, 100f);
                return null;
            }
        };
    }

    public void setMailManager(MailManager mailManager)
    {
        this.mailManager = mailManager;
    }

    public void setInfos(String email, String objet, String message)
    {
        this.email = email;
        this.objet = objet;
        this.message = message;
    }
}

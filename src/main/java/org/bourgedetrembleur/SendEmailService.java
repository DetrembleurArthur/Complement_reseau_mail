package org.bourgedetrembleur;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.swing.*;

public class SendEmailService extends Service<Void>
{
    private MailManager mailManager;
    private String email;
    private String objet;
    private String message;

    public SendEmailService(MailManager mailManager)
    {
        //setOnSucceeded(workerStateEvent -> JOptionPane.showMessageDialog(null, "Mail envoyé à " + email));
        setOnFailed(workerStateEvent -> JOptionPane.showMessageDialog(null, "Impossible d'envoyer le mail à " + email));
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
                 System.out.println("send");
                mailManager.send(email, objet, message);
                System.out.println("end send");
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

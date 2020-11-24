package org.bourgedetrembleur;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecvEmailService extends Service<List<ViewMessage>>
{
    private MailManager mailManager;

    public RecvEmailService(MailManager mailManager)
    {
        this.mailManager = mailManager;
    }

    @Override
    protected Task<List<ViewMessage>> createTask()
    {
        return new Task<>()
        {
            @Override
            protected List<ViewMessage> call() throws InterruptedException
            {
                int nb = 0;
                while (true)
                {
                    if(isCancelled())
                        break;
                    var messages = mailManager.receivePop3();
                    int nbnew = messages.length;
                    System.err.println("SIZE/ " + nbnew + " " + nb);

                    List<ViewMessage> returnMessagesList = new ArrayList<>();

                    try
                    {
                        for (int i = 0; i < nbnew - nb; i++)
                        {

                            if (!messages[nbnew - i - 1].getFrom()[0].toString().contains(mailManager.getSettings().getEmail()))
                                returnMessagesList.add(new ViewMessage(messages[nbnew - i - 1]));
                        }
                        nb = nbnew;
                        updateValue(returnMessagesList);
                        Thread.sleep(5000);
                    }
                    catch (MessagingException e)
                    {
                        e.printStackTrace();
                    }

                }
                return null;
            }
        };
    }

    public MailManager getMailManager()
    {
        return mailManager;
    }

    public void setMailManager(MailManager mailManager)
    {
        this.mailManager = mailManager;
    }
}

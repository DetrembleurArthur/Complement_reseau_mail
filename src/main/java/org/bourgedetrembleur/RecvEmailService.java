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
            protected List<ViewMessage> call() throws Exception
            {
                int nb = 0;
                String account = mailManager.getSettings().getEmail();
                while (true)
                {
                    String currentAccount = mailManager.getSettings().getEmail();
                    if(!account.equalsIgnoreCase(currentAccount))
                    {
                        nb = 0;
                        account = currentAccount;
                        updateValue(null);
                    }
                    if (isCancelled())
                        break;
                    var messages = mailManager.receivePop3();
                    int nbnew = messages.length;
                    System.err.println("SIZE/ " + nbnew + " " + nb);

                    List<ViewMessage> returnMessagesList = new ArrayList<>();

                    for (int i = 0; i < nbnew - nb; i++)
                    {

                        if (!messages[nbnew - i - 1].getFrom()[0].toString().contains(mailManager.getSettings().getEmail()))
                            returnMessagesList.add(new ViewMessage(messages[nbnew - i - 1]));
                    }
                    nb = nbnew;
                    updateValue(returnMessagesList);

                    Thread.sleep(30000);
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

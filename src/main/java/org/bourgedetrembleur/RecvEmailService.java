package org.bourgedetrembleur;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
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
        return new Task<List<ViewMessage>>()
        {
            @Override
            protected List<ViewMessage> call() throws Exception
            {
                int nb = 0;
                while(true)
                {
                    var messages = mailManager.receivePop3();
                    int nbnew = messages.length;
                    System.err.println("SIZE/ " + nbnew + " " + nb);

                    List<ViewMessage> returnMessagesList = new ArrayList<>();

                    for(int i = 0; i < nbnew - nb; i++)
                    {
                        if(!messages[nbnew-i-1].getFrom()[0].toString().equalsIgnoreCase("detart.dummy@gmail.com"))
                            returnMessagesList.add(new ViewMessage(messages[nbnew-i-1]));
                    }
                    nb = nbnew;
                    updateValue(returnMessagesList);
                    Thread.sleep(5000);
                }
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

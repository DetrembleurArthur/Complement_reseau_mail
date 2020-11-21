package org.bourgedetrembleur;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;

public class RecvEmailService extends Service<Void>
{
    private MailManager mailManager;

    public RecvEmailService(MailManager mailManager)
    {
        this.mailManager = mailManager;
    }

    @Override
    protected Task<Void> createTask()
    {
        return new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                var messages = mailManager.receivePop3();


                System.err.println(messages.length);

                for(int i = 0; i < messages.length; i++)
                {
                    String exp = messages[i].getFrom()[0].toString();
                    String subject = messages[i].getSubject();
                    String date = messages[i].getSentDate().toString();


                    System.err.println(i + ">> " + exp + " " + subject + " " + date);

                    if(messages[i].getContent() instanceof Multipart)
                    {
                        Multipart multipart = (Multipart) messages[i].getContent();
                        int nbParts = multipart.getCount();

                        for(int j = 0; j< nbParts; j++)
                        {
                            System.err.println("Composante: " + j);
                            Part part = multipart.getBodyPart(j);
                            String disp = part.getDisposition();
                            if(part.isMimeType("text/plain"))
                            {
                                System.err.println("Plain text: " + part.getContent());
                            }
                            else if(part.isMimeType("text/html"))
                            {
                                System.err.println("Html text: " + part.getContent());
                            }
                            else if(disp != null && disp.equalsIgnoreCase(Part.ATTACHMENT))
                            {
                                System.err.println("Attachement: " + part.getFileName());
                            }
                        }
                    }
                    else
                    {
                        String content = (String) messages[i].getContent();
                        System.err.println("Content: " + content);
                    }


                    System.err.println(i + " / " + messages.length);
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

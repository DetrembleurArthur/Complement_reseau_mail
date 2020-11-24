package org.bourgedetrembleur;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewMessage
{
    private final Message message;
    private Object loadedContent;

    public ViewMessage(Message message)
    {
        this.message = message;
    }

    private void loadContent()
    {
        try
        {
            if(loadedContent == null)
                loadedContent = message.getContent();
        } catch (IOException | MessagingException e)
        {
            e.printStackTrace();
        }
    }

    public Multipart getCacheMultipart()
    {
        loadContent();
        if(loadedContent instanceof Multipart)
            return (Multipart) loadedContent;
        return null;
    }

    public Message getMessage()
    {
        return message;
    }
    /*
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
                                //System.err.println("Plain text: " + part.getContent());
                            }
                            else if(part.isMimeType("text/html"))
                            {
                                //System.err.println("Html text: " + part.getContent());
                            }
                            else if(disp != null && disp.equalsIgnoreCase(Part.ATTACHMENT))
                            {
                               //System.err.println("Attachement: " + part.getFileName());
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
*/
    public String getMessageText()
    {
        loadContent();
        if(loadedContent instanceof String)
            return (String) loadedContent;
        return null;
    }

    public List<Part> getParts()
    {
        var multipart = getCacheMultipart();
        if(multipart != null)
        {
            try
            {
                List<Part> parts = new ArrayList<>();
                int count = multipart.getCount();
                System.err.println("PARTS: " + count);
                for(int i = 0; i < count; i++)
                {
                    parts.add(multipart.getBodyPart(i));
                }
                return parts;
            } catch (MessagingException e)
            {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String toString()
    {
        try
        {
            return message.getFrom()[0] + " : " + message.getSubject();
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return "error";
    }
}

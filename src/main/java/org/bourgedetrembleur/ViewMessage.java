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
            return message.getFrom()[0] + "\n> " + message.getSubject();
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return "error";
    }
}

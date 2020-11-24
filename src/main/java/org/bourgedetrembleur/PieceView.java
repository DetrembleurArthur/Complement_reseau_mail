package org.bourgedetrembleur;

import javax.mail.MessagingException;
import javax.mail.Part;

public class PieceView
{
    private final Part part;

    public PieceView(Part part)
    {
        this.part = part;
    }

    public Part getPart()
    {
        return part;
    }

    @Override
    public String toString()
    {
        try
        {
            return part.getFileName();
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return "error";
    }
}

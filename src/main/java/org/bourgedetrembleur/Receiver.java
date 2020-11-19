package org.bourgedetrembleur;

import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.io.*;
import java.util.ArrayList;

public class Receiver implements Serializable
{
    private static ObservableList<Receiver> receivers;
    private static final String filename = "receivers.dat";
    private String name;
    private String email;

    public Receiver()
    {
        name = "no name";
        email = "no email";
    }

    public Receiver(String name, String email)
    {
        this.name = name.toLowerCase();
        this.email = email.toLowerCase();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name.toLowerCase();
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email.toLowerCase();
    }

    public static void load()
    {
        try
        {
            File file = new File(filename);
            if(!file.exists())
            {
                file.createNewFile();
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            ArrayList<Receiver> list = (ArrayList<Receiver>) objectInputStream.readObject();
            objectInputStream.close();
            receivers.addAll(list);
        } catch (IOException | ClassNotFoundException e)
        {
            //e.printStackTrace();
        }
    }

    public static ObservableList<Receiver> getReceivers()
    {
        return receivers;
    }

    public static void setReceivers(ObservableList<Receiver> receivers)
    {
        Receiver.receivers = receivers;
    }

    public static void save()
    {
        try
        {
            ArrayList<Receiver> list = new ArrayList<>();
            list.addAll(receivers);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filename));
            objectOutputStream.writeObject(list);
            objectOutputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == null)
            return false;
        return name.equals(((Receiver) other).getName()) || email.equals(((Receiver) other).getEmail());
    }
}

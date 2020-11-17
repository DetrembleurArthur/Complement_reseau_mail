package org.bourgedetrembleur;

import javax.swing.*;
import java.io.*;
import java.util.Properties;


public class Settings
{
    private static final String filename = "parameters.properties";
    private Properties properties = new Properties();

    public void load()
    {
        try
        {
            properties.load(new FileReader(filename));
        } catch (IOException e)
        {
            try
            {
                new File(filename).createNewFile();
                setSmtpServer("no server");
                setSmtpPort(0);
                setTtls(false);
                setAuthentication(false);
                setEmail("no email");
                setPassword("no password");
                save();
                JOptionPane.showMessageDialog(null, "Veuillez compléter vos paramètres");

            } catch (IOException ioException)
            {

            }
        }
    }

    public void save()
    {
        try
        {
            properties.store(new FileWriter(filename), "Settings of mail user agent");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setSmtpServer(String server)
    {
        properties.setProperty("smtp.server", server);
    }

    public void setSmtpPort(int port)
    {
        properties.setProperty("smtp.port", Integer.toString(port));
    }

    public void setTtls(boolean ttls)
    {
        properties.setProperty("ttls", Boolean.toString(ttls));
    }

    public void setAuthentication(boolean auth)
    {
        properties.setProperty("authentication", Boolean.toString(auth));
    }

    public void setEmail(String email)
    {
        properties.setProperty("email", email);
    }

    public void setPassword(String password)
    {
        properties.setProperty("password", password);
    }

    public String getSmtpServer()
    {
        return properties.getProperty("smtp.server");
    }

    public int getSmtpPort()
    {
        return Integer.parseInt(properties.getProperty("smtp.port"));
    }

    public boolean getTtls()
    {
        return Boolean.parseBoolean(properties.getProperty("ttls"));
    }

    public boolean getAuthentication()
    {
        return Boolean.parseBoolean(properties.getProperty("authentication"));
    }

    public String getEmail()
    {
        return properties.getProperty("email");
    }

    public String getPassword()
    {
        return properties.getProperty("password");
    }
}


/*
* module org.bourgedetrembleur {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens org.bourgedetrembleur to javafx.fxml;
    exports org.bourgedetrembleur;
}
* */
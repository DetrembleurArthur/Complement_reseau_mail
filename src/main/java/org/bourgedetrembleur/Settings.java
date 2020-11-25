package org.bourgedetrembleur;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;


public class Settings
{
    private static final String filename = "parameters.properties";
    private Properties properties = new Properties();

    public synchronized void load()
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
                setEnableSoundEffect(false);
                setVolume(0.5f);
                setEnableNotifications(true);
                setPop3Server("no server");
                setPop3Port(0);
                save();
                App.notification("Warning", "Veuillez compléter vos paramètres", TrayIcon.MessageType.WARNING);

            } catch (IOException ioException)
            {

            }
        }
    }

    public synchronized void save()
    {
        try
        {
            properties.store(new FileWriter(filename), "Settings of mail user agent");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void setSmtpServer(String server)
    {
        properties.setProperty("smtp.server", server);
    }

    public synchronized void setSmtpPort(int port)
    {
        properties.setProperty("smtp.port", Integer.toString(port));
    }

    public synchronized void setTtls(boolean ttls)
    {
        properties.setProperty("ttls", Boolean.toString(ttls));
    }

    public synchronized void setAuthentication(boolean auth)
    {
        properties.setProperty("authentication", Boolean.toString(auth));
    }

    public synchronized void setEmail(String email)
    {
        properties.setProperty("email", email);
    }

    public synchronized void setPassword(String password)
    {
        properties.setProperty("password", password);
    }

    public synchronized void setEnableSoundEffect(boolean enable)
    {
        properties.setProperty("enableSoundEffect", String.valueOf(enable));
    }

    public synchronized void setVolume(double volume)
    {
        properties.setProperty("volume", String.valueOf(volume));
    }

    public synchronized void setEnableNotifications(boolean enable)
    {
        properties.setProperty("enableNotifications", String.valueOf(enable));
    }

    public synchronized void setPop3Server(String server)
    {
        properties.setProperty("pop3.server", server);
    }

    public synchronized void setPop3Port(int port)
    {
        properties.setProperty("pop3.port", Integer.toString(port));
    }

    public synchronized String getSmtpServer()
    {
        return properties.getProperty("smtp.server");
    }

    public synchronized int getSmtpPort()
    {
        return Integer.parseInt(properties.getProperty("smtp.port"));
    }

    public synchronized boolean getTtls()
    {
        return Boolean.parseBoolean(properties.getProperty("ttls"));
    }

    public synchronized boolean getAuthentication()
    {
        return Boolean.parseBoolean(properties.getProperty("authentication"));
    }

    public synchronized String getEmail()
    {
        return properties.getProperty("email");
    }

    public synchronized String getPassword()
    {
        return properties.getProperty("password");
    }

    public synchronized boolean getEnableSoundEffect()
    {
        return Boolean.parseBoolean(properties.getProperty("enableSoundEffect"));
    }

    public synchronized double getVolume()
    {
        return Double.parseDouble(properties.getProperty("volume"));
    }

    public synchronized boolean getEnableNotifications()
    {
        return Boolean.parseBoolean(properties.getProperty("enableNotifications"));
    }

    public synchronized String getPop3Server()
    {
        return properties.getProperty("pop3.server");
    }

    public synchronized int getPop3Port()
    {
        return Integer.parseInt(properties.getProperty("pop3.port"));
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
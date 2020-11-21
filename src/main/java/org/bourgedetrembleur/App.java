package org.bourgedetrembleur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    private static final MailManager mailManager = new MailManager();
    private static final SendEmailService sendEmailService = new SendEmailService(mailManager);

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mail"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        App.stage = stage;
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
        clearNotifications();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static MailManager getMailManager()
    {
        return mailManager;
    }

    public static SendEmailService getSendEmailService()
    {
        return sendEmailService;
    }

    public static Scene getScene()
    {
        return scene;
    }

    public static Stage getStage()
    {
        return stage;
    }

    public static void notification(String caption, String message, TrayIcon.MessageType type)
    {
        if(!getMailManager().getSettings().getEnableNotifications()) return;
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("java.png");
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        try
        {
            tray.add(trayIcon);
        } catch (AWTException e)
        {
            e.printStackTrace();
        }

        trayIcon.displayMessage(caption, message, type);
    }

    public static void clearNotifications()
    {
        System.err.println("clear notifications");
        SystemTray tray = SystemTray.getSystemTray();
        for(var icon : tray.getTrayIcons())
        {
            tray.remove(icon);
        }
    }
}
package org.bourgedetrembleur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private static final MailManager mailManager = new MailManager();
    private static final SendEmailService sendEmailService = new SendEmailService(mailManager);

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mail"));
        stage.setScene(scene);
        stage.show();
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
}
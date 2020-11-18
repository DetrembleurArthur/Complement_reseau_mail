package org.bourgedetrembleur;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import javax.mail.MessagingException;
import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MailController implements Initializable
{
    @FXML
    TabPane mainTabPane;

    @FXML
    Label messageLabel;

    @FXML
    ProgressIndicator mailSendingProgressIndicator;

    @FXML
    ComboBox<Receiver> destinataireSelection;

    @FXML
    Label emailLabel;

    @FXML
    TextField nomDestinataire;

    @FXML
    TextField emailDestinataire;

    @FXML
    Button ajoutCarnetButton;

    @FXML
    TextField mailObjetLabel;

    @FXML
    TextArea mailMessageLabel;

    @FXML
    Button sendButton;

    //Carnet d'adresses
    @FXML
    TableView<Receiver> carnetAddrTableView;

    //Paramètres
    @FXML
    TextField paramSmtpServerTextField;
    @FXML
    Spinner<Integer> paramPortSpinner;
    @FXML
    CheckBox paramTtlsCheckBox;
    @FXML
    CheckBox paramAuthenticationCheckBox;
    @FXML
    TextField paramEmailTextField;
    @FXML
    PasswordField paramPasswordTextField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        App.getMailManager().getSettings().load();
        App.getSendEmailService().setOnFailed(workerStateEvent -> {
            messageLabel.setText("Impossible d'envoyer l'email: " + workerStateEvent.getSource().getException().getMessage());
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(mailSendingProgressIndicator);
            fadeTransition.setDuration(Duration.millis(1000));
            fadeTransition.setFromValue(1f);
            fadeTransition.setToValue(0f);
            fadeTransition.playFromStart();
        });
        App.getSendEmailService().setOnSucceeded(workerStateEvent -> {
            messageLabel.setText("Mail envoyé");
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(mailSendingProgressIndicator);
            fadeTransition.setDuration(Duration.millis(3000));
            fadeTransition.setFromValue(1f);
            fadeTransition.setToValue(0f);
            fadeTransition.playFromStart();
        });
        App.getSendEmailService().setOnRunning(workerStateEvent -> {
            messageLabel.setText("Envoi du mail");
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(mailSendingProgressIndicator);
            fadeTransition.setDuration(Duration.millis(1000));
            fadeTransition.setFromValue(0f);
            fadeTransition.setToValue(1f);
            fadeTransition.playFromStart();
        });
        mailSendingProgressIndicator.progressProperty().bind(App.getSendEmailService().progressProperty());

        initSettingsControls();
        initCarnetAddrControls();
    }

    public void initCarnetAddrControls()
    {
        TableColumn<Receiver, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setMinWidth(300);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Receiver, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setMinWidth(530);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        carnetAddrTableView.getColumns().clear();
        carnetAddrTableView.getColumns().addAll(nameColumn, emailColumn);

        Receiver.setReceivers(carnetAddrTableView.getItems());
        Receiver.load();

        destinataireSelection.itemsProperty().bind(carnetAddrTableView.itemsProperty());

        emailLabel.setText("no email");
        destinataireSelection.setOnAction(actionEvent -> emailLabel.setText(destinataireSelection.getValue().getEmail()));
    }

    public void initSettingsControls()
    {
        paramPasswordTextField.disableProperty().bind(paramAuthenticationCheckBox.selectedProperty().not());

        paramSmtpServerTextField.setText(App.getMailManager().getSettings().getSmtpServer());
        paramPortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535));
        paramPortSpinner.getValueFactory().setValue(App.getMailManager().getSettings().getSmtpPort());
        paramTtlsCheckBox.setSelected(App.getMailManager().getSettings().getTtls());
        paramAuthenticationCheckBox.setSelected(App.getMailManager().getSettings().getAuthentication());
        paramEmailTextField.setText(App.getMailManager().getSettings().getEmail());
        paramPasswordTextField.setText(App.getMailManager().getSettings().getPassword());
    }

    @FXML
    public void saveSettings_Action()
    {
        Settings settings = App.getMailManager().getSettings();
        settings.setSmtpServer(paramSmtpServerTextField.getText());
        settings.setSmtpPort(paramPortSpinner.getValue());
        settings.setTtls(paramTtlsCheckBox.isSelected());
        settings.setAuthentication(paramAuthenticationCheckBox.isSelected());
        settings.setEmail(paramEmailTextField.getText());
        settings.setPassword(paramPasswordTextField.getText());
        settings.save();
    }

    @FXML
    public void addReceiver_Action()
    {
        String name = nomDestinataire.getText();
        String email = emailDestinataire.getText();
        if(name.isBlank() || email.isBlank())
        {
            messageLabel.setText("Les champs doivent être remplis");
        }
        else
        {
            carnetAddrTableView.getItems().add(new Receiver(name, email));
            nomDestinataire.clear();
            emailDestinataire.clear();
            messageLabel.setText(name + " a été ajouté au carnet d'adresses");
        }
    }

    @FXML
    public void quit_Action()
    {
        Receiver.save();
        Platform.exit();
    }

    @FXML
    public void sendEmail_Action()
    {
        if(!App.getSendEmailService().isRunning())
        {
            if(emailLabel.getText().isBlank() || mailObjetLabel.getText().isBlank() || mailMessageLabel.getText().isBlank())
            {
                messageLabel.setText("Les champs doivent être remplis");
            }
            else
            {
                App.getSendEmailService().setInfos(
                        emailLabel.getText(),
                        mailObjetLabel.getText(),
                        mailMessageLabel.getText());
                App.getSendEmailService().reset();
                App.getSendEmailService().start();
            }
        }
        else
        {
            messageLabel.setText("Attendez que le mail précédent soit envoyé");
        }
    }
}

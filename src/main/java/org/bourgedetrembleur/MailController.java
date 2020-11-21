package org.bourgedetrembleur;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
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
    HTMLEditor htmlMessageTextArea;

    @FXML
    Tab plainTextTab;

    @FXML
    Button sendButton;

    @FXML
    ComboBox<File> attachedFilesComboBox;

    //Carnet d'adresses
    @FXML
    TableView<Receiver> carnetAddrTableView;

    //Paramètres
    @FXML
    TextField paramSmtpServerTextField;
    @FXML
    Spinner<Integer> paramSmtpPortSpinner;
    @FXML
    CheckBox paramSmtpTtlsCheckBox;
    @FXML
    CheckBox paramSmtpAuthenticationCheckBox;
    @FXML
    TextField paramEmailTextField;
    @FXML
    PasswordField paramPasswordTextField;
    @FXML
    CheckBox paramEnableSoundEffectCheckBox;
    @FXML
    Slider paramVolumeSlider;

    @FXML
    CheckBox paramEnableNotificationsCheckBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        SoundManager.MAIL_SEND_SOUND.muteProperty().bind(paramEnableSoundEffectCheckBox.selectedProperty().not());
        SoundManager.MAIL_RECV_SOUND.muteProperty().bind(paramEnableSoundEffectCheckBox.selectedProperty().not());

        SoundManager.MAIL_SEND_SOUND.volumeProperty().bind(paramVolumeSlider.valueProperty());
        SoundManager.MAIL_RECV_SOUND.volumeProperty().bind(paramVolumeSlider.valueProperty());

        paramVolumeSlider.disableProperty().bind(paramEnableSoundEffectCheckBox.selectedProperty().not());

        App.getMailManager().getSettings().load();
        App.getSendEmailService().setOnFailed(workerStateEvent -> {
            messageLabel.setText("Impossible d'envoyer l'email");
            App.notification("Mail error", workerStateEvent.getSource().getException().getMessage(), TrayIcon.MessageType.ERROR);
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(mailSendingProgressIndicator);
            fadeTransition.setDuration(Duration.millis(1000));
            fadeTransition.setFromValue(1f);
            fadeTransition.setToValue(0f);
            fadeTransition.playFromStart();
        });
        App.getSendEmailService().setOnSucceeded(workerStateEvent -> {
            messageLabel.setText("Mail envoyé");
            mailMessageLabel.clear();
            mailObjetLabel.clear();
            htmlMessageTextArea.setHtmlText("");
            App.notification("Mail envoyé", "Succès!", TrayIcon.MessageType.INFO);
            SoundManager.MAIL_SEND_SOUND.stop();
            SoundManager.MAIL_SEND_SOUND.play();
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
        emailColumn.setMinWidth(611);
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
        paramPasswordTextField.disableProperty().bind(paramSmtpAuthenticationCheckBox.selectedProperty().not());

        paramSmtpServerTextField.setText(App.getMailManager().getSettings().getSmtpServer());
        paramSmtpPortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535));
        paramSmtpPortSpinner.getValueFactory().setValue(App.getMailManager().getSettings().getSmtpPort());
        paramSmtpTtlsCheckBox.setSelected(App.getMailManager().getSettings().getTtls());
        paramSmtpAuthenticationCheckBox.setSelected(App.getMailManager().getSettings().getAuthentication());
        paramEmailTextField.setText(App.getMailManager().getSettings().getEmail());
        paramPasswordTextField.setText(App.getMailManager().getSettings().getPassword());
        paramEnableSoundEffectCheckBox.setSelected(App.getMailManager().getSettings().getEnableSoundEffect());
        paramVolumeSlider.setValue(App.getMailManager().getSettings().getVolume());
        paramEnableNotificationsCheckBox.setSelected(App.getMailManager().getSettings().getEnableNotifications());

    }

    @FXML
    public void saveSettings_Action()
    {
        Settings settings = App.getMailManager().getSettings();
        settings.setSmtpServer(paramSmtpServerTextField.getText());
        settings.setSmtpPort(paramSmtpPortSpinner.getValue());
        settings.setTtls(paramSmtpTtlsCheckBox.isSelected());
        settings.setAuthentication(paramSmtpAuthenticationCheckBox.isSelected());
        settings.setEmail(paramEmailTextField.getText());
        settings.setPassword(paramPasswordTextField.getText());
        settings.setEnableSoundEffect(paramEnableSoundEffectCheckBox.isSelected());
        settings.setVolume(paramVolumeSlider.getValue());
        settings.setEnableNotifications(paramEnableNotificationsCheckBox.isSelected());
        settings.save();
        messageLabel.setText("Paramètres sauvegardés");
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
            Receiver receiver = new Receiver(name, email);
            if(!carnetAddrTableView.getItems().contains(receiver))
            {
                carnetAddrTableView.getItems().add(receiver);
                Receiver.save();
                nomDestinataire.clear();
                emailDestinataire.clear();
                messageLabel.setText(name + " a été ajouté au carnet d'adresses");
            }
            else
            {
                messageLabel.setText(name + " est déja présent dans le carnet d'adresses");
            }
        }
    }

    @FXML
    public void quit_Action()
    {
        App.getStage().close();
    }

    @FXML
    public void sendEmail_Action()
    {
        if(!App.getSendEmailService().isRunning())
        {
            if(emailLabel.getText().isBlank() || mailObjetLabel.getText().isBlank())
            {
                messageLabel.setText("Les champs doivent être remplis");
            }
            else
            {
                String text = plainTextTab.isSelected() ? mailMessageLabel.getText() : htmlMessageTextArea.getHtmlText();
                App.getSendEmailService().setInfos(
                        emailLabel.getText(),
                        mailObjetLabel.getText(),
                        text,
                        attachedFilesComboBox.getItems());
                App.getSendEmailService().reset();
                App.getSendEmailService().start();
            }
        }
        else
        {
            messageLabel.setText("Attendez que le mail précédent soit envoyé");
        }
    }

    @FXML
    public void addAttachedFile_Action()
    {
        FileChooser chooser = new FileChooser();
        var files = chooser.showOpenMultipleDialog(App.getStage());
        if(files != null)
        {
            attachedFilesComboBox.getItems().addAll(files);
            attachedFilesComboBox.getSelectionModel().selectLast();
        }
    }

    @FXML
    public void removeAttachedFile_Action()
    {
        var file = attachedFilesComboBox.getSelectionModel().getSelectedItem();
        if(file != null)
            attachedFilesComboBox.getItems().remove(file);

    }

    @FXML
    public void cancel_Action()
    {
        mailObjetLabel.clear();
        mailMessageLabel.clear();
        attachedFilesComboBox.getItems().clear();
    }

    @FXML
    public void testSmtp_Action()
    {
        switch(App.getMailManager().testSmtpAccess())
        {
            case 1:
                messageLabel.setText("SMTP connection success");
                App.notification("SMTP connection success", App.getMailManager().getSettings().getSmtpServer(), TrayIcon.MessageType.INFO);
                break;
            case 2:
                messageLabel.setText("SMTP autentication failed");
                App.notification("SMTP autentication failed", App.getMailManager().getSettings().getEmail(), TrayIcon.MessageType.WARNING);
                break;
            case 3:
                messageLabel.setText("SMTP server not recognized");
                App.notification("SMTP server not recognized", App.getMailManager().getSettings().getSmtpServer(), TrayIcon.MessageType.ERROR);
                break;
        }
    }
}

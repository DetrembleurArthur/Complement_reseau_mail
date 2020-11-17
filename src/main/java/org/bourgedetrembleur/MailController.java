package org.bourgedetrembleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.mail.MessagingException;
import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MailController implements Initializable
{
    @FXML
    TabPane mainTabPane;

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
        initSettingsControls();
        initCarnetAddrControls();
    }

    public void initCarnetAddrControls()
    {
        TableColumn<Receiver, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setMinWidth(220);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Receiver, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setMinWidth(300);
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
            JOptionPane.showMessageDialog(null, "Les champs ne doivent pas être vides");
        }
        else
        {
            carnetAddrTableView.getItems().add(new Receiver(name, email));
        }
    }

    @FXML
    public void quit_Action()
    {
        Receiver.save();
        Platform.exit();
    }

    @FXML
    public void sendEmail_Action() throws MessagingException
    {
        if(!App.getSendEmailService().isRunning())
        {
            App.getSendEmailService().setInfos(
                    emailLabel.getText(),
                    mailObjetLabel.getText(),
                    mailMessageLabel.getText());
            App.getSendEmailService().reset();
            System.out.println("Service reset");
            App.getSendEmailService().start();
            System.out.println("Service lancé");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Attendez que le mail précédent soit envoyé");
        }
    }
}

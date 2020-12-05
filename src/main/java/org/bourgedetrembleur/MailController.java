package org.bourgedetrembleur;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
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

    @FXML
    TextField paramPop3ServerTextField;

    @FXML
    Spinner<Integer> paramPop3PortSpinner;

    @FXML
    ListView<ViewMessage> inboxMailsListView;

    @FXML
    WebView mailContentRecvWebView;

    @FXML
    ComboBox<PieceView> attachedPiecesComboBox;


    @FXML
    Label fromRecvLabel;

    @FXML
    Label subjectRecvLabel;

    @FXML
    Label dateRecvLabel;

    @FXML
    SplitPane mailRecvSplitpane;

    @FXML
    Tab mailRecvTab;

    @FXML
    ComboBox<ViewMessage> choiceMailComboBox;

    @FXML
    TreeView<String> mailTreeView;

    @FXML
    Button listenPop3Button;

    @FXML
    VBox mailViewerBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        mailContentRecvWebView.getEngine().loadContent("Empty :(");
        inboxMailsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, viewMessage, t1) -> {
            try
            {
                select_receive_message();
            } catch (MessagingException | IOException e)
            {
                e.printStackTrace();
            }
        });

        App.getRecvEmailService().setOnFailed(workerStateEvent -> {
            System.err.println("clear");
            fromRecvLabel.setText("<value>");
            dateRecvLabel.setText("<value>");
            subjectRecvLabel.setText("<value>");
            mailContentRecvWebView.getEngine().loadContent("Empty :(");
            inboxMailsListView.getItems().clear();
            guilog("Pop3 listening stopped");
            App.notification("POP3 error", "Pop3 listening stopped", TrayIcon.MessageType.WARNING);
        });
        App.getRecvEmailService().valueProperty().addListener((observableValue, viewMessages, t1) -> {
            System.err.println("Update");
            if(t1 == null)
            {
                fromRecvLabel.setText("<value>");
                dateRecvLabel.setText("<value>");
                subjectRecvLabel.setText("<value>");
                mailContentRecvWebView.getEngine().loadContent("Empty :(");
                inboxMailsListView.getItems().clear();
            }
            else if(t1.size() > 0)
            {
                mailRecvTab.setText("Réception de mails (" + t1.size() + ")");
                App.notification("Nouveaux messages", t1.size() + " nouveaux messages", TrayIcon.MessageType.INFO);
                SoundManager.MAIL_RECV_SOUND.stop();
                SoundManager.MAIL_RECV_SOUND.play();
                inboxMailsListView.getItems().addAll(t1);
                inboxMailsListView.getItems().sort((a, b) -> {
                    try
                    {
                        return b.getMessage().getSentDate().compareTo(a.getMessage().getSentDate());
                    } catch (MessagingException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return -1;
                });
            }
        });

        mailRecvTab.setOnSelectionChanged((e)-> mailRecvTab.setText("Réception de mails"));


        SoundManager.MAIL_SEND_SOUND.muteProperty().bind(paramEnableSoundEffectCheckBox.selectedProperty().not());
        SoundManager.MAIL_RECV_SOUND.muteProperty().bind(paramEnableSoundEffectCheckBox.selectedProperty().not());

        SoundManager.MAIL_SEND_SOUND.volumeProperty().bind(paramVolumeSlider.valueProperty());
        SoundManager.MAIL_RECV_SOUND.volumeProperty().bind(paramVolumeSlider.valueProperty());

        paramVolumeSlider.disableProperty().bind(paramEnableSoundEffectCheckBox.selectedProperty().not());

        App.getMailManager().getSettings().load();


        listen_pop3_Action();


        App.getSendEmailService().setOnFailed(workerStateEvent -> {
            guilog("Impossible d'envoyer l'email");
            App.notification("Mail error", workerStateEvent.getSource().getException().getMessage(), TrayIcon.MessageType.ERROR);
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(mailSendingProgressIndicator);
            fadeTransition.setDuration(Duration.millis(1000));
            fadeTransition.setFromValue(1f);
            fadeTransition.setToValue(0f);
            fadeTransition.playFromStart();
        });
        App.getSendEmailService().setOnSucceeded(workerStateEvent -> {
            guilog("Mail envoyé");
            mailMessageLabel.clear();
            mailObjetLabel.clear();
            htmlMessageTextArea.setHtmlText("");
            attachedFilesComboBox.getItems().clear();
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
            guilog("Envoi du mail");
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(mailSendingProgressIndicator);
            fadeTransition.setDuration(Duration.millis(1000));
            fadeTransition.setFromValue(0f);
            fadeTransition.setToValue(1f);
            fadeTransition.playFromStart();
        });
        mailSendingProgressIndicator.progressProperty().bind(App.getSendEmailService().progressProperty());

        choiceMailComboBox.itemsProperty().bind(inboxMailsListView.itemsProperty());

        listenPop3Button.disableProperty().bind(App.getRecvEmailService().runningProperty());

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
        paramPop3ServerTextField.setText(App.getMailManager().getSettings().getPop3Server());
        paramPop3PortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535));
        paramPop3PortSpinner.getValueFactory().setValue(App.getMailManager().getSettings().getPop3Port());


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
        settings.setPop3Server(paramPop3ServerTextField.getText());
        settings.setPop3Port(paramPop3PortSpinner.getValue());
        settings.save();
        guilog("Paramètres sauvegardés");
    }

    public void guilog(String msg)
    {
        messageLabel.setText(Date.from(Instant.now()).toString() + " :: " + msg);
    }

    @FXML
    public void addReceiver_Action()
    {
        String name = nomDestinataire.getText();
        String email = emailDestinataire.getText();
        if(name.isBlank() || email.isBlank())
        {
            guilog("Les champs doivent être remplis");
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
                guilog(name + " a été ajouté au carnet d'adresses");
            }
            else
            {
                guilog(name + " est déja présent dans le carnet d'adresses");
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
                guilog("Les champs doivent être remplis");
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
            guilog("Attendez que le mail précédent soit envoyé");
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
                guilog("SMTP connection success");
                App.notification("SMTP connection success", App.getMailManager().getSettings().getSmtpServer(), TrayIcon.MessageType.INFO);
                break;
            case 2:
                guilog("SMTP autentication failed");
                App.notification("SMTP autentication failed", App.getMailManager().getSettings().getEmail(), TrayIcon.MessageType.WARNING);
                break;
            case 3:
                guilog("SMTP server not recognized");
                App.notification("SMTP server not recognized", App.getMailManager().getSettings().getSmtpServer(), TrayIcon.MessageType.ERROR);
                break;
        }
    }

    @FXML
    public void delete_Action()
    {
        Receiver receiver = carnetAddrTableView.getSelectionModel().getSelectedItem();
        if(receiver != null)
        {
            carnetAddrTableView.getItems().remove(receiver);
            Receiver.save();
        }
    }

    public void select_receive_message() throws MessagingException, IOException
    {


        var message = inboxMailsListView.getSelectionModel().getSelectedItem();

        if(message != null)
        {
            mailContentRecvWebView.getEngine().loadContent("");
            attachedPiecesComboBox.getItems().clear();

            fromRecvLabel.setText(message.getMessage().getFrom()[0].toString());
            subjectRecvLabel.setText(message.getMessage().getSubject());
            dateRecvLabel.setText(message.getMessage().getSentDate().toString());

            String text = message.getMessageText();
            if(text == null)
            {
                for(var part : message.getParts())
                {
                    System.err.println(part.getContentType());


                    String disp = part.getDisposition();
                    System.err.println(">>>>> " + part.getContentType());
                    if(part.isMimeType("text/plain") || part.isMimeType("text/html"))
                    {
                        mailContentRecvWebView.getEngine().loadContent((String) part.getContent());
                    }
                    else if(disp != null && disp.equalsIgnoreCase(Part.ATTACHMENT))
                    {
                        System.err.println("Attachement: " + part.getFileName());

                        attachedPiecesComboBox.getItems().add(new PieceView(part));
                    }
                }
            }
            else
            {
                mailContentRecvWebView.getEngine().loadContent(text);
            }
        }
    }

    @FXML
    public void download_piece_Action() throws IOException, MessagingException
    {
        PieceView pieceView = attachedPiecesComboBox.getValue();
        if(pieceView != null)
        {
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(pieceView.getPart().getFileName());
            var files = chooser.showSaveDialog(App.getStage());
            if(files != null)
            {
                InputStream inputStream = pieceView.getPart().getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int c;
                while((c = inputStream.read()) != -1)
                {
                    byteArrayOutputStream.write(c);
                }
                byteArrayOutputStream.flush();

                FileOutputStream fileOutputStream = new FileOutputStream(files.getAbsoluteFile());
                byteArrayOutputStream.writeTo(fileOutputStream);
                fileOutputStream.close();
            }
        }
    }



    @FXML
    public void analyse_message_Action() throws MessagingException
    {
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);
        mailTreeView.setRoot(root);

        ViewMessage message = choiceMailComboBox.getSelectionModel().getSelectedItem();
        if(message != null)
        {
            root.setValue("Mail: " + message.getMessage().getFrom()[0].toString());
            TreeItem<String> tracingItem = new TreeItem<>();
            tracingItem.setValue("Tracing");
            TreeItem<String> otherItem = new TreeItem<>();
            otherItem.setValue("Other");
            root.getChildren().addAll(tracingItem, otherItem);

            Enumeration<Header> headerEnum = message.getMessage().getAllHeaders();

            int i = 0;
            while(headerEnum.hasMoreElements()) if(headerEnum.nextElement().getName().equalsIgnoreCase("received")) i++;
            headerEnum = message.getMessage().getAllHeaders();
            Header header = headerEnum.nextElement();

            boolean first = true;
            while(headerEnum.hasMoreElements())
            {
                TreeItem<String> item = new TreeItem<>();

                if(header.getName().equalsIgnoreCase("received"))
                {
                    i--;
                    if(first)
                    {
                        item.setValue("RMTA");
                        first = false;
                    }
                    else if(i != 0)
                    {
                        item.setValue("MTA");
                    }
                    else
                    {
                        item.setValue("FMTA");
                    }

                    Tracker tracker = new Tracker(header.getValue());
                    TreeItem<String> from = new TreeItem<>();
                    from.setValue("FROM -> " + tracker.getFrom());
                    item.getChildren().add(from);

                    TreeItem<String> by = new TreeItem<>();
                    by.setValue("BY -> " + tracker.getBy());
                    item.getChildren().add(by);

                    TreeItem<String> _for = new TreeItem<>();
                    _for.setValue("FOR -> " + tracker.getFor());
                    item.getChildren().add(_for);

                    TreeItem<String> with = new TreeItem<>();
                    with.setValue("WITH -> " + tracker.getWith());
                    item.getChildren().add(with);

                    TreeItem<String> via = new TreeItem<>();
                    via.setValue("VIA -> " + tracker.getVia());
                    item.getChildren().add(via);


                    TreeItem<String> id = new TreeItem<>();
                    id.setValue("ID -> " + tracker.getId());
                    item.getChildren().add(id);

                    TreeItem<String> date = new TreeItem<>();
                    date.setValue("DATE -> " + tracker.getDate());
                    item.getChildren().add(date);
                    tracingItem.getChildren().add(item);
                }
                else
                {
                    item.setValue(header.getName().toUpperCase());
                    TreeItem<String> value = new TreeItem<>();
                    value.setValue(header.getValue());
                    item.getChildren().add(value);
                    otherItem.getChildren().add(item);

                }
                header = headerEnum.nextElement();
            }
        }
    }

    @FXML
    public void listen_pop3_Action()
    {
        if(!App.getRecvEmailService().isRunning())
        {
            App.getRecvEmailService().reset();
            App.getRecvEmailService().start();
        }
    }
}

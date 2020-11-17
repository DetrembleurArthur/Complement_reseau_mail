module org.bourgedetrembleur {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.mail;

    opens org.bourgedetrembleur to javafx.fxml;
    exports org.bourgedetrembleur;
}
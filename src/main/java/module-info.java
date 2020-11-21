module org.bourgedetrembleur {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.mail;
    requires javafx.media;
    requires javafx.web;
    requires activation;

    opens org.bourgedetrembleur to javafx.fxml;
    exports org.bourgedetrembleur;

    
}
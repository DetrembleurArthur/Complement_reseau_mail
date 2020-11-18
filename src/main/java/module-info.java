module org.bourgedetrembleur {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.mail;
    requires transitive javafx.media;

    opens org.bourgedetrembleur to javafx.fxml;
    exports org.bourgedetrembleur;

    
}
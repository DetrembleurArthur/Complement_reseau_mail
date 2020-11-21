package org.bourgedetrembleur;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager
{
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static final MediaPlayer MAIL_SEND_SOUND = new MediaPlayer(
        new Media(SoundManager.class.getResource("mail-send."
        + ((OS.indexOf("mac") >= 0)? "m4r" : "mp3")).toString()));
    public static final MediaPlayer MAIL_RECV_SOUND = new MediaPlayer(
        new Media(SoundManager.class.getResource("mail-recv."
        + ((OS.indexOf("mac") >= 0)? "m4r" : "mp3")).toString()));
}

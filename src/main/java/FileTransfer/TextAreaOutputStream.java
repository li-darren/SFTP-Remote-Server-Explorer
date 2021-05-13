package FileTransfer;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    TextArea textArea = null;

    TextAreaOutputStream(TextArea textArea){
        this.textArea = textArea;
        this.textArea.setWrapText(true);
        this.textArea.setEditable(false);
    }

    @Override
    public void write(int b){
        Platform.runLater(new Runnable() {
            public void run() {
                System.out.print(String.valueOf((char) b));
                textArea.appendText(String.valueOf((char) b));
                textArea.appendText("");
            }
        });
    }
}

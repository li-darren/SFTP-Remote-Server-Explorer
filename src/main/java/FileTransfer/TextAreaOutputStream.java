package FileTransfer;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

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
                textArea.appendText(String.valueOf((char) b));
                textArea.appendText("");
            }
        });
    }
}

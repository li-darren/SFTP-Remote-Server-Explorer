package FileTransfer;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class FileListCell extends ListCell<String> {


    @Override
    public void updateItem(String item, boolean empty){
        super.updateItem(item, empty);

        if (empty){
            setText(null);
            setGraphic(null);
        }
        else{
            Image image = IconFetcher.getFileIcon(item);
            setGraphic(new ImageView(image));
            setText(item);
        }

    }

}

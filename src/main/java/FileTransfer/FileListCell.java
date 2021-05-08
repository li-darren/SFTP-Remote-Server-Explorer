package FileTransfer;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FileListCell extends ListCell<FileInfo>{

    @Override
    public void updateItem(FileInfo entry, boolean empty){
        super.updateItem(entry, empty);

        if (empty){
            setText(null);
            setGraphic(null);
        }
        else{
            Image image = IconFetcher.getFileIcon(entry.getFileName());
            setGraphic(new ImageView(image));
            setText(entry.getFileName());
        }

    }

}

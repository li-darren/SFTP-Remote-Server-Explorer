package FileTransfer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class IconFetcher {

    private static HashMap<String, Image> extensionFileIconMapping = new HashMap<>();

    private static String getFileExtension(String name){
        String extension = ".";
        int indexOfExtension = name.lastIndexOf(extension);
        if (indexOfExtension >= 0){
            extension = name.substring(indexOfExtension);
        }
        return extension.toLowerCase();
    }

    private static Icon getJSwingIconFromSystem(File file){

        boolean isWindows = true;

        Icon icon;

        if (isWindows){
            FileSystemView view = FileSystemView.getFileSystemView();
            icon = view.getSystemIcon(file);
        }
        else{
            final JFileChooser fileChooser = new JFileChooser();
            icon = fileChooser.getUI().getFileView(fileChooser).getIcon(file);
        }

        return icon;
    }

    public static Image getFileIcon(String fileName){

        final String extension = getFileExtension(fileName);
        Image icon = extensionFileIconMapping.get(extension);

        if (icon == null) {
            File file = new File(fileName);

            Icon fileIcon = null;

            if (file.exists()) {
                fileIcon = getJSwingIconFromSystem(file);
            } else {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("tempFile", extension);
                    fileIcon = getJSwingIconFromSystem(tempFile);
                } catch (Exception e) {
                    if (App.DEBUGGING) {
                        System.out.println("Failed to get icon and could not create temp file...");
                        e.printStackTrace();
                    }
                } finally {
                    if (tempFile != null) {
                        tempFile.delete();
                    }
                }
            }

            if (fileIcon != null){
                icon = convertJSwingIconToImage(fileIcon);
                extensionFileIconMapping.put(extension, icon);
            }

        }

        return icon;

    }

    private static Image convertJSwingIconToImage(Icon jSwingIcon){
        BufferedImage bufferedImage = new BufferedImage(jSwingIcon.getIconWidth(), jSwingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jSwingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }



}

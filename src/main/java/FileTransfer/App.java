/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package FileTransfer;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class App extends Application {

    public final static boolean DEBUGGING = true;
    private FileSender fileSender = null;
    private TextField currentUrl = null;
    ListView<FileInfo> folderItems = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();

        ImageView dragAndDropArea = new ImageView("DragAndDropIcon.png");

        dragAndDropArea.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().hasFiles()){
                    if (DEBUGGING){
                        System.out.println("Dragged Over Drag and Drop....");
                    }
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        });

        dragAndDropArea.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (DEBUGGING){
                    System.out.println("Dropped Onto Drag and Drop.....");
                }

                List<File> files = event.getDragboard().getFiles();
                if (DEBUGGING){
                    for (File f : files){
                        System.out.println(String.format("File dragged: %s", f.toString()));
                    }
                }
            }
        });

        root.setLeft(dragAndDropArea);

        VBox topBar = new VBox();
        currentUrl = new TextField("/");

        currentUrl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (DEBUGGING){
                    System.out.println("Changing directory from url bar....");
                }
                setRemoteDirectoryToUrlBar();
                updateUrlBarAndDirectories();
            }
        });

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        menuBar.getMenus().addAll(menuFile);

        topBar.getChildren().addAll(menuBar, currentUrl);

        root.setTop(topBar);

        folderItems = new ListView();

        //initialize the folder icon
        folderItems.getItems().add(new FileInfo(".", null));

        folderItems.setCellFactory(lv -> new ListCell<FileInfo>(){
            @Override
            public void updateItem(FileInfo entry, boolean empty){
                super.updateItem(entry, empty);

                if (empty){
                    setText(null);
                    setGraphic(null);
                }
                else{
                    Image image;

                    if (entry.getSftpATTRS() == null || entry.getSftpATTRS().isDir()){
                        image = IconFetcher.getFileIcon(".");
                    }
                    else{
                        image = IconFetcher.getFileIcon(entry.getFileName());
                    }

                    setGraphic(new ImageView(image));
                    setText(entry.getFileName());
                    setOnMouseClicked(mouseClickedEvent -> {
                        if (mouseClickedEvent.getButton().equals(MouseButton.PRIMARY) && mouseClickedEvent.getClickCount() == 2){
                            if (DEBUGGING){
                                System.out.println(String.format("Item: %s has been double clicked", entry.getFileName()));
                            }
                        }
                    });
                }
            }
        });

        root.setCenter(folderItems);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("File Transfer");
        primaryStage.show();

//        SSHSessionCredentials credentials = null;
//
//        while (credentials == null || isInvalidCredentials(credentials)){
//            credentials = promptUserForConnection();
//        }
//
//        if (DEBUGGING){
//            System.out.println(credentials.getHostName());
//            System.out.println(credentials.getUsername());
//            System.out.println(credentials.getPassword());
//        }
//
//        configureJschClient(credentials);
        testSendFile();
        updateUrlBarAndDirectories();

    }

    private void updateUrlBarAndDirectories(){

        System.out.println(String.format("File Sender text: %s", fileSender.getRemotePath()));
        currentUrl.setText(fileSender.getRemotePath());

        try{
            folderItems.getItems().clear();

            ArrayList<FileInfo> directoryFiles = new ArrayList<>();

            for (ChannelSftp.LsEntry entry : fileSender.listItems()) {
                directoryFiles.add(new FileInfo(entry.getFilename(), entry.getAttrs()));
            }

            Collections.sort(directoryFiles);

            folderItems.getItems().addAll(directoryFiles);
        }
        catch(SftpException  e){
            if (DEBUGGING){
                System.out.println("Failed to set directory and path....");
                e.printStackTrace();
            }
            //todo: show warning dialog
        }
    }

    private void setRemoteDirectoryToUrlBar(){
        fileSender.setRemotePath(currentUrl.getText());
    }

    private SSHSessionCredentials promptUserForConnection(){
        Dialog<SSHSessionCredentials> hostAndUserNameDialog = new Dialog<>();
        hostAndUserNameDialog.setTitle("Host and Username");
        hostAndUserNameDialog.setHeaderText("Please enter your hostname, username, and password");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        hostAndUserNameDialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField hostnameField = new TextField();
        hostnameField.setPromptText("hostname");
        TextField usernameField = new TextField();
        usernameField.setPromptText("username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("password");

        gridPane.add(new Label("Hostname:"), 0, 0);
        gridPane.add(hostnameField, 1, 0);
        gridPane.add(new Label("Username:"), 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(new Label("Password:"), 0, 2);
        gridPane.add(passwordField, 1, 2);


        hostAndUserNameDialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> hostnameField.requestFocus());

        hostAndUserNameDialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType){
                return new SSHSessionCredentials(hostnameField.getText(), usernameField.getText(), passwordField.getText());
            }
            return null;
        });

        Optional<SSHSessionCredentials> sshSessionCredentialsOptional = hostAndUserNameDialog.showAndWait();

        if (sshSessionCredentialsOptional.isPresent()){
            return sshSessionCredentialsOptional.get();
        }
        else{
            System.out.flush();
            System.exit(0);
        }

        return null;

    }

    private boolean isInvalidCredentials(SSHSessionCredentials credentials) {
        return credentials == null ||
                isEmptyOrNullString(credentials.getHostName()) ||
                isEmptyOrNullString(credentials.getUsername()) ||
                isEmptyOrNullString(credentials.getPassword());
    }


    private boolean isEmptyOrNullString(String s){
        return s == null || s.length() == 0;
    }

    public void sendFile(String fileName){

        if (fileSender == null){
            if (DEBUGGING){
                System.out.println("Trying to send when Jsch is not configured");
            }
            //todo: not configured yet.
            return;
        }

        fileSender.sendFile(fileName);
    }

    public void configureJschClient(SSHSessionCredentials credentials){
        String knownHosts = System.getenv("USERPROFILE").concat("\\.ssh\\known_hosts");
        String saveFile = System.getenv("APPDATA").concat("\\FileTransfer");

        if (App.DEBUGGING){
            System.out.printf("Known Host: %s%n", knownHosts);
            System.out.printf("Save File: %s%n", saveFile);

            File knownHostsFile = new File(knownHosts);
            File saveFileFile = new File(saveFile);

            if (knownHostsFile.exists()){
                System.out.println("Known Hosts Exists");
            }
            if (saveFileFile.exists()){
                System.out.println("Save File Exists");
            }
        }

        this.fileSender = new FileSender();

        try{
            fileSender.configureJsch(knownHosts, credentials.getHostName(), credentials.getUsername(), credentials.getPassword());
        }
        catch (JSchException e){
            if (DEBUGGING){
                e.printStackTrace();
                System.err.println("JSchException has been thrown...");
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error Configuring JSch Client");
            alert.setHeaderText("Error Configuring JSch Client");
            alert.setContentText("Would you like to retry?");
            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            ButtonType cancelButton = new ButtonType("Yes", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

            alert.showAndWait().ifPresent(type -> {
                if (DEBUGGING){
                    System.out.println(String.format("Button Type Pressed: %s", type.toString()));
                }

                if (type == ButtonType.YES) {

                }
                else if (type == ButtonType.NO) {

                }
                else if (type == ButtonType.CANCEL){

                }
            });

            //todo: failed to configure....
        }

        if (App.DEBUGGING){
            System.out.println("Done Configuring Jsch!");
        }

    }

    public void testSendFile(){

        Properties loginProperties = new Properties();

        try (FileReader in = new FileReader("login.properties")) {
            loginProperties.load(in);
        }
        catch(Exception e){
            if (App.DEBUGGING){
                System.out.println("Failed to load login properties...");
            }
            return;
        }

        String hostname = "linux.student.cs.uwaterloo.ca";
        String username = loginProperties.getProperty("username");
        String password = loginProperties.getProperty("password");

        configureJschClient(new SSHSessionCredentials(hostname, username, password));
        sendFile("test.txt");
    }




}

package FileTransfer;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.nio.file.Path;

public class FileMonitor {

    public final static long pollingInterval = 500;

    private FileSender fileSender = null;
    private FileAlterationMonitor fileAlterationMonitor = null;
    private Path rootFileToMonitor = null;

    FileMonitor(File fileToMonitor, FileSender fileSender){
        this.fileSender =  fileSender;
        initializeFileMonitor(fileToMonitor);
        this.rootFileToMonitor = fileToMonitor.toPath();
    }

    private void initializeFileMonitor(File fileToMonitor){

        if (fileToMonitor == null || !fileToMonitor.exists()){
            throw new RuntimeException(String.format("Failed to monitor %s, does not exist", fileToMonitor.toString()));
            //todo: let user know
        }

        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(fileToMonitor);
        fileAlterationMonitor = new FileAlterationMonitor(pollingInterval);
        FileAlterationListener fileAlterationListener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                Path relativePath = rootFileToMonitor.relativize(file.toPath());
                System.out.println(String.format("File created: %s", relativePath.toString()));
            }

            @Override
            public void onFileChange(File file) {
                Path relativePath = rootFileToMonitor.relativize(file.toPath());
                String relativePathString = relativePath.toString();
                relativePathString = relativePathString.replace("\\", "/");
                relativePathString = "/".concat(relativePathString);
                if (App.DEBUGGING){
                    System.out.println(String.format("File Changed: %s", file.toString()));
                }

                String localFileDestination = file.toString().replace("\\", "/");
                fileSender.sendFile(localFileDestination, relativePathString);
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println(String.format("Deleting file: %s", file.toString()));
            }
        };

        fileAlterationObserver.addListener(fileAlterationListener);
        fileAlterationMonitor.addObserver(fileAlterationObserver);
    }

    public void startFileMonitor() throws Exception {
        fileAlterationMonitor.start();
    }

}

package FileTransfer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileManager {

    private final WatchService watchService;
    private final FileSender fileSender;

    FileManager(FileSender fileSender) throws IOException {
        this.fileSender = fileSender;
        watchService = FileSystems.getDefault().newWatchService();
    }

    public static void main(String[] args){
        System.out.println("Test");
    }

    public void registerDirectory(Path path) throws IOException {
        if (path == null){
            if (App.DEBUGGING){
                System.out.println("Path is null, could not register....");
            }
            return;
        }

        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

    }

    public void pollInfinitely(){
        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) {
                        if (App.DEBUGGING) {
                            System.out.println(String.format("Error, detected event of type not modify %s", event.kind().toString()));
                        }
                    }

                    System.out.println(String.format("Modified file: %s", event.context().toString()));

                    if (!key.reset()) {
                        break;
                    }
                }
            }

            if (App.DEBUGGING){
                System.out.println("Broke out of watching cycle... invalid key");
            }

        }
        catch (Exception e){
            if (App.DEBUGGING){
                System.out.println("Having trouble watching file changes...");
            }

            //todo: notify user that there are troubles with file changes

        }
    }

}

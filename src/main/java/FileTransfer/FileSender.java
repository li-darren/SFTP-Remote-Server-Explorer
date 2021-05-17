package FileTransfer;

import com.jcraft.jsch.*;

import java.io.File;
import java.util.List;

public class FileSender {

    private ChannelSftp channelSftp = null;

    FileSender(Session jschSession) throws JSchException {
        this.channelSftp = (ChannelSftp) jschSession.openChannel("sftp");;
        this.channelSftp.connect();
    }

    public List<ChannelSftp.LsEntry> listItems() throws SftpException {
        if (!channelSftpConfigured()){
            return null;
        }
        return channelSftp.ls(".");
    }

    public static boolean isDotOrDotDotDirectory(String dir){
        return ".".equals(dir) || "..".equals(dir);
    }

    public List<ChannelSftp.LsEntry> ls (String location) throws SftpException {
        if (!channelSftpConfigured()){
            return null;
        }
        return channelSftp.ls(location);
    }

    public SftpATTRS stat (String path) throws SftpException {
        if (!channelSftpConfigured()){
            return null;
        }
        return channelSftp.stat(path);
    }

    private boolean channelSftpConfigured(){
        if (channelSftp == null){
            if (App.DEBUGGING){
                System.out.println("File Sender is not configured yet...");
            }
            //todo: filesender is not configured yet....
            return false;
        }
        return true;
    }

    public void mkdir(String dir) throws SftpException {
        if (!channelSftpConfigured()){
            return;
        }
        channelSftp.mkdir(dir);
    }

    public void rename(String oldPath, String newPath) throws SftpException {
        if (!channelSftpConfigured()){
            return;
        }
        channelSftp.rename(oldPath, newPath);
    }

    public boolean isDir(String dir) throws SftpException {
        return channelSftp.stat(dir).isDir();
    }

    public void removeFile(String fileName) throws SftpException {
        if (!channelSftpConfigured()){
            return;
        }
        channelSftp.rm(fileName);
    }
    public void removeAllFolderContents(String fileName) throws SftpException {
        if (!channelSftpConfigured()){
            return;
        }
        removeRecursively(getRemotePath() + "/" + fileName);
    }

    private void removeRecursively(String remoteDir) throws SftpException {
        for (ChannelSftp.LsEntry entry : (List<ChannelSftp.LsEntry>) channelSftp.ls(remoteDir)){
            if (! isDotOrDotDotDirectory(entry.getFilename())){
                String fileName = remoteDir + "/" + entry.getFilename();
                if (entry.getAttrs().isDir()){
                    removeRecursively(fileName);
                }
                else{
                    channelSftp.rm(fileName);
                }
            }
        }
        channelSftp.rmdir(remoteDir);
    }

    public void cd(String path){
        try{
            channelSftp.cd(path);
        }
        catch(SftpException e){
            if (App.DEBUGGING){
                System.out.println("Failed to change remote path");
                e.printStackTrace();
            }
            //todo: dialog saying failed to change remote path
        }
    }

    public String getRemotePath(){

        try{
            return channelSftp.pwd();
        }
        catch(SftpException e){
            if (App.DEBUGGING){
                System.out.println("Failed to get remote path");
                e.printStackTrace();
            }
            //todo: dialog saying failed to get remote path
        }

        return "";

    }

    public File getFile(String remoteFilePath, String localFilePath) throws SftpException {
        channelSftp.get(remoteFilePath, localFilePath);
        if (App.DEBUGGING){
            System.out.println(String.format("Downloaded file locally... %s", localFilePath));
        }
        return new File(localFilePath);
    }

    public void sendFile(String localFilePath, String remoteFilePath){

        if (!channelSftpConfigured()){
            return;
        }

        //todo: extract file name from path

        File fileToSend = new File(localFilePath);

        if (fileToSend.exists()){
            try{
                channelSftp.put(localFilePath, remoteFilePath);
            }
            catch (Exception e){
                if (App.DEBUGGING){
                    System.out.println("Failed to transfer files...");;
                    e.printStackTrace();
                }
                //todo: failed to transfer files....
            }
        }
        else{
            throw new RuntimeException(String.format("Failed to transfer file, file doesn't exist: %s", localFilePath));
        }

    }

}

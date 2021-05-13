package FileTransfer;

import com.jcraft.jsch.*;

import java.io.File;
import java.util.List;

public class FileSender {

    ChannelSftp channelSftp = null;

    FileSender(Session jschSession) throws JSchException {
        this.channelSftp = (ChannelSftp) jschSession.openChannel("sftp");;
        this.channelSftp.connect();
    }

    public List<ChannelSftp.LsEntry> listItems() throws SftpException {
        if (channelSftp == null){
            if (App.DEBUGGING){
                System.out.println("File Sender is not configured yet...");
            }
            //todo: filesender is not configured yet....
            return null;
        }

        return channelSftp.ls(".");
    }

    public void cd(String path){
        try{
            if (App.DEBUGGING){
                System.out.println(String.format("Changing remote path to: %s", path));
            }
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

    public void getFile(String remoteFilePath, String localFilePath) throws SftpException {
        channelSftp.get(remoteFilePath, localFilePath);
    }

    public void sendFile(String localFilePath, String remoteFilePath){

        if (channelSftp == null){
            if (App.DEBUGGING){
                System.out.println("File Sender is not configured yet...");
            }
            //todo: filesender is not configured yet....
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

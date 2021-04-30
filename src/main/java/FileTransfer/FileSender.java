package FileTransfer;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public class FileSender {

    ChannelSftp channelSftp = null;

    FileSender(){
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

    public void setRemotePath(String path){
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


    public void sendFile(String fileName){

        if (channelSftp == null){
            if (App.DEBUGGING){
                System.out.println("File Sender is not configured yet...");
            }
            //todo: filesender is not configured yet....
            return;
        }

        //todo: extract file name from path

        File fileToSend = new File(fileName);

        if (fileToSend.exists()){
            if (App.DEBUGGING){
                System.out.println("File Exists!");
            }

            try{
                channelSftp.put(fileName, fileName);
            }
            catch (Exception e){
                if (App.DEBUGGING){
                    e.printStackTrace();
                }
                //todo: failed to transfer files....
            }

        }

    }

    public void configureJsch(String hostsFileLocation, String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(hostsFileLocation);
        Session jschSession = jsch.getSession(username, host);
        jschSession.setPassword(password);

        jschSession.connect();
        this.channelSftp = (ChannelSftp) jschSession.openChannel("sftp");;
        this.channelSftp.connect();
    }

}

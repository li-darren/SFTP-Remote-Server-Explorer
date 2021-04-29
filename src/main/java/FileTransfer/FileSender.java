package FileTransfer;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;

public class FileSender {

    ChannelSftp channelSftp = null;

    FileSender(){
    }

    public void sendFile(String fileName){

        if (channelSftp == null){
            if (App.DEBUGGING){
                System.out.println("File Sender is not configured yet...");
            }
            //todo: filesender is not configured yet....
            return;
        }

        File fileToSend = new File(fileName);

        if (fileToSend.exists()){
            if (App.DEBUGGING){
                System.out.println("File Exists!");
            }

            try{
                channelSftp.cd("filetest");
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

        //todo: this is insecure, fix this
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);

        jschSession.connect();
        this.channelSftp = (ChannelSftp) jschSession.openChannel("sftp");;
        this.channelSftp.connect();

        if (App.DEBUGGING){
            System.out.println("Done Configuring Jsch!");
        }

    }

}

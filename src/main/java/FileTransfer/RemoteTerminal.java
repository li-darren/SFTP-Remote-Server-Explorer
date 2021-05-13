package FileTransfer;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class RemoteTerminal {

    ChannelShell channelShell  = null;
    PrintStream printStream = null;

    RemoteTerminal(Session jschSession, OutputStream outputStream) throws JSchException {
        channelShell = (ChannelShell) jschSession.openChannel("shell");;

//        channelShell.setInputStream(System.in);

        try {
            printStream = new PrintStream(channelShell.getOutputStream(), true);

            channelShell.setOutputStream(outputStream, true);
            channelShell.setPty(true);
//        channelShell.setPtyType("vt102");
            channelShell.connect();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendCommand(String command){
        printStream.print(command + "\n");
    }



}

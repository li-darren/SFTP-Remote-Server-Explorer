package FileTransfer;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JschSessionClient {

    Session jschSession = null;

    JschSessionClient(String hostsFileLocation, String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(hostsFileLocation);
        jschSession = jsch.getSession(username, host);
        jschSession.setPassword(password);

        //TODO: THIS IS TEMPORARY WORK AROUND
        jschSession.setConfig("StrictHostKeyChecking", "no");
        jschSession.connect();
    }

    public Session getJschSession() {
        return jschSession;
    }

}

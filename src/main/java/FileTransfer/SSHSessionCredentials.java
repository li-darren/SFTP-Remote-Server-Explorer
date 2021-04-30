package FileTransfer;

public class SSHSessionCredentials {

    //instances of this class is never stored
    //only used temporary to retriever data from user
    private final String hostName;
    private final String username;
    private final String password;

    SSHSessionCredentials(String hostName, String username, String password){
        this.hostName = hostName;
        this.username = username;
        this.password = password;
    }

    public String getHostName() {
        return hostName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

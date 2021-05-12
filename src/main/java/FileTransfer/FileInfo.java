package FileTransfer;

import com.jcraft.jsch.SftpATTRS;

public class FileInfo implements Comparable<FileInfo>{

    private String fileName;
    private SftpATTRS sftpATTRS;

    public FileInfo(String fileName, SftpATTRS sftpATTRS){
        this.fileName = fileName;
        this.sftpATTRS = sftpATTRS;
    }

    public String getFileName() {
        return fileName;
    }

    public SftpATTRS getSftpATTRS() {
        return sftpATTRS;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSftpATTRS(SftpATTRS sftpATTRS) {
        this.sftpATTRS = sftpATTRS;
    }

    @Override
    public int compareTo(FileInfo o) {

        if ((isDirectoryOrLink(this.sftpATTRS) && isDirectoryOrLink(o.getSftpATTRS())) || (!isDirectoryOrLink(this.sftpATTRS) && !isDirectoryOrLink(o.getSftpATTRS()))){
            return this.fileName.compareTo(o.getFileName());
        }

        if (isDirectoryOrLink(this.sftpATTRS)){
            return -1;
        }

        return 1;

    }

    public static boolean isDirectoryOrLink(SftpATTRS attrs){
        return attrs.isDir() || attrs.isLink();
    }

}

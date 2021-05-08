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

    @Override
    public int compareTo(FileInfo o) {

        if ((sftpATTRS.isDir() && o.getSftpATTRS().isDir()) || (!sftpATTRS.isDir() && !o.getSftpATTRS().isDir())){
            return this.fileName.compareTo(o.getFileName());
        }

        if (sftpATTRS.isDir()){
            return -1;
        }

        return 1;


    }
}

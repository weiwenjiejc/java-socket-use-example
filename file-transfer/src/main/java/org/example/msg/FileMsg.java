package org.example.msg;

public class FileMsg extends Msg {


    private static final long serialVersionUID = 3766593750087120862L;

    public FileMsg() {
        super(1);
    }


    private byte[] fileBytes;

    private String fileName;

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

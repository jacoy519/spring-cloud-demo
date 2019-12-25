package com.devchen.sftp.controller.resp;

public class SftpDownloadResult {

    private String localDir;

    private String fileName;

    private byte[] bytes;

    public String getLocalDir() {
        return localDir;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}

package com.devchen.sftp.controller.request;

public class SftpDownloadRequest {

    private String host;

    private String port;

    private String useName;

    private String password;

    private String remotePath;

    private String fileName;

    private String requestId;

    private long proxySftpMaxDownloadSec = 1800L;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUseName() {
        return useName;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getProxySftpMaxDownloadSec() {
        return proxySftpMaxDownloadSec;
    }

    public void setProxySftpMaxDownloadSec(long proxySftpMaxDownloadSec) {
        this.proxySftpMaxDownloadSec = proxySftpMaxDownloadSec;
    }
}

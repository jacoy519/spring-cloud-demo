package com.devchen.file.entity;

public class DownloadResult {

    private boolean isSuccess;

    private String errorMsg;

    public DownloadResult(boolean isSuccess, String errorMsg) {
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}

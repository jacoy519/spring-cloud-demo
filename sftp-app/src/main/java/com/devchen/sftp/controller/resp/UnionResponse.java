package com.devchen.sftp.controller.resp;

import com.devchen.sftp.constant.ResultCode;

public class UnionResponse<T> {

    private String resCode;

    private String resMsg;

    private T data;

    public UnionResponse(ResultCode resultCode) {
        this.resCode = resultCode.getCode();
        this.resMsg = resultCode.getDesc();
    }

    public UnionResponse(ResultCode resultCode, T data) {
        this.resCode = resultCode.getCode();
        this.resMsg = resultCode.getDesc();
        this.data = data;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }
}

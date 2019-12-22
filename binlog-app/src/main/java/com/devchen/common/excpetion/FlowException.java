package com.devchen.common.excpetion;

import com.devchen.common.constant.ResultCode;

public class FlowException extends RuntimeException {

    private ResultCode resultCode;

    private String message;

    public FlowException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public FlowException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}

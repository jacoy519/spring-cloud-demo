package com.devchen.acount.common.error;

public class UnionRuntimeException extends  RuntimeException{

    private String code;

    private String message;

    public UnionRuntimeException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public  String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

package com.devchen.sftp.constant;

public enum ResultCode {

    OK("0", "正确"),
    PROCESSING("8888", "处理中"),
    INNER_EXCEPTION("9999", "内部错误");

    ResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

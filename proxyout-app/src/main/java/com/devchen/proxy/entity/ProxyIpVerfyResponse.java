package com.devchen.proxy.entity;

import java.util.HashMap;
import java.util.Map;

public class ProxyIpVerfyResponse {

    private String msg;

    private String code;

    private Map<String, Boolean> data = new HashMap<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Boolean> getData() {
        return data;
    }

    public void setData(Map<String, Boolean> data) {
        this.data = data;
    }
}

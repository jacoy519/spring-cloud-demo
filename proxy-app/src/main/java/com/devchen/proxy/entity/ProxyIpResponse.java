package com.devchen.proxy.entity;

import com.devchen.proxy.service.ProxyIpService;

public class ProxyIpResponse {

    private String msg;

    private String code;

    private ProxyIpList data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public ProxyIpList getData() {
        return data;
    }

    public void setData(ProxyIpList data) {
        this.data = data;
    }
}

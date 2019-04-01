package com.devchen.proxy.entity;

import java.util.Date;

public class ProxyIpEntity {


    private String ip;

    private Date date;

    private String chromeProxyZip;


    public String getChromeProxyZip() {
        return chromeProxyZip;
    }

    public void setChromeProxyZip(String chromeProxyZip) {
        this.chromeProxyZip = chromeProxyZip;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}

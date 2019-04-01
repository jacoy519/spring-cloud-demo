package com.devchen.proxy.entity;

import java.util.HashMap;
import java.util.Map;

public class ProxyHttpRequest {

    private String url;

    private Map<String,String> headers = new HashMap<>();

    private Map<String,String> cookies = new HashMap<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void addCookies(String key, String value) {
        cookies.put(key,value);
    }
}

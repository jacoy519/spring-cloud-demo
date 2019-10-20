package com.devchen.proxy.entity;

import java.util.List;

public class ProxyIpList {

    private long count;

    private List<String> proxy_list;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<String> getProxy_list() {
        return proxy_list;
    }

    public void setProxy_list(List<String> proxy_list) {
        this.proxy_list = proxy_list;
    }
}

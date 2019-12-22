package com.devchen.proxy.entity;

import java.util.ArrayList;
import java.util.List;

public class QbResponse {

    private List<QbResponseEntity>  data = new ArrayList<>();

    public List<QbResponseEntity> getData() {
        return data;
    }

    public void setData(List<QbResponseEntity> data) {
        this.data = data;
    }
}

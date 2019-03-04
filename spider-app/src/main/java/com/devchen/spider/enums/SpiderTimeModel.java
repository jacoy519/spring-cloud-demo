package com.devchen.spider.enums;

public enum SpiderTimeModel {

    ONE_DAY(24L*60L*60L*1000L),
<<<<<<< HEAD
    TEN_MIN(10L*60L*1000L),
    THREE_DAY(3L*24L*60L*60L*1000L),
=======
>>>>>>> 5d9c7d42bd0f07d5186e7ee32dff8bb0572fd9e5
    ONE_MIN(60L*1000L);


    private long timeValue;

    SpiderTimeModel(long timeValue) {
        this.timeValue=timeValue;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(long timeValue) {
        this.timeValue = timeValue;
    }
}

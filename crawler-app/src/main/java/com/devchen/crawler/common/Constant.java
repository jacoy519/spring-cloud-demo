package com.devchen.crawler.common;

public class Constant {

    public final static  String TRANSMISSION_MERGE_DOWNLOAD_CMD_FORMAT = "transmission-cli %s --download-dir %s --port %s";

    public final static long DOWNLOAD_TASK_MAX_RUN_TIME = 24L * 3600L * 1000L;

    public final static long  ONE_MINUTE =  60 * 1000;

    public final static long THREE_HOUR = 180L * ONE_MINUTE;

    public final static long FIVE_MINUTE = 5L * ONE_MINUTE;

    public final static long TEN_MINUTE = 10L * ONE_MINUTE;

    public final static int MAX_DOWNLOAD_TASK_NUM = 5;

    public final static String DOWNLOAD_VIDEO_ROOT_DIR = "/owncloud/Video";
}

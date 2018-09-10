package com.devchen.crawler.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperty {

    @Value("${zimuzu.loginUrl}")
    private String zimuzuLoginUrl;

    @Value("${zimuzu.favUrl}")
    private String zimuzuFavUrl;

    @Value("${zimuzu.account}")
    private String zimuzuAccount;

    @Value("${zimuzu.password}")
    private String zimuzuPwd;

    @Value("${zimuzu.homeUrl}")
    private String zimuzuHome;

    @Value("${zimuzu.videoSaveDir}")
    private String videSavaDir;

    @Value("${getuploader.localSaveDir}")
    private String uploaderSaveDir;

    @Value("${sock5.ip}")
    private String proxyIp;

    @Value("${sock5.socket}")
    private int proxySocket;

    @Value("${pixiv.localSaveDir}")
    private String pixivSaveDir;


    public String getZimuzuAccount() {
        return zimuzuAccount;
    }

    public String getZimuzuFavUrl() {
        return zimuzuFavUrl;
    }

    public String getZimuzuLoginUrl() {
        return zimuzuLoginUrl;
    }

    public String getZimuzuPwd() {
        return zimuzuPwd;
    }

    public String getZimuzuHome() {return zimuzuHome;}

    public String getVideSavaDir() {return videSavaDir;}

    public String getUploaderSaveDir() {return uploaderSaveDir;}

    public String getProxyIp() {return proxyIp;}

    public int getProxySocket() {return proxySocket;}

    public String getPixivSaveDir() {return pixivSaveDir;}
}

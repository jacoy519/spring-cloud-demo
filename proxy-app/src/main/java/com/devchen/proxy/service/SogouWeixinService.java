package com.devchen.proxy.service;

import com.devchen.proxy.dal.dao.WeixinPageResultDAO;
import com.devchen.proxy.dal.dao.WeixinPageSourceDAO;
import com.devchen.proxy.dal.dao.WeixinSpiderTargetDAO;
import com.devchen.proxy.dal.entity.WeixinPageResultEntity;
import com.devchen.proxy.dal.entity.WeixinPageSourceEntity;
import com.devchen.proxy.dal.entity.WeixinSpiderTargetEntity;
import com.devchen.proxy.entity.ProxyIpEntity;
import com.devchen.proxy.entity.ProxyResultEntity;
import com.devchen.proxy.webDriver.IWebDriverHandler;
import com.devchen.proxy.webDriver.SogouWexinHandler;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SogouWeixinService {

    @Resource
    private SogouWexinHandler sogouWexinHandler;

    @Resource
    private WebDriverService webDriverService;

    @Resource
    private ProxyIpService proxyIpService;

    @Resource
    private SogouV2WeixinService sogouV2WeixinService;


    @Resource
    private WeixinSpiderTargetDAO weixinSpiderTargetDAO;

    @Resource
    private WeixinPageSourceDAO weixinPageSourceDAO;

    @Resource
    private WeixinPageResultDAO weixinPageResultDAO;

    @Value("${proxy.out}")
    private String proxyOutIp;


    private final static Logger logger = LoggerFactory.getLogger(SogouWeixinService.class);

    private String sogouWeixinTemplate= "https://weixin.sogou.com/weixin?type=1&s_from=input&query=%s&ie=utf8&_sug_=n&_sug_type_=";


    private String targetUrl = "http://%s/weixin-proxy-v2";




    private String getWeixinPageList(String weixin) {
        if("NFJJ4008898899".equals(weixin)) {
            weixin = "南方基金微视界";
        }
        return weixin;
    }

    public void saveWeixinPageUrl(String originId, String weixinId) {
       List<String> targetUrl = sogouV2WeixinService.targetList(weixinId);

       List<ProxyResultEntity> resultList = new ArrayList<>();

       for(String target : targetUrl) {
           ProxyResultEntity resultEntity = new ProxyResultEntity();
           target = target.replaceAll("https://mp\\.weixin\\.qq\\.com", "");
           resultEntity.setContent_url(target);
           resultList.add(resultEntity);
       }

       String gson = (new Gson()).toJson(resultList);


       WeixinPageResultEntity pageResult = weixinPageResultDAO.selectOne(originId);

       if(pageResult != null) {
           pageResult.setWeixinId(originId);
           pageResult.setPageUrl(gson);
           weixinPageResultDAO.updatePageUrl(pageResult);
       } else {
           pageResult = new WeixinPageResultEntity();
           pageResult.setWeixinId(originId);
           pageResult.setPageUrl(gson);
           weixinPageResultDAO.insertOne(pageResult);
       }


       WeixinPageSourceEntity sourceEntity = weixinPageSourceDAO.selectOne(weixinId);

       String askUrl = String.format("http://%s/weixin-proxy-v2?id=%s", proxyOutIp,originId);

       if(sourceEntity != null) {
           sourceEntity.setWeixinId(originId);
           sourceEntity.setPageUrl(askUrl);
           weixinPageSourceDAO.updatePageUrl(sourceEntity);
       } else {
           sourceEntity = new WeixinPageSourceEntity();
           sourceEntity.setWeixinId(originId);
           sourceEntity.setPageUrl(askUrl);
           weixinPageSourceDAO.insertOne(sourceEntity);
       }

    }

    public  void runSpider() {
        logger.info("start run weixin spider");
        List<WeixinSpiderTargetEntity> targets = weixinSpiderTargetDAO.selectAll();
        for(WeixinSpiderTargetEntity target : targets) {
            try {
                String weixinId = getWeixinPageList(target.getWeixinId());
                saveWeixinPageUrl(target.getWeixinId(),weixinId);
            }catch (Exception e) {
                logger.error("error", e);
            }

            try {
                int randMillis = Math.abs((new Random()).nextInt()%(1*60*1000)) + 1*60*1000;
              Thread.sleep(1L * 60L * 1000L + randMillis);
            } catch (Exception e) {
                logger.error("error", e);
            }

        }
    }



}

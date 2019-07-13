package com.devchen.proxy.service;

import com.devchen.proxy.dal.dao.WeixinPageSourceDAO;
import com.devchen.proxy.dal.dao.WeixinSpiderTargetDAO;
import com.devchen.proxy.dal.entity.WeixinPageSourceEntity;
import com.devchen.proxy.dal.entity.WeixinSpiderTargetEntity;
import com.devchen.proxy.entity.ProxyIpEntity;
import com.devchen.proxy.webDriver.IWebDriverHandler;
import com.devchen.proxy.webDriver.SogouWexinHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private WeixinSpiderTargetDAO weixinSpiderTargetDAO;

    @Resource
    private WeixinPageSourceDAO weixinPageSourceDAO;

    private final static Logger logger = LoggerFactory.getLogger(SogouWeixinService.class);

    private String sogouWeixinTemplate= "https://weixin.sogou.com/weixin?type=1&s_from=input&query=%s&ie=utf8&_sug_=n&_sug_type_=";

    public String getWeixinPageList(String weixin) {
        String url = String.format(sogouWeixinTemplate, weixin);
        return webDriverService.execWebDriverHandler(sogouWexinHandler, url);
    }

    public void saveWeixinPageUrl(String weixinId) {
        String url = getWeixinPageList(weixinId);
        WeixinPageSourceEntity page =weixinPageSourceDAO.selectOne(weixinId);
        if(page == null) {
            page = new WeixinPageSourceEntity();
            page.setPageUrl(url);
            page.setWeixinId(weixinId);
            weixinPageSourceDAO.insertOne(page);
        } else {
            page.setPageUrl(url);
            weixinPageSourceDAO.updatePageUrl(page);
        }

    }

    @Scheduled(fixedDelay = 5L* 3600L * 1000L)
    public  void runSpider() {
        logger.info("start run weixin spider");
        List<WeixinSpiderTargetEntity> targets = weixinSpiderTargetDAO.selectAll();
        for(WeixinSpiderTargetEntity url : targets) {
            try {
                saveWeixinPageUrl(url.getWeixinId());
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

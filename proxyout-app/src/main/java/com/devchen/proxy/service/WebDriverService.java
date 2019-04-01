package com.devchen.proxy.service;

import com.devchen.proxy.entity.ProxyIpEntity;
import com.devchen.proxy.webDriver.IWebDriverHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.Element;
import java.io.File;
import java.util.*;

@Service
public class WebDriverService {


    private final static Logger logger = LoggerFactory.getLogger(WebDriverService.class);


    public synchronized String execWebDriverHandler(IWebDriverHandler handler, String targetUrl) {

        logger.info(String.format("visit url %s" , targetUrl));
        ChromeOptions co = new ChromeOptions();
        //co.addExtensions(new File(proxyIpEntity.getChromeProxyZip()));      //将proxy的信息添加到ChromeOptions中
        co.addArguments("--no-sandbox","--disable-dev-shm-usage");
        co.addArguments("disable-gpu");

        Map<String, Object> prefs = new HashMap<String, Object>();

        prefs.put("profile.managed_default_content_settings.images",2); //禁止下载加载图片
        co.setExperimentalOption("prefs", prefs);
        System.setProperty("webdriver.chrome.driver","/root/chromedriver/2.35/chromedriver");

        WebDriver driver = new ChromeDriver(co);
        String result =null;
        try {
            driver.manage().deleteAllCookies();
            result = handler.getTargetWeb(driver,targetUrl);
        }catch (Exception e) {
            logger.error(String.format("get result error"),e);
        }
        driver.quit();
        return result;
    }
}

package com.devchen.proxy.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SogouV2WeixinService {

    private final static Logger logger = LoggerFactory.getLogger(SogouV2WeixinService.class);





    public List<String> targetList(String weixinId) {

        String firstUrl= String.format("https://weixin.sogou.com/weixin?type=2&ie=utf8&query=%s", weixinId);
        logger.info(String.format("visit url %s" , firstUrl));

        ChromeOptions co = new ChromeOptions();
        //co.addExtensions(new File(proxyIpEntity.getChromeProxyZip()));      //将proxy的信息添加到ChromeOptions中
        co.addArguments("--no-sandbox","--disable-dev-shm-usage");
        co.addArguments("disable-gpu");
        //co.addArguments(String.format("Referer=%s",firstUrl));

        Map<String, Object> prefs = new HashMap<String, Object>();

        prefs.put("profile.managed_default_content_settings.images",2); //禁止下载加载图片
        co.setExperimentalOption("prefs", prefs);
        System.setProperty("webdriver.chrome.driver","/root/applications/chrome-driver/2.35/chromedriver");

        //System.setProperty("webdriver.chrome.driver","/root/chromedriver/2.35/chromedriver");

        WebDriver driver = new ChromeDriver(co);
        List<String> list = new ArrayList<>();
        try {
            driver.manage().deleteAllCookies();

            driver.get(firstUrl);

            By.ByXPath toolShowBy = new By.ByXPath("//a[@uigs='select_show']");

            WebElement toolShowElement = driver.findElement(toolShowBy);
            toolShowElement.click();

            By.ByXPath BtnTimeBy = new By.ByXPath("//a[@class='btn-time']");

            List<WebElement> btnElement = driver.findElements(BtnTimeBy);

            btnElement.get(0).click();


            By.ByXPath selecTimeBy = new By.ByXPath("//a[@uigs='select_time_day']");
            WebElement selectTimeElement = driver.findElement(selecTimeBy);
            selectTimeElement.click();

            btnElement = driver.findElements(BtnTimeBy);

            btnElement.get(2).click();

            Thread.sleep(3000);


            By.ByXPath searchInputBy = new By.ByXPath("//input[@placeholder='输入公众号或微信号']");
            WebElement searchInputElement = driver.findElement(searchInputBy);
            searchInputElement.sendKeys(weixinId);

            By.ByXPath searchEnterBy = new By.ByXPath("//a[@uigs='select_search_yes']");
            WebElement searchEnterElement = driver.findElement(searchEnterBy);
            searchEnterElement.click();

            Thread.sleep(3000);

            for(int i=0;i<10;i++) {
                try {
                    By.ByXPath articleEnterBy = new By.ByXPath(String.format("//a[@uigs='article_title_%s']",String.valueOf(i)));
                    WebElement articleEnterElement = driver.findElement(articleEnterBy);
                    articleEnterElement.click();
                    Thread.sleep(3000);
                }catch (Exception e) {
                    logger.error(String.format("get article url error"),e);
                    break;
                }
            }

            String handle = driver.getWindowHandle();
            for (String handles : driver.getWindowHandles()) {
                if (handles.equals(handle))
                    continue;
                driver.switchTo().window(handles);

                list.add(driver.getCurrentUrl());
            }





        }catch (Exception e) {
            logger.error(String.format("get result error"),e);
        }
        driver.quit();

        return list;

    }
}

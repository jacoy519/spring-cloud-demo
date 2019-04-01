package com.devchen.proxy.webDriver;



import com.devchen.proxy.service.SogouWeixinService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SogouWexinHandler implements IWebDriverHandler {

    private final static Logger logger = LoggerFactory.getLogger(SogouWexinHandler.class);

    @Override
    public String getTargetWeb(WebDriver driver, String targetUrl) throws Exception {
        driver.get(targetUrl);
        By.ByXPath byXPath = new By.ByXPath("//p[@class='tit']/a");
        List<WebElement> elements = driver.findElements(byXPath);

        if(elements.isEmpty()) {
            logger.info("may meet verfiy code and quit");
            throw new Exception("not get target url");
        }
        int randMillis = Math.abs((new Random()).nextInt()%3000) + 1000;
        logger.info("wait " + randMillis);
        Thread.sleep(randMillis);

        for(WebElement element : elements) {
              element.click();
        }
        LinkedHashSet<String> windowHandleSet = (LinkedHashSet<String>)driver.getWindowHandles();
        List<String> windowHandleList = new ArrayList<>();
        for(String windowHandle : windowHandleSet) {
            windowHandleList.add(windowHandle);
        }
        driver.switchTo().window(windowHandleList.get(windowHandleList.size()-1));
        String url = driver.getCurrentUrl();
        logger.info("get target url " + url);
        return url;
    }
}

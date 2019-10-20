package com.devchen.proxy.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ZodChromeService {



    private final static String zodUserName="medivh";


    private final static  String password = "Chen5860Qi";

    private final static Logger logger = LoggerFactory.getLogger(ZodChromeService.class);

    private final static String LOG_URL = "https://www.zodgame.us/member.php?mod=logging&action=login&referer=https%3A%2F%2Fwww.zodgame.us%2Fforum.php";



    @Scheduled(fixedDelay = 4L* 3600L * 1000L)
    public void run() throws Exception{
        runWithOutLogin();
        Thread.sleep(10000L);
        runWithLogon();
    }

    public void runWithLogon() {
        ChromeOptions co = new ChromeOptions();
        //co.addExtensions(new File(proxyIpEntity.getChromeProxyZip()));      //将proxy的信息添加到ChromeOptions中
        co.addArguments("--no-sandbox","--disable-dev-shm-usage");
        co.addArguments("disable-gpu");


        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.managed_default_content_settings.images",2); //禁止下载加载图片
        co.setExperimentalOption("prefs", prefs);
        System.setProperty("webdriver.chrome.driver","/root/applications/chrome-driver/2.35/chromedriver");

        WebDriver driver = new ChromeDriver(co);
        String result =null;
        try {

            logger.info(String.format("start logon zod"));
            driver.manage().deleteAllCookies();

            //LOGIN
            logger.info(String.format("visit url %s" , LOG_URL));
            driver.get("https://www.zodgame.us/forum.php");
            Thread.sleep(10000L);

            By.ByXPath signBtn = new By.ByXPath("//a[@href='plugin.php?id=dsu_paulsign:sign']");
            WebElement signWe = driver.findElement(signBtn);
            signWe.click();
            Thread.sleep(10000L);

            By.ByXPath wlBtn = new By.ByXPath("//li[@id='wl']");
            WebElement wlWe = driver.findElement(wlBtn);
            wlWe.click();
            Thread.sleep(10000L);


            By.ByXPath wlBtn2 = new By.ByXPath("//input[@name='todaysay']");
            WebElement wlWe2 = driver.findElement(wlBtn2);
            wlWe2.sendKeys("today is good day");
            Thread.sleep(10000L);

            By.ByXPath wlBtn3 = new By.ByXPath("//div[@style='padding:20px 0;']/a");
            WebElement wlWe3 = driver.findElement(wlBtn3);
            wlWe3.click();
            Thread.sleep(10000L);
            logger.info(String.format("Logon zod success"));
        }catch (Exception e) {
            logger.error(String.format("sign zod error"),e);
        }
        driver.quit();
    }


    public void runWithOutLogin() {

        ChromeOptions co = new ChromeOptions();
        //co.addExtensions(new File(proxyIpEntity.getChromeProxyZip()));      //将proxy的信息添加到ChromeOptions中
        co.addArguments("--no-sandbox","--disable-dev-shm-usage");
        co.addArguments("disable-gpu");


        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.managed_default_content_settings.images",2); //禁止下载加载图片
        co.setExperimentalOption("prefs", prefs);
        System.setProperty("webdriver.chrome.driver","/root/applications/chrome-driver/2.35/chromedriver");

        WebDriver driver = new ChromeDriver(co);
        String result =null;
        try {

            logger.info(String.format("start logon zod"));
            driver.manage().deleteAllCookies();

            //LOGIN
            logger.info(String.format("visit url %s" , LOG_URL));
            driver.get(LOG_URL);
            Thread.sleep(10000L);
            By.ByXPath loginInputBy = new By.ByXPath("//input[@name='username']");
            WebElement loginElement = driver.findElement(loginInputBy);
            loginElement.sendKeys("medivh");
            Thread.sleep(10000L);



            By.ByXPath pwInputBy = new By.ByXPath("//input[@name='password']");
            WebElement pwInputWE = driver.findElement(pwInputBy);
            pwInputWE.sendKeys("Chen5860Qi");
            Thread.sleep(10000L);



            By.ByXPath logonBtn = new By.ByXPath("//button[@name='loginsubmit']");
            WebElement logonBtnWE = driver.findElement(logonBtn);
            logonBtnWE.click();
            Thread.sleep(60000L);

            By.ByXPath signBtn = new By.ByXPath("//a[@href='plugin.php?id=dsu_paulsign:sign']");
            WebElement signWe = driver.findElement(signBtn);
            signWe.click();
            Thread.sleep(10000L);

            By.ByXPath wlBtn = new By.ByXPath("//li[@id='wl']");
            WebElement wlWe = driver.findElement(wlBtn);
            wlWe.click();
            Thread.sleep(10000L);


            By.ByXPath wlBtn2 = new By.ByXPath("//input[@name='todaysay']");
            WebElement wlWe2 = driver.findElement(wlBtn2);
            wlWe2.sendKeys("today is good day");
            Thread.sleep(10000L);

            By.ByXPath wlBtn3 = new By.ByXPath("//div[@style='padding:20px 0;']/a");
            WebElement wlWe3 = driver.findElement(wlBtn3);
            wlWe3.click();
            Thread.sleep(10000L);
            logger.info(String.format("Logon zod success"));
        }catch (Exception e) {
            logger.error(String.format("sign zod error"),e);
        }
        driver.quit();

    }
}

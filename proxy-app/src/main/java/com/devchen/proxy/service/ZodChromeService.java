package com.devchen.proxy.service;

import com.devchen.proxy.dal.dao.ProxyConfigDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.openqa.selenium.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ZodChromeService {



    private final static String zodUserName="Z92O58D";


    private final static  String password = "cHEN5860qI";

    @Resource
    private ProxyConfigDAO proxyConfigDAO;

    private final static Logger logger = LoggerFactory.getLogger(ZodChromeService.class);

    private final static String LOG_URL = "https://www.zodgame.xyz/member.php?mod=logging&action=login&referer=https%3A%2F%2Fwww.zodgame.xyz%2Fforum.php";



    @Scheduled(fixedDelay = 4L* 3600L * 1000L)
    public void run() throws Exception{
        //runWithOutLogin();
        //Thread.sleep(10000L);
        runWithLogon();
    }

    public void runWithLogon()  {
        WebDriver driver = null;
        try {
        ChromeOptions co = new ChromeOptions();
        //co.addExtensions(new File(proxyIpEntity.getChromeProxyZip()));      //将proxy的信息添加到ChromeOptions中
        co.addArguments("--no-sandbox","--disable-dev-shm-usage");
        co.addArguments("disable-gpu");


        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.managed_default_content_settings.images",2); //禁止下载加载图片
        co.setExperimentalOption("prefs", prefs);
        System.setProperty("webdriver.chrome.driver","/root/applications/chrome-driver/2.35/chromedriver");


        driver = new ChromeDriver(co);

        String cookieStr = proxyConfigDAO.selectOne("zod_game").getKeyValue();

        List<Cookie> cookieList = new ArrayList<>();

        String[] strs = cookieStr.split("; ");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try  {
            date = format.parse("2019-12-30 12:00:00");
        } catch (Exception e) {

        }

        for(String str : strs) {
            String[] splitStr = str.split("=");
            Cookie cookie = new Cookie(splitStr[0], splitStr[1], "zodgame.xyz","/", date,true,true);
            cookieList.add(cookie);
        }

        String result =null;


            logger.info(String.format("start logon zod"));
            //driver.manage().deleteAllCookies();

            //LOGIN
            logger.info(String.format("visit url %s" , LOG_URL));
            driver.get("https://www.zodgame.xyz/forum.php");
            Thread.sleep(10000L);
            driver.manage().deleteAllCookies();

            for(Cookie cookie :cookieList) {
                 driver.manage().addCookie(cookie);
            }

            //LOGIN
            logger.info(String.format("visit url %s" , "https://zodgame.xyz/plugin.php?id=dsu_paulsign:sign"));
            driver.get("https://zodgame.xyz/plugin.php?id=dsu_paulsign:sign");
            Thread.sleep(10000L);

            String test = driver.getPageSource();

            if(test.contains("您今天已经签到过了或者签到时间还未开始")) {
                logger.info("today zod has been signed");
            } else {
                By.ByXPath wlBtn = new By.ByXPath("//li[@id='wl']");
                WebElement wlWe = driver.findElement(wlBtn);
                wlWe.click();
                Thread.sleep(10000L);

            /*
            By.ByXPath wlBtn2 = new By.ByXPath("//input[@name='todaysay']");
            WebElement wlWe2 = driver.findElement(wlBtn2);
            wlWe2.sendKeys("today is good day");
            Thread.sleep(10000L);
            */

                By.ByXPath wlBtn3 = new By.ByXPath("//div[@style='padding:20px 0;']/a");
                WebElement wlWe3 = driver.findElement(wlBtn3);
                wlWe3.click();
                Thread.sleep(10000L);
                logger.info(String.format("signed zod success"));
            }


        }catch (Exception e) {
            logger.error(String.format("sign zod error"),e);
        } finally {
            if(driver != null) {
                driver.quit();
            }
        }

    }


    public void runWithOutLogin() {

        ChromeOptions co = new ChromeOptions();
        //co.addExtensions(new File(proxyIpEntity.getChromeProxyZip()));      //将proxy的信息添加到ChromeOptions中
        co.addArguments("--no-sandbox","--disable-dev-shm-usage");
        co.addArguments("disable-gpu");


        Map<String, Object> prefs = new HashMap<String, Object>();
        //prefs.put("profile.managed_default_content_settings.images",2); //禁止下载加载图片
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

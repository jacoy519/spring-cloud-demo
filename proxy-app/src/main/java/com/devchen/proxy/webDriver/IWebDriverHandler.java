package com.devchen.proxy.webDriver;

import org.openqa.selenium.WebDriver;

public interface IWebDriverHandler {


    String getTargetWeb(WebDriver webDriver, String targetUrl) throws Exception;
}

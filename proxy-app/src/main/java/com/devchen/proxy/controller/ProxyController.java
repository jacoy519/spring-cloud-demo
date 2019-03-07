package com.devchen.proxy.controller;

import com.devchen.proxy.service.ProxyIpService;
import com.devchen.proxy.service.ProxyService;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ProxyController {

    @Resource
    private ProxyService proxyService;

    @RequestMapping(value = "/proxy", method = RequestMethod.POST)
    public void submitMagnetDownLoadTask(@RequestParam("url") String url, HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws Exception{
        proxyService.proxy(httpRequest,httpResponse,url);

    }
}

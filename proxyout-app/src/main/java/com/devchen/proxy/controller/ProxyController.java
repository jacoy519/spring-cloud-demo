package com.devchen.proxy.controller;

import com.devchen.proxy.dal.entity.WeixinPageSourceEntity;
import com.devchen.proxy.service.ProxyIpService;
import com.devchen.proxy.service.ProxyService;
import com.devchen.proxy.service.SogouWeixinService;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ProxyController {

    @Resource
    private ProxyService proxyService;

    @Resource
    private SogouWeixinService sogouWeixinService;

    @RequestMapping(value = "/proxy", method = RequestMethod.POST)
    public void proxyRequest(@RequestParam("request") String proxyRequestJson, HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws Exception{
        proxyService.proxy(httpRequest,httpResponse,proxyRequestJson);

    }

    @RequestMapping(value = "/weixin-proxy", method = RequestMethod.GET)
    @ResponseBody
    public WeixinPageSourceEntity proxyRequest(@RequestParam("id") String id) throws Exception{
        return sogouWeixinService.getWeixinPageList(id);

    }

}

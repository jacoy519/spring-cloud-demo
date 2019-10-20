package com.devchen.crawler.remote;

import com.devchen.proxy.ProxyApplication;
import com.devchen.proxy.service.SogouV2WeixinService;
import com.devchen.proxy.service.SogouWeixinService;
import com.devchen.proxy.service.ZodChromeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ProxyApplication.class)
public class ProxyTest {

    @Resource
    private SogouWeixinService sogouWeixinService;


    @Resource
    private ZodChromeService zodChromeService;

    @Resource
    private SogouV2WeixinService sogouV2WeixinService;

    @Test
    public  void test() throws Exception{
        //sogouV2WeixinService.targetList("lc_funds");
        //sogouV2WeixinService.targetList("xiaojikuaipao2014");
        zodChromeService.run();
        //sogouWeixinService.saveWeixinPageUrl("xiaojikuaipao2014", "xiaojikuaipao2014");
    }
}

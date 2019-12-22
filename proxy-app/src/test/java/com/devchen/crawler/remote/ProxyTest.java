package com.devchen.crawler.remote;

import com.devchen.proxy.ProxyApplication;
import com.devchen.proxy.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ProxyApplication.class)
public class ProxyTest {

    @Resource
    private SogouWeixinService sogouWeixinService;


    @Resource
    private ZodChromeService zodChromeService;

    @Resource
    private SogouV2WeixinService sogouV2WeixinService;

    @Resource
    private SogouV3WeixinService sogouV3WeixinService;

    @Resource
    private QbWexinSpiderService qbWexinSpiderService;

    @Test
    public  void test() throws Exception{
        //sogouV2WeixinService.targetList("lc_funds");
        //sogouV2WeixinService.targetList("xiaojikuaipao2014");
        //sogouV3WeixinService.targetList("lc_funds");
        //sogouWeixinService.saveWeixinPageUrl("xiaojikuaipao2014", "xiaojikuaipao2014");
        List<String> target = new ArrayList<>();
        target.add("lc_funds");
        zodChromeService.run();
    }
}

package com.devchen.crawler.remote;

import com.devchen.proxy.ProxyApplication;
import com.devchen.proxy.service.SogouWeixinService;
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

    @Test
    public  void test() {
        sogouWeixinService.saveWeixinPageUrl("lc_funds");
    }
}

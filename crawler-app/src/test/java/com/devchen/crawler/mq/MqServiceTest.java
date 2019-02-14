package com.devchen.crawler.mq;


import com.devchen.crawler.common.factory.HttpClientFactory;
import com.devchen.crawler.pixiv.service.PixivService;
import com.devchen.crawler.service.MqService;
import com.devchen.crawler.zimuzu.service.ZimuzuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MqServiceTest {


    @Resource
    private MqService mqService;

    @Test
    public void test() {
        mqService.sendMsgToNoticeMsgQueue("test");
    }
}

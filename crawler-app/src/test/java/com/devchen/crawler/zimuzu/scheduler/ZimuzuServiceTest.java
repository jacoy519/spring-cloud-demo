package com.devchen.crawler.zimuzu.scheduler;


import com.devchen.crawler.zimuzu.service.ZimuzuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ZimuzuServiceTest {

    @Resource
    private ZimuzuService zimuzuService;

    @Test
    public void test() {
        zimuzuService.fetchFavList();
    }
}

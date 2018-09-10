package com.devchen.crawler.getuploader;

import com.devchen.crawler.getuploader.service.GetUploadService;
import com.devchen.crawler.pixiv.service.PixivService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GetUploadServiceTest {

    @Resource
    private GetUploadService getUploadService;

    @Resource
    private PixivService pixivService;

    //@Test
    public void test() throws Exception{
        String mainTag = "cm3d2_j";
        getUploadService.fetchAllFiles();
    }

    @Test
    public void test2() {
        pixivService.visitPixiv();
    }
}

package com.devchen.spider.dal.dao;


import com.devchen.spider.dal.entity.SpiderTaskEntity;
import com.devchen.spider.service.biz.getupload.GetUploadSpiderTaskService;
import com.devchen.spider.service.biz.hexun.HexunSpiderTaskService;
import com.devchen.spider.service.biz.zimuzu.ZimuzuSpiderTaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SpiderTaskDAOTest {

    @Resource
    private SpiderTaskDAO spiderTaskDAO;

    @Resource
    private ZimuzuSpiderTaskService zimuzuSpiderTaskService;

    @Resource
    private GetUploadSpiderTaskService getUploadSpiderTaskService;

    @Resource
    private HexunSpiderTaskService hexunSpiderTaskService;

    //@Test
    public void test() {
        SpiderTaskEntity spiderTaskEntity = new SpiderTaskEntity();
        spiderTaskEntity.setTaskStatus("test_1");
        spiderTaskEntity.setTaskJob("M");
        spiderTaskEntity.setSpiderId("test_1234123");
        spiderTaskEntity.setTaskType("test_type");
        spiderTaskDAO.insertOne(spiderTaskEntity);
        spiderTaskEntity.setId(146L);
        spiderTaskEntity.setTaskStatus("test_2");
        spiderTaskEntity.setVersion(1L);
        int i= spiderTaskDAO.updateStatus(spiderTaskEntity);
        int j=0;



    }


    //@Test
    public void test2() {
        String taskType = "test_type";
        String taskJob = "M";
        List<String> taskTypeList = new ArrayList<>();
        taskTypeList.add("NEW");
        List<SpiderTaskEntity> spiderTaskEntityList = spiderTaskDAO.selectByTaskTypeAndTaskJobAndStatus(taskType, taskJob,
                taskTypeList);
        int i=0;
    }

    @Test
    public void test3() throws Exception {
        for(int i=0;i<100000;i++) {
            hexunSpiderTaskService.run();
            Thread.sleep(1000);
        }

    }
}

package com.devchen.file.dal.dao;


import com.devchen.file.dal.entity.DownloadTaskEntity;
import com.devchen.file.factory.DownloadTaskFactory;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DownloadTaskDaoTest {

    private final static Logger logger = Logger.getLogger(DownloadTaskDaoTest.class);

    @Resource
    private DownloadTaskDao downloadTaskDao;

    @Test
    public void test() {

        String address= "231512315123123123123123123123";

        String localSaveDir = "12315412312312312454123123";

        List<DownloadTaskEntity> tests = downloadTaskDao.selectTopAcceptArticle();

        for(DownloadTaskEntity test : tests) {
            downloadTaskDao.updateTaskDownloadStatusById(test.getId(), "BASD");
        }


    }
}

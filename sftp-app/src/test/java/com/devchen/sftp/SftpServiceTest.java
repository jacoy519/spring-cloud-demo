package com.devchen.sftp;

import com.devchen.sftp.controller.request.SftpDownloadRequest;
import com.devchen.sftp.controller.resp.SftpDownloadResult;
import com.devchen.sftp.service.SftpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SftpServiceTest {

    @Resource
    private SftpService sftpService;


    @Test
    public void test() throws Exception{
        SftpDownloadRequest sftpDownloadRequest = new SftpDownloadRequest();
        sftpDownloadRequest.setFileName("ubuntu_vm.part1.rar");
        sftpDownloadRequest.setRemotePath("/root/owncloud/backup/application/virtualbox_ova");
        sftpDownloadRequest.setHost("192.168.0.106");
        sftpDownloadRequest.setPort("22");
        sftpDownloadRequest.setUseName("root");
        sftpDownloadRequest.setPassword("123456");
        sftpDownloadRequest.setRequestId("1234131dasfasd");
        sftpDownloadRequest.setProxySftpMaxDownloadSec(1L);

        try {
            sftpService.submitSftpDownload(sftpDownloadRequest);
        } catch (Exception e ) {

        }



        Thread.sleep(3000000L);
    }
}

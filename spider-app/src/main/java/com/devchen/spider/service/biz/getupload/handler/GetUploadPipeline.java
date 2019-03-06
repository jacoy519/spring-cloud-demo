package com.devchen.spider.service.biz.getupload.handler;

import com.devchen.spider.common.AppProperty;
import com.devchen.spider.component.download.SocksHttpDownloader;
import com.devchen.spider.util.HttpUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

import javax.annotation.Resource;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetUploadPipeline implements Pipeline {

    private final static Logger logger= Logger.getLogger(GetUploadPipeline.class);

    @Resource
    private AppProperty appProperty;


    @Resource
    private SocksHttpDownloader socksHttpDownloader;

    @Override
    public void process(ResultItems resultItems, Task task) {
        String token = resultItems.get("token");
        String fileInfoUrl = resultItems.get("fileInfoUrl");
        String fileName = resultItems.get("fileName");
        if(StringUtils.isEmpty(token)) {
            return;
        }
        String[] splits = fileInfoUrl.split("/");
        String dir = splits[splits.length-4];
        String index = splits[splits.length-2];
        if("download".equals(index)) {
            dir = splits[splits.length-3];
            index = splits[splits.length-1];
        }

        String localFileSavePath = appProperty.getUploaderSaveDir() + "/" + dir + "/" + fileName;
        if(isFileExist(localFileSavePath)) {
            return;
        }
        String dirPath = appProperty.getUploaderSaveDir() + "/" + dir;

        if(!isFileExist(dirPath)) {
            try {
                FileUtils.forceMkdir(new File(dirPath));
            } catch (Exception e) {
                logger.error(e);
            }

        }
        String encodeFileName =  null;
        try {
            encodeFileName =URLEncoder.encode(fileName, "utf-8");
        } catch (Exception e) {
            logger.error(e);
        }

        String downLoadPath = "http://download1.getuploader.com/g/"+token+"/"+dir+"/"+index+"/"+encodeFileName;
        String bkDownLoadPath = "http://dl1.getuploader.com/g/"+token+"/"+dir+"/"+index+"/"+encodeFileName;
        List<String> fileDownloadPath = new ArrayList<>();
        fileDownloadPath.add(downLoadPath);
        fileDownloadPath.add(bkDownLoadPath);
        for(String download: fileDownloadPath) {
            socksHttpDownloader.downloadFile(localFileSavePath, download);
            if(isFileExist(localFileSavePath)) {
                logger.info(String.format("download %s in %s from %s",fileName, localFileSavePath, download));
                return;
            }
        }
    }

    private boolean isFileExist(String filePath) {
        File file =new File(filePath);
        return file.exists();
    }

    private void downloadFile(String savePath, String downloadUrl, CloseableHttpClient httpClient) {
        try {

            HttpGet request = new HttpGet(downloadUrl);
            request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
            HttpUtils.downloadFile(request,savePath, httpClient);
        }catch (Exception e) {
            logger.error(String.format("download error %s %s", savePath, downloadUrl));
        }
    }


}

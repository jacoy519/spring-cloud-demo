package com.devchen.crawler.getuploader.service;

import com.devchen.crawler.common.AppProperty;
import com.devchen.crawler.common.Constant;
import com.devchen.crawler.common.factory.HttpClientFactory;
import com.devchen.crawler.common.util.HttpUtils;
import com.devchen.crawler.other.SockTest;
import com.devchen.crawler.service.MqService;
import com.netflix.ribbon.proxy.annotation.Http;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class GetUploadService {


    private final static Logger logger = Logger.getLogger(GetUploadService.class);

    @Resource
    private AppProperty appProperty;

    @Resource
    private HttpClientFactory httpClientFactory;

    private MqService mqService;

    @Scheduled(fixedDelay = Constant.THREE_HOUR)
    public void fetchAllFiles() throws Exception{

        logger.info("start to fetch file");
        CloseableHttpClient httpClient = httpClientFactory.createSocksHttpClient();
        List<String> list = new ArrayList<>();
        list.add("tokinagare");
        list.add("melala001");
        list.add("test_20160728");
        list.add("cm3d2_l");
        list.add("cm3d2_k");
        list.add("cm3d2_j");
        list.add("cm3d2_i");
        list.add("cm3d2_h");
        list.add("cm3d2_g");
        list.add("cm3d2_f");
        list.add("cm3d2_e");
        list.add("cm3d2_d");
        list.add("cm3d2_c");
        list.add("cm3d2_b");
        list.add("cm3d2");
        for(String mainTag: list) {
            fetchFiles(mainTag, httpClient);
        }
        logger.info("end to fetch file");
        httpClient.close();
    }

    public void fetchFiles(String mainTag, CloseableHttpClient httpClient) {
        for(int i=1; i<30 ; i++) {
            String filePageUrl = String.format("http://ux.getuploader.com/%s/index/%s/date/desc", mainTag, i);
            fetchOneFilePage(filePageUrl, httpClient);
            String bkFilePageUrl = String.format("http://ux.getuploader.com/%s/index/date/desc/%s", mainTag, i);
            fetchOneFilePage(bkFilePageUrl, httpClient);
        }
    }

    private void fetchOneFilePage(String url, CloseableHttpClient httpClient) {
        try {
            logger.info(String.format("visit file list page url %s", url));
            String html = HttpUtils.getHtml(url, httpClient);
            List<FileInfo> fileInfos = getFileInfoUrlFromHtml(html);
            for(FileInfo info : fileInfos) {
                fetchFile(info, httpClient);
            }
        } catch (Exception e){
            logger.error(e);
        }
    }

    private List<FileInfo> getFileInfoUrlFromHtml(String html) {

        List<FileInfo> fileInfos = new ArrayList<>();
        Pattern fileInfoTablePattern = Pattern.compile("<tbody>([\\s\\S]*?)</tbody>");
        Matcher fileInfoTableMatcher = fileInfoTablePattern.matcher(html);
        if(fileInfoTableMatcher.find()) {
            String fileInfoTable = fileInfoTableMatcher.group();
            Pattern fileInfoPattern = Pattern.compile("<a href=\\\"(.*?)\\\" title=\\\"(.*?)\\\">");
            Matcher fileInfoMatcher = fileInfoPattern.matcher(fileInfoTable);
            while(fileInfoMatcher.find()) {
                String fileUrl = fileInfoMatcher.group(1);
                String fileName = fileInfoMatcher.group(2);
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileInfoUrl(fileUrl);
                fileInfo.setFileName(fileName);
                fileInfos.add(fileInfo);
            }
        }
        return fileInfos;
    }

    private void fetchFile(FileInfo fileInfo, CloseableHttpClient httpClient) {
        try {
            String fileInfoUrl = fileInfo.getFileInfoUrl();
            logger.info(String.format("visit file info page url %s", fileInfoUrl));
            String html = HttpUtils.getHtml(fileInfoUrl, httpClient);
            String token = getFileToken(html);
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
            String fileName = fileInfo.getFileName();
            String localFileSavePath = appProperty.getUploaderSaveDir() + "/" + dir + "/" + fileName;
            if(isFileExist(localFileSavePath)) {
                return;
            }
            String dirPath = appProperty.getUploaderSaveDir() + "/" + dir;
            if(!isFileExist(dirPath)) {
                FileUtils.forceMkdir(new File(dirPath));
            }
            String encodeFileName =  URLEncoder.encode(fileName, "utf-8");
            String downLoadPath = "http://download1.getuploader.com/g/"+token+"/"+dir+"/"+index+"/"+encodeFileName;
            String bkDownLoadPath = "http://dl1.getuploader.com/g/"+token+"/"+dir+"/"+index+"/"+encodeFileName;
            List<String> fileDownloadPath = new ArrayList<>();
            fileDownloadPath.add(downLoadPath);
            fileDownloadPath.add(bkDownLoadPath);
            for(String download: fileDownloadPath) {
                downloadFile(localFileSavePath, download, httpClient);
                if(isFileExist(localFileSavePath)) {
                    logger.info(String.format("download %s in %s from %s",fileName, localFileSavePath, download));
                    mqService.sendMsgToNoticeMsgQueue(String.format("download cm3d2 mod %s", fileName));
                    return;
                }
            }
        }catch (Exception e) {
            logger.error(e);
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

    private String getFileToken(String html) {
        Pattern tokenPattern = Pattern.compile("<input type=\"hidden\" name=\"token\" value=\"(.*)\" />");
        Matcher matcher = tokenPattern.matcher(html);
        if(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private class FileInfo {

        private String fileName;

        private String fileInfoUrl;


        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileInfoUrl() {
            return fileInfoUrl;
        }

        public void setFileInfoUrl(String fileInfoUrl) {
            this.fileInfoUrl = fileInfoUrl;
        }
    }


}

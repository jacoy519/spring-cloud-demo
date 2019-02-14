package com.devchen.crawler.zimuzu.service;

import com.devchen.crawler.common.AppProperty;
import com.devchen.crawler.common.Constant;
import com.devchen.crawler.common.factory.HttpClientFactory;
import com.devchen.crawler.common.util.HttpUtils;
import com.devchen.crawler.remote.response.UnionResponse;
import com.devchen.crawler.remote.service.FileAppRemoteService;
import com.devchen.crawler.service.MqService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ZimuzuService {


    private final static Logger logger = Logger.getLogger(ZimuzuService.class);

    @Resource
    private AppProperty appProperty;

    @Resource
    private HttpClientFactory httpClientFactory;


    @Resource
    private FileAppRemoteService fileAppRemoteService;

    @Scheduled(fixedDelay = Constant.TEN_MINUTE)
    public void fetchFavList() {
        logger.info("start fetch fav list");
        CloseableHttpClient httpClient = null;
        try {
            httpClient = httpClientFactory.createHttpClient();
            loginZimuzu(httpClient);
            List<FavInfo> list = getFavDownloadList(httpClient);
            logger.info("fetch list: " + list.size());
            for(FavInfo info : list) {
                String saveDir = appProperty.getVideSavaDir() + "/" +info.getSerialName();
                UnionResponse<String> response = fileAppRemoteService.submitMagnetDownLoadTaskWithFileName(info.getMagnetAddress(), saveDir, info.getVideoName());
                if(!isSuccessResponse(response)) {
                    logger.warn(String.format("fail to submit download with params[magnetAddress:%s,savedir:%s,videoName:%s]", info.getMagnetAddress(), saveDir, info.getVideoName()));
                }

            }
        } catch (Exception e) {
            logger.error("fetch fav list error", e);
        } finally {
            if(httpClient !=null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    logger.error(e);
                }

            }
        }
        logger.info("end fetch fav list");
    }

    private boolean isSuccessResponse(UnionResponse<String> response) {
        return "0000".equals(response.getResCode());
    }

    private void loginZimuzu(CloseableHttpClient httpClient) throws Exception{

        HttpPost loginPost = new HttpPost(appProperty.getZimuzuLoginUrl());
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("account", appProperty.getZimuzuAccount()));
        params.add(new BasicNameValuePair("password", appProperty.getZimuzuPwd()));
        params.add(new BasicNameValuePair("remember", "1"));
        params.add(new BasicNameValuePair("url_back", appProperty.getZimuzuHome()));
        loginPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = null;
        BufferedReader reader = null;
        try {
            response = httpClient.execute(loginPost);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        } finally {

            if(reader != null) {
                reader.close() ;
            }
            if(response != null) {
                response.close() ;
            }
            loginPost.releaseConnection();
        }

    }

    private List<FavInfo> getFavDownloadList(CloseableHttpClient httpClient) throws Exception {

        List<FavInfo> list = new ArrayList<>();
        String html = HttpUtils.getHtml(appProperty.getZimuzuFavUrl(), httpClient);
        Pattern updateInfoPattern = Pattern.compile("<a href=\"\\/resource\\/[0-9]*\"><img src=\".*\" \\/>[\\s\\S]*?<strong><a href=\".*\">.*<\\/a>[\\s\\S]*?<ul class=\"list\">[\\s\\S]*?<\\/ul>");
        Pattern namePattern = Pattern.compile("<strong><a href=\"\\/resource\\/[0-9]*\">(.*)<\\/a><\\/strong>");
        Pattern addressPattern = Pattern.compile("<span class=\"lk\">([\\s\\S]*?)</span>[\\s\\S]*?<a href=\"(magnet[\\s\\S]*?)\" class=\"corner\"");
        Matcher updateInfoMatcher = updateInfoPattern.matcher(html);
        while(updateInfoMatcher.find()) {
            String updateInfo = updateInfoMatcher.group();
            Matcher nameMatcher = namePattern.matcher(updateInfo);
            Matcher addressMatcher = addressPattern.matcher(updateInfo);
            String serialName = null;
            if(nameMatcher.find()) {
                serialName = nameMatcher.group(1);
                serialName = serialName.replaceAll(" ","");
            }
            String address = null;
            String videoName = null;
            while(addressMatcher.find()) {
                address = addressMatcher.group(2);
                videoName = addressMatcher.group(1);
                videoName = videoName.replaceAll(" ","");
                FavInfo info =new FavInfo();
                info.setSerialName(serialName);
                info.setMagnetAddress(address);
                info.setVideoName(videoName);
                list.add(info);
            }

        }
        return list;
    }


    private class FavInfo {

        private String serialName;

        private String videoName;

        private String magnetAddress;

        public String getSerialName() {
            return serialName;
        }

        public void setSerialName(String serialName) {
            this.serialName = serialName;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getMagnetAddress() {
            return magnetAddress;
        }

        public void setMagnetAddress(String magnetAddress) {
            this.magnetAddress = magnetAddress;
        }
    }

}

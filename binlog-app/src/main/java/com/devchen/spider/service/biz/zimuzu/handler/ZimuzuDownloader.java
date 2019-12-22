package com.devchen.spider.service.biz.zimuzu.handler;

import com.devchen.spider.common.AppProperty;
import com.devchen.spider.component.download.SocksHttpDownloader;
import com.devchen.spider.factory.HttpClientFactory;
import com.devchen.spider.util.HttpUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ZimuzuDownloader  extends AbstractDownloader {

    private CloseableHttpClient httpClient;

    private final static Logger logger = Logger.getLogger(SocksHttpDownloader.class);

    @Resource
    private HttpClientFactory httpClientFactory;

    @Resource
    private AppProperty appProperty;

    @PostConstruct
    private void init(){
        httpClient = httpClientFactory.createHttpClient();
    }


    @Override
    public Page download(Request request, Task task) {
        Page page = Page.fail();
        try {
            loginZimuzu();
            if (task == null || task.getSite() == null) {
                throw new NullPointerException("task or site can not be null");
            }
            String html = HttpUtils.getHtml(request.getUrl(), httpClient);
            page.setRawText(html);
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);
            page.setStatusCode(HttpStatus.SC_OK);
            page.setDownloadSuccess(true);
            logger.info(String.format("downloading page success for %s", request.getUrl()));
            return page;
        }catch (Exception e) {
            logger.warn(String.format("download page error for %s", request.getUrl()), e);
            onError(request);
            return page;
        }
    }

    @Override
    public void setThread(int threadNum) {

    }


    private void loginZimuzu() throws Exception{

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
}

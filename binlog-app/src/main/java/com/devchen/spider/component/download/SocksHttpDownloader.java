package com.devchen.spider.component.download;

import com.devchen.spider.factory.HttpClientFactory;
import com.devchen.spider.util.HttpUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import sun.net.www.http.HttpClient;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.selector.PlainText;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Service
public class SocksHttpDownloader extends AbstractDownloader {

    private CloseableHttpClient httpClient;

    private final static Logger logger = Logger.getLogger(SocksHttpDownloader.class);

    @Resource
    private HttpClientFactory httpClientFactory;

    @PostConstruct
    private void init(){
        httpClient = httpClientFactory.createSocksHttpClient();
    }

    @Override
    public Page download(Request request, Task task) {
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        Page page = Page.fail();
        try {
            String html = HttpUtils.getHtml(request.getUrl(), httpClient);
            page.setRawText(html);
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);
            page.setStatusCode(HttpStatus.SC_OK);
            page.setDownloadSuccess(true);
            logger.info(String.format("downloading page success for %s", request.getUrl()));
            return page;
        } catch (Exception e) {
            logger.warn(String.format("download page error for %s", request.getUrl()), e);
            onError(request);
            return page;
        }
    }

    public void downloadFile(String localPath, String downloadUrl) {
        try {
            HttpGet request = new HttpGet(downloadUrl);
            request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
            HttpUtils.downloadFile(request,localPath, httpClient);
        }catch (Exception e) {
            logger.error(String.format("download error %s %s", localPath, downloadUrl));
        }
    }

    @Override
    public void setThread(int threadNum) {

    }
}

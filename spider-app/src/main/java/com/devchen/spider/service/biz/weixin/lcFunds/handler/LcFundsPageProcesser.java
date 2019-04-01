package com.devchen.spider.service.biz.weixin.lcFunds.handler;

import com.devchen.spider.factory.HttpClientFactory;
import com.devchen.spider.util.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.net.www.http.HttpClient;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class LcFundsPageProcesser  implements PageProcessor {

    private final static Logger logger = Logger.getLogger(LcFundsPageProcesser.class);


    private Site site = Site.me().setSleepTime(5000).setTimeOut(30000)
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");

    @Resource
    private ThreadPoolTaskExecutor downloadPool;

    @Resource
    private HttpClientFactory httpClientFactory;

    @Override
    public void process(Page page) {
        Request request = page.getRequest();
        if("mainList".equals(request.getExtra("type"))) {
            handleListPage(page);
        } else if("detail".equals(request.getExtra("type"))) {
            handlePageList(page);
        } else {
            handleFindResult(page);
        }

    }


    private void handleFindResult(Page page) {
        String bodyRegex = "//p[@class='tit']/a/@href";
        Selectable body= page.getHtml().xpath(bodyRegex);
        if(!StringUtils.isEmpty(body.get())) {
            String href = body.get();
            logger.info(href);
            Request request = new Request();
            request.setUrl(href);
            request.putExtra("type", "mainList");
            page.addTargetRequest(request);
        }
    }

    private void handleListPage(Page page) {
        String href = "\"content_url\":\"([\\s\\S]*?)\"";
        Pattern pattern = Pattern.compile(href);
        Set<String> urlSet = new HashSet<String>();
        Matcher matcher = pattern.matcher(page.getRawText());
        while(matcher.find()) {
            logger.info(matcher.group(1));
            String url = String.format("http://mp.weixin.qq.com%s", matcher.group(1)).replaceAll("amp;", "");
            urlSet.add(url);
        }

        for(String url:urlSet) {
            Request request =new Request();
            request.setUrl(url);
            request.putExtra("type", "detail");
            page.addTargetRequest(request);
        }
    }

    private void handlePageList(Page page) {
        Selectable node = page.getHtml().xpath("//div[@class='rich_media_content ']");
        String html = node.get();
        Document htmlDoc = Jsoup.parse(html);
        Iterator<Element> iter = htmlDoc.select("img").iterator();

        while(iter.hasNext()) {
            Element ele =iter.next();
            String src = ele.attr("data-src");
            if(StringUtils.isEmpty(src)) {
                src = ele.attr(src);
            }

            if(!StringUtils.isEmpty(src)) {
                src = src.replace("https", "http");
                final String imageSrc=src;
                downloadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        downloadFile(imageSrc);
                    }
                });
            }
        }

    }

    private void downloadFile(String url) {
        CloseableHttpClient httpClient = null;
        OutputStream out = null;
        InputStream in = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = httpClientFactory.createHttpClient();
            HttpGet request = new HttpGet(url);
            String prefix = url.substring(url.lastIndexOf("=")+ 1);
            if(StringUtils.isEmpty(prefix)) {
                return;
            }
            String fileName = UUID.randomUUID().toString() + "." + prefix;
            String filePath = "/root/download-file/" + fileName;
            httpResponse = httpClient.execute(request);
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();
            long length = entity.getContentLength();
            if (length <= 0) {
                return;
            }
            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength=in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }
            out.flush();
        } catch (Exception e) {
           logger.error("download file error");
        } finally {
            try {
                if(out !=null ) {
                    out.close();
                }
                if(in != null) {
                    in.close();
                }
                if(httpResponse!=null) {
                    httpResponse.close();
                }
            } catch (Exception e) {
                logger.info("close file error", e);
            }

        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}

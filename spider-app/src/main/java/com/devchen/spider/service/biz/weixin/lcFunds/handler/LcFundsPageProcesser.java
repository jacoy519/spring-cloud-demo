package com.devchen.spider.service.biz.weixin.lcFunds.handler;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class LcFundsPageProcesser  implements PageProcessor {

    private final static Logger logger = Logger.getLogger(LcFundsPageProcesser.class);


    private Site site = Site.me().setSleepTime(100)
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");

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
        int i=0;
    }

    @Override
    public Site getSite() {
        return site;
    }
}

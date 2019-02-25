package com.devchen.spider.service.biz.hexun.handler;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.ListIterator;


@Service
public class HexunPageProcesser  implements PageProcessor {

    private final static Logger logger = Logger.getLogger(HexunPageProcesser.class);


    private Site site = Site.me().setSleepTime(100);

    @Override
    public void process(Page page) {
        String bodyRegex = "//div[@class='art_contextBox']";
        Selectable body= page.getHtml().xpath(bodyRegex);
        Document document = Jsoup.parse(body.get());
        ListIterator<Element> pngs = document.select("img[src]").listIterator();
        while (pngs.hasNext()) {
            Element element = pngs.next();
            element.attr("src");

            logger.info( element.attr("src"));
            element.attr("src","test");
        }
        logger.info(document.toString());
    }

    @Override
    public Site getSite() {
        return site;
    }
}

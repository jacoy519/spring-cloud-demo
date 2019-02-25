package com.devchen.spider.service.biz.zimuzu.handler;

import com.devchen.spider.service.biz.zimuzu.entity.FavInfo;
import com.devchen.spider.util.HttpUtils;
import com.sun.media.jfxmedia.logging.Logger;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ZimuzuPageProcessor implements PageProcessor {


    private Site site = Site.me().setSleepTime(100);

    @Override
    public void process(Page page) {
        List<FavInfo> list = new ArrayList<>();
        String html = page.getRawText();
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
        page.putField("favListInfo",list);
    }

    @Override
    public Site getSite() {
        return site;
    }
}

package com.devchen.spider.service.biz.getupload.handler;

import com.devchen.spider.service.biz.getupload.entity.FileInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GetUploadPageProcesser  implements PageProcessor {

    private Site site = Site.me().setSleepTime(100);

    private final static Logger logger = Logger.getLogger(GetUploadPageProcesser.class);

    @Override
    public void process(Page page) {

        String url = page.getUrl().get();
        if(url.contains("/date/desc")) {
            handleFileListInfo(page);
        } else if(url.contains("/download/")) {
            handleFileInfoPage(page);
        }


    }

    private void handleFileListInfo(Page page) {
        String html = page.getRawText();
        List<FileInfo> fileInfos = getFileInfoUrlFromHtml(html);
        for(FileInfo fileInfo: fileInfos) {
           page.addTargetRequest(fileInfo.getFileInfoUrl());
        }


        List<String> pageHrefList = page.getHtml().xpath("//ul[@class='pagination pagination-sm']/li[@class='page']/a").all();
        Pattern hrefPattern = Pattern.compile("<a href=\"([\\s\\S]*?)\">");
        for(String pageHref : pageHrefList) {
            Matcher matcher = hrefPattern.matcher(pageHref);
            if(matcher.find()) {
                page.addTargetRequest(matcher.group(1));
                logger.info(matcher.group(1));
            }
        }
        page.setSkip(true);
    }

    private void handleFileInfoPage(Page page) {
        String html = page.getRawText();
        String fileNameReg="//h1[@class='lead']/strong";
        String token = getFileToken(html);
        String fileName = page.getHtml().xpath(fileNameReg).get().
                replace("<strong>","").replace("</strong>", "");
        page.putField("fileName", fileName);
        page.putField("token",token);
        page.putField("fileInfoUrl", page.getUrl().get());
    }

    private String getFileToken(String html) {
        Pattern tokenPattern = Pattern.compile("<input type=\"hidden\" name=\"token\" value=\"(.*)\" />");
        Matcher matcher = tokenPattern.matcher(html);
        if(matcher.find()) {
            return matcher.group(1);
        }
        return null;
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







    @Override
    public Site getSite() {
        return site;
    }


}

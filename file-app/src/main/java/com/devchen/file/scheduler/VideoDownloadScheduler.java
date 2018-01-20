package com.devchen.file.scheduler;

import com.devchen.file.common.Constant;
import com.devchen.file.service.DownloadService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("videoDownloadScheduler")
public class VideoDownloadScheduler {


    @Resource
    private DownloadService downloadService;

    private final static Logger logger = Logger.getLogger(VideoDownloadScheduler.class);

    @Scheduled(fixedDelay = Constant.FIVE_MINUTE)
    public void findVideoDownload() {
        HttpClient httpclient = null;
        HttpPost loginPost = null;
        HttpGet favGet = null;

        try {
            httpclient = new DefaultHttpClient();

            String loginUrl = "http://www.zimuzu.tv/User/Login/ajaxLogin";

            loginPost = new HttpPost(loginUrl);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("account", "medivh519"));
            params.add(new BasicNameValuePair("password", "Chen5860Qi"));
            params.add(new BasicNameValuePair("remember", "1"));
            params.add(new BasicNameValuePair("url_back", "http://www.zmz2017.com/"));
            loginPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(loginPost);response.getEntity();
            loginPost.releaseConnection();
            String memberpage = "http://www.zmz2017.com/user/fav";
            favGet = new HttpGet(memberpage);
            response = httpclient.execute(favGet); // 必须是同一个HttpClient！

            String html = EntityUtils.toString(response.getEntity(), "utf-8");

            Pattern updateInfoPattern = Pattern.compile("<a href=\"\\/resource\\/[0-9]*\"><img src=\".*\" \\/>[\\s\\S]*?<strong><a href=\".*\">.*<\\/a>[\\s\\S]*?<ul class=\"list\">[\\s\\S]*?<\\/ul>");
            Pattern namePattern = Pattern.compile("<strong><a href=\"\\/resource\\/[0-9]*\">(.*)<\\/a><\\/strong>");
            Pattern addressPattern = Pattern.compile("<div class=\"links\">[\\s\\S]*?<a href=\"(.*?)\"");
            Pattern newfileNamePattern = Pattern.compile("<a href=\".*\" target=\".*\"><span class=\".*\">(.*)<\\/span>");
            Matcher updateInfoMatcher = updateInfoPattern.matcher(html);
            favGet.releaseConnection();
            while(updateInfoMatcher.find()) {
                String updateInfo = updateInfoMatcher.group();
                Matcher nameMatcher = namePattern.matcher(updateInfo);
                Matcher addressMatcher = addressPattern.matcher(updateInfo);
                Matcher newfileNameMatcher = newfileNamePattern.matcher(updateInfo);
                String videoName = null;
                String address = null;
                String newFileName = null;
                if(nameMatcher.find()) {
                    videoName = nameMatcher.group(1);
                }
                if(addressMatcher.find()) {
                    address = addressMatcher.group(1);
                }
                if(newfileNameMatcher.find()) {
                    newFileName = newfileNameMatcher.group(1);
                }
                if(!StringUtils.isEmpty(videoName) && !StringUtils.isEmpty(address) && !StringUtils.isEmpty(newFileName)) {
                    String truthDownloadDir = Constant.DOWNLOAD_VIDEO_ROOT_DIR + "/" + videoName;
                    downloadService.acceptMagnetDownloadTask(address,truthDownloadDir,newFileName);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if(favGet != null) {
                favGet.releaseConnection();
            }
            if(loginPost != null) {
                loginPost.releaseConnection();
            }
        }



    }

}

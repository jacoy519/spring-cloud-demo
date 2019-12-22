package com.devchen.proxy.service;

import com.devchen.proxy.dal.dao.WeixinPageResultDAO;
import com.devchen.proxy.dal.dao.WeixinPageSourceDAO;
import com.devchen.proxy.dal.dao.WeixinSpiderTargetDAO;
import com.devchen.proxy.dal.entity.WeixinPageResultEntity;
import com.devchen.proxy.dal.entity.WeixinPageSourceEntity;
import com.devchen.proxy.dal.entity.WeixinSpiderTargetEntity;
import com.devchen.proxy.entity.ProxyResultEntity;
import com.devchen.proxy.entity.QbResponse;
import com.devchen.proxy.entity.QbResponseEntity;
import com.google.gson.Gson;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QbWexinSpiderService {

    @Resource
    private WeixinSpiderTargetDAO weixinSpiderTargetDAO;

    @Resource
    private WeixinPageSourceDAO weixinPageSourceDAO;

    @Resource
    private WeixinPageResultDAO weixinPageResultDAO;


    @Value("${proxy.out}")
    private String proxyOutIp;

    private final static Logger logger = Logger.getLogger(QbWexinSpiderService.class);

    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36";


    @Scheduled(fixedDelay = 5L* 3600L * 1000L)
    public  void runSpider() {
        logger.info("start run weixin spider");
        List<WeixinSpiderTargetEntity> targets = weixinSpiderTargetDAO.selectAll();
        List<String> targetWeixin = new ArrayList<>();
        for(WeixinSpiderTargetEntity target : targets) {
            try {
                targetWeixin.add(target.getWeixinId());
            }catch (Exception e) {
                logger.error("error", e);
            }
        }

        try {
            spiderWeixin(targetWeixin);

        } catch (Exception e) {
            logger.error("error", e);
        }

    }



    public void spiderWeixin(List<String> weixinIdList) throws Exception{
        HttpClient client = new DefaultHttpClient();
        loginUser(client);

        for(String weixinId : weixinIdList) {
            try {
                List<String> list = vistTarget(client, weixinId);
                handleList(list, weixinId);
            } catch (Exception e) {
                logger.error(e);
            }

        }

    }


    private void handleList(List<String> targetUrl, String weixinId) {

        List<ProxyResultEntity> resultList = new ArrayList<>();

        for(String target : targetUrl) {
            ProxyResultEntity resultEntity = new ProxyResultEntity();
            target = target.replaceAll("https://mp\\.weixin\\.qq\\.com", "");
            resultEntity.setContent_url(target);
            resultList.add(resultEntity);
        }

        String gson = (new Gson()).toJson(resultList);


        WeixinPageResultEntity pageResult = weixinPageResultDAO.selectOne(weixinId);


        logger.info(String.format("%s %s",weixinId, gson));

        if(pageResult != null) {
            pageResult.setWeixinId(weixinId);
            pageResult.setPageUrl(gson);
            weixinPageResultDAO.updatePageUrl(pageResult);
        } else {
            pageResult = new WeixinPageResultEntity();
            pageResult.setWeixinId(weixinId);
            pageResult.setPageUrl(gson);
            weixinPageResultDAO.insertOne(pageResult);
        }


        WeixinPageSourceEntity sourceEntity = weixinPageSourceDAO.selectOne(weixinId);

        String askUrl = String.format("http://%s/weixin-proxy-v2?id=%s", proxyOutIp,weixinId);

        if(sourceEntity != null) {
            sourceEntity.setWeixinId(weixinId);
            sourceEntity.setPageUrl(askUrl);
            weixinPageSourceDAO.updatePageUrl(sourceEntity);
        } else {
            sourceEntity = new WeixinPageSourceEntity();
            sourceEntity.setWeixinId(weixinId);
            sourceEntity.setPageUrl(askUrl);

            weixinPageSourceDAO.insertOne(sourceEntity);
        }
    }

    private void loginUser(HttpClient client) throws Exception {
        String logUrl = "https://u.gsdata.cn/member/login?url=https%3A%2F%2Fu.gsdata.cn%2Fuser%2Findex";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "13501863410"));
        params.add(new BasicNameValuePair("password", "medivh519"));

        HttpPost loginPost = new HttpPost(logUrl);
        loginPost.setEntity(new UrlEncodedFormEntity(params));
        loginPost.setHeader("User-Agent",userAgent);
        HttpResponse response = null;
        BufferedReader reader = null;
        StringBuffer strb = new StringBuffer();
        try {
            response = client.execute(loginPost);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                strb.append(line);
            }
            int i=0;
        } finally {
            if(reader != null) {
                reader.close() ;
            }
            loginPost.releaseConnection();
        }

    }


    private List<String> vistTarget(HttpClient client,String targetWeixinId) throws Exception{
        Thread.sleep(5000L);
        String targetUrl = String.format("http://www.gsdata.cn/query/wx?q=%s", targetWeixinId);
        HttpGet loginPost = new HttpGet(targetUrl);
        loginPost.setHeader("User-Agent",userAgent);
        loginPost.setHeader("Refer","http://www.gsdata.cn");
        loginPost.setHeader("X-Requested-With","XMLHttpRequest");
        HttpResponse response = null;
        BufferedReader reader = null;
        StringBuffer strb = new StringBuffer();
        try {
            response = client.execute(loginPost);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                strb.append(line);
            }
            int i=0;
        } finally {
            if(reader != null) {
                reader.close() ;
            }
            loginPost.releaseConnection();
        }


        String html = strb.toString();
        Pattern updateInfoPattern = Pattern.compile("<a target=\"_blank\" id=\"nickname\" href=\"/rank/wxdetail[\\s\\S]*?wxname=([\\s\\S]*?)\">");
        Matcher updateInfoMatcher = updateInfoPattern.matcher(html);
        if(updateInfoMatcher.find()) {
            return spiderTarget(client,updateInfoMatcher.group(1),targetWeixinId);
        }

        return new ArrayList<>();

    }




    private List<String> spiderTarget(HttpClient client,String id, String targetWeixin) throws Exception{
        Thread.sleep(5000L);
        String logUrl = String.format("http://www.gsdata.cn/rank/toparc?wxname=%s&wx=%s&sort=-1",id, targetWeixin);

        HttpPost loginPost = new HttpPost(logUrl);
        loginPost.setHeader("User-Agent",userAgent);
        loginPost.setHeader("Refer",String.format("http://www.gsdata.cn/rank/wxdetail?wxname=%s",id));
        loginPost.setHeader("X-Requested-With","XMLHttpRequest");
        HttpResponse response = null;
        BufferedReader reader = null;
        StringBuffer strb = new StringBuffer();
        try {
            response = client.execute(loginPost);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                strb.append(line);
            }
            int i=0;
        } finally {
            if(reader != null) {
                reader.close() ;
            }
            loginPost.releaseConnection();
        }

        QbResponse qbResponse = new Gson().fromJson(strb.toString(),QbResponse.class);
        List<String> result = new ArrayList<>();
        for(QbResponseEntity urlEntity : qbResponse.getData()) {
            result.add(urlEntity.getUrl().replaceAll("http://mp.weixin.qq.com", ""));
        }
        return result;
    }
}

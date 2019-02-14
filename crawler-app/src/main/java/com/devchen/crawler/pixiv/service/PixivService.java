package com.devchen.crawler.pixiv.service;

import com.devchen.crawler.common.AppProperty;
import com.devchen.crawler.common.Constant;
import com.devchen.crawler.common.factory.HttpClientFactory;
import com.devchen.crawler.common.util.HttpUtils;
import com.devchen.crawler.getuploader.service.GetUploadService;
import com.devchen.crawler.service.MqService;
import com.google.gson.Gson;
import com.netflix.discovery.provider.Serializer;
import com.netflix.ribbon.proxy.annotation.Http;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
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
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PixivService {

    private final static Logger logger = Logger.getLogger(PixivService.class);

    @Resource
    private HttpClientFactory httpClientFactory;


    @Resource
    private AppProperty appProperty;


    @PostConstruct
    public void init() throws Exception{
        File file= new File(appProperty.getPixivSaveDir());
        if(!file.exists()) {
            FileUtils.forceMkdir(file);
        }
    }

    @Scheduled(cron="0 0 0 ? * MON")
    public  void visitPixiv() {
        CloseableHttpClient httpClient = null;
        try {
            logger.info(String.format("start visit pixiv"));
            httpClient = httpClientFactory.createSocksHttpClient();
            loginPixiv(httpClient);
            Map<String, String> result = getArtistMap(httpClient);
            for(Map.Entry<String, String> entry : result.entrySet()) {
                visitArtistWorkList(entry.getKey(), entry.getValue(), httpClient);
            }
            logger.info(String.format("finish visit pixiv"));
        } catch (Exception e) {
            logger.info("error",e);
        }
        finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("close http client error", e);
            }

        }
    }

    private void visitArtistWorkList(String id, String artist, CloseableHttpClient httpClient) throws Exception{
        String dirName = tryGetMainDirName(appProperty.getPixivSaveDir(), id);
        Map<String, Boolean> workMap = getArtistWorkMap(httpClient, id);
        String saveRootDir = null;
        if(dirName == null) {
            saveRootDir = appProperty.getPixivSaveDir() + "/" + id + "_" +  artist;
            FileUtils.forceMkdir(new File(saveRootDir));
        } else  {
            saveRootDir = appProperty.getPixivSaveDir() + "/" + dirName;
        }
        for(Map.Entry<String, Boolean> work : workMap.entrySet()) {
            String workId = work.getKey();
            if(work.getValue()) {
                handleMangaPictureWork(workId, saveRootDir, httpClient);
            } else {

                handleOnePictureWork(workId, saveRootDir, httpClient);
            }
        }

    }

    private void visitArtistWorkPage(String url, String savePath,  CloseableHttpClient httpClient) throws Exception{
        String html = HttpUtils.getHtml(url, httpClient);
        Pattern workPattern=Pattern.compile("<li class=\"image-item\">[\\s\\S]*?</a>[\\s\\S]*?href=\"/[\\s\\S]*?illust_id=([\\s\\S]*?)\"[\\s\\S]*?title=\"[\\s\\S]*?\"[\\s\\S]*?</li>");
        Matcher matcher = workPattern.matcher(html);
        while(matcher.find()) {
            try {

            } catch (Exception e) {
                logger.error(e);
            }


        }
    }



    private void handleOnePictureWork(String id,String savePath, CloseableHttpClient httpClient) throws Exception {
        String refUrl =  String.format("http://www.pixiv.net/member_illust.php?mode=medium&amp;illust_id=%s", id);
        String html = HttpUtils.getHtml(refUrl, httpClient);
        Pattern pattern = Pattern.compile("\"original\":\"([\\s\\S]*?)\"");
        Matcher matcher = pattern.matcher(html);
        if(matcher.find()) {
            String downloadUrl =matcher.group(1).replaceAll("\\\\","");
            downloadFile(downloadUrl, savePath, httpClient, refUrl);
        }



    }

    private void handleMangaPictureWork(String id, String savePath,  CloseableHttpClient httpClient) throws Exception{
        String url =  String.format("http://www.pixiv.net/member_illust.php?mode=manga&amp;illust_id=%s", id);
        String html = HttpUtils.getHtml(url, httpClient);
        Pattern pattern1 = Pattern.compile("<section class=\"manga\">([\\s\\S]*?)</section>");
        Matcher matcher1 = pattern1.matcher(html);
        if(matcher1.find()) {
            String section = matcher1.group(1);
            Pattern pattern2 = Pattern.compile("<div class=\"item-container\">[\\s\\S]*?href=\"([\\s\\S]*?)\"[\\s\\S]*?data-src=\"([\\s\\S]*?)\"[\\s\\S]*?</div>");
            Matcher matcher2 = pattern2.matcher(section);
            while(matcher2.find()) {
                String refUrl = "http://www.pixiv.net" + matcher2.group(1);
                String fileUrl = matcher2.group(2);
                downloadFile(fileUrl, savePath, httpClient, refUrl);
            }
        }
    }

    private boolean downloadFile(String downloadUrl, String savePath, CloseableHttpClient httpClient, String refUrl) {
        String[] strs = downloadUrl.split("/");
        savePath = savePath + "/" + strs[strs.length-1];
        logger.info(String.format("download %s", savePath));
        File file = new File(savePath);
        boolean downloadResult = false;
        if(file.exists()) {
            logger.info("cancel download " + savePath + ", the file has exist");
            return downloadResult;
        }
        try {
            HttpGet request = new HttpGet(downloadUrl);
            request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
            request.setHeader("Referer",refUrl);
            HttpUtils.downloadFile(request, savePath, httpClient);
            downloadResult = true;
        }catch (Exception e) {
            logger.error(String.format("download file error [savePath:%s downloadUrl:%s]", savePath, downloadUrl));
            if(file.exists()) {
                try {
                    FileUtils.forceDelete(file);
                } catch (Exception e2) {
                    logger.error(e2);
                }
            }
        }
        return downloadResult;
    }






    private String tryGetMainDirName(String root, String id) {
        File file =new File(root);
        File[] files = file.listFiles();
        if(files == null || files.length == 0) {
            return null;
        }
        for(File childFile : files) {
            String childFileId = childFile.getName().split("_")[0];
            if(childFileId.equals(id)) {
                return  childFile.getName();
            }
        }
        return null;
    }




    private void loginPixiv(CloseableHttpClient httpClient) throws Exception {

        InetSocketAddress socksaddr = new InetSocketAddress(appProperty.getProxyIp(), appProperty.getProxySocket());
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("socks.address", socksaddr);
        String accountUrl = "http://accounts.pixiv.net/login?lang=zh&source=pc&view_type=page&ref=wwwtop_accounts_index";

        String html = HttpUtils.getHtml(accountUrl, httpClient);
        Pattern postKeyPattern = Pattern.compile("\"pixivAccount.postKey\":\"([\\s\\S]*?)\"");
        Matcher postKeyMatcher = postKeyPattern.matcher(html);
        String key = "";
        if(postKeyMatcher.find()) {
            key = postKeyMatcher.group(1);
        }
        logger.info(key);
        String loginUrl = "http://accounts.pixiv.net/api/login?lang=zh";
        HttpPost loginPost = new HttpPost(loginUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("pixiv_id", "jacoy519"));
        params.add(new BasicNameValuePair("password", "medivh519"));
        params.add(new BasicNameValuePair("source", "pc"));
        params.add(new BasicNameValuePair("ref", "wwwtop_accounts_index"));
        params.add(new BasicNameValuePair("return_to", "http://www.pixiv.net/"));
        params.add(new BasicNameValuePair("post_key", key));
        loginPost.setEntity(new UrlEncodedFormEntity(params));
        loginPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
        CloseableHttpResponse response = null;
        BufferedReader reader = null;
        try {
            response = httpClient.execute(loginPost,context);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
            if(response != null) {
                response.close();
            }
            if(loginPost!=null) {
                loginPost.releaseConnection();
            }
        }
    }

    private Map<String, String> getArtistMap(CloseableHttpClient httpClient) throws Exception {
        Map<String, String> result = new HashMap<>();
        for(int i=1;i<=3;i++) {
            String favUrl = "http://www.pixiv.net/bookmark.php?type=user&rest=hide&p=" + i;
            String html2 = HttpUtils.getHtml(favUrl, httpClient);

            Pattern favPattern = Pattern.compile("data-user_id=\"([\\s\\S]*?)\"[\\s\\S]*?data-user_name=\"([\\s\\S]*?)\"");

            Matcher matcher = favPattern.matcher(html2);

            while(matcher.find()) {
                String id = matcher.group(1);
                String name = matcher.group(2);
                logger.info(String.format("find id:%s name:%s",id, name));
                result.put(id, name);
            }
        }
        return result;
    }

    private static Map<String ,Boolean> getArtistWorkMap(CloseableHttpClient httpClient,String artistId)throws Exception {
        Profile profile = getProfileBody(httpClient,artistId);
        List<String> artistWorkId = new ArrayList<>();
        artistWorkId.addAll(profile.body.illusts.keySet());
        artistWorkId.addAll(profile.body.manga.keySet());
        int batch = 40;
        int start =0;
        int end = (start + batch) <= (artistWorkId.size()) ? (start + batch) : artistWorkId.size();
        String mainUrl = String.format("http://www.pixiv.net/ajax/user/%s/profile/illusts?is_manga_top=0", artistId);
        Map<String,Boolean> result = new HashMap<>();
        while(start < artistWorkId.size()) {
            String targetUrl = mainUrl;
            for(int i=start ;i<end;i++) {
                String workId = artistWorkId.get(i);
                targetUrl = targetUrl + "&ids%5B%5D=" + workId;
            }
            WorkResponse workResponse = getWorkRespsone(httpClient,targetUrl);
            for(Map.Entry<String, workProfile> entry: workResponse.body.works.entrySet()) {
                int pageCount = entry.getValue().pageCount;
                if(pageCount >1 ) {
                    result.put(entry.getKey(), false);
                } else {
                    result.put(entry.getKey(), true);
                }
             }
             start = end;
            end = (start + batch) <= (artistWorkId.size()) ? (start + batch) : artistWorkId.size();
        }
        return result;

    }

    private static WorkResponse getWorkRespsone(CloseableHttpClient httpClient, String url) throws Exception {
        String response = HttpUtils.getHtml(url, httpClient);
        Gson gson = new Gson();
        return gson.fromJson(response, WorkResponse.class);
    }


    private static Profile getProfileBody(CloseableHttpClient httpClient,String artistId) throws Exception{
        String allProfileUrl = String.format("http://www.pixiv.net/ajax/user/%s/profile/all", artistId);
        String response = HttpUtils.getHtml(allProfileUrl,httpClient);
        Gson gson = new Gson();
        return gson.fromJson(response, Profile.class);
    }

    private class Profile {
        private  ProfileBody body = new ProfileBody();
    }

    private class ProfileBody {
        private Map<String,String> illusts = new HashMap<>();

        private Map<String,String> manga = new HashMap<>();

    }

    private class WorkResponse {
        private Work body = new Work();
    }

    private class Work {
        Map<String, workProfile> works= new HashMap<>();
    }

    private class workProfile {
        String id;
        String title;
        int pageCount;
    }


}

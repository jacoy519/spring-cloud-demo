package com.devchen.proxy.service;

import com.devchen.proxy.entity.ProxyHttpRequest;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Service
public class ProxyService {


    private final static Logger logger = LoggerFactory.getLogger(ProxyService.class);

    private PoolingHttpClientConnectionManager npccm = null;

    @Resource
    private ProxyIpService proxyIpService;

    private static String username = "jacoy519"; //用户名
    private static String password = "45cjheej"; //密码


    @PostConstruct
    public void init() {
        npccm = createNormalConnectionManager();
    }

    private PoolingHttpClientConnectionManager createNormalConnectionManager() {
        PoolingHttpClientConnectionManager npccm = new PoolingHttpClientConnectionManager();
        npccm.setMaxTotal(300); // 连接池最大并发连接数
        npccm.setDefaultMaxPerRoute(50); // 单路由最大并发数
        return npccm;
    }


    public void proxy(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String proxyJson) throws Exception{
        ProxyHttpRequest proxyHttpRequest = (new Gson()).fromJson(proxyJson, ProxyHttpRequest.class);
        String ip = "";
        String[] ipArray = ip.split(":");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(ipArray[0], Integer.valueOf(ipArray[1])),
                new UsernamePasswordCredentials(username, password));
        CloseableHttpClient httpclient = null;
        try {
            URL url = new URL(proxyHttpRequest.getUrl());
            HttpHost target = new HttpHost(url.getHost(), url.getDefaultPort(), url.getProtocol());
            HttpHost proxy = new HttpHost(ipArray[0], Integer.valueOf(ipArray[1]));

            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            HttpGet httpget = new HttpGet(url.toURI());
            httpget.setConfig(config);

            Enumeration<String> headerIter = httpServletRequest.getHeaderNames();


            for(Map.Entry<String, String> headerEntity : proxyHttpRequest.getHeaders().entrySet()) {
                httpget.addHeader(headerEntity.getKey(), headerEntity.getValue());
            }
            httpget.addHeader("Host",url.getHost());


            //set cookie
            CookieStore cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> cookieEntry : proxyHttpRequest.getCookies().entrySet()) {
                BasicClientCookie cookie1 = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie1.setDomain(url.getHost());
                cookieStore.addCookie(cookie1);
            }

            httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
                    .setDefaultCredentialsProvider(credsProvider).build();

            logger.info("Executing request " + httpget.getRequestLine() + " to " + target + " via " + proxy + " request json" +
            proxyJson);

            CloseableHttpResponse response = httpclient.execute(target, httpget);
            try {


                httpServletResponse.setStatus(response.getStatusLine().getStatusCode());
                Header[] responseHeaderArray = response.getAllHeaders();
                for(int i=0;i<responseHeaderArray.length;i++) {
                    Header header = responseHeaderArray[i];
                    if("Cache-Control".equals(header.getName())
                            || "Transfer-Encoding".equals(header.getName())
                            || "Content-Encoding".equals(header.getName())
                            || "Content-Type".equals(header.getName())
                            || "Content-Length".equals(header.getName())
                            || "Accept-Ranges".equals(header.getName())) {
                        httpServletResponse.addHeader(header.getName(), header.getValue());
                    }

                }
                response.getEntity().writeTo(httpServletResponse.getOutputStream());
                httpServletResponse.getOutputStream().flush();
            } finally {
                response.close();
            }
        } catch (Exception e) {
            logger.error("test1", e);
        } finally {
            httpclient.close();
        }


    }


}

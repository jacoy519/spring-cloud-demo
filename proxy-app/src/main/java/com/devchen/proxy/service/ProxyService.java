package com.devchen.proxy.service;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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


    public void proxy(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String proxyUrl) throws Exception{
        String ip = proxyIpService.getRandomIp();
        String[] ipArray = ip.split(":");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(ipArray[0], Integer.valueOf(ipArray[1])),
                new UsernamePasswordCredentials(username, password));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
        try {
            URL url = new URL(proxyUrl);
            HttpHost target = new HttpHost(url.getHost(), url.getDefaultPort(), url.getProtocol());
            HttpHost proxy = new HttpHost(ipArray[0], Integer.valueOf(ipArray[1]));

            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            HttpGet httpget = new HttpGet(url.toURI());
            String query = url.getQuery();
            httpget.setConfig(config);

            Enumeration<String> headerIter = httpServletRequest.getHeaderNames();

            while(headerIter.hasMoreElements()) {
                String header = headerIter.nextElement();
                if("User-Agent".equals(header)) {
                    httpget.addHeader(header, httpServletRequest.getHeader(header));
                }

            }

            logger.info("Executing request " + httpget.getRequestLine() + " to " + target + " via " + proxy);

            CloseableHttpResponse response = httpclient.execute(target, httpget);
            try {


                httpServletResponse.setStatus(response.getStatusLine().getStatusCode());
                Header[] responseHeaderArray = response.getAllHeaders();
                for(int i=0;i<responseHeaderArray.length;i++) {
                    Header header = responseHeaderArray[i];
                    if("Cache-Control".equals(header.getName())
                            || "Transfer-Encoding".equals(header.getName())
                            || "Content-Encoding".equals(header.getName())
                            || "Content-Type".equals(header.getName())) {
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

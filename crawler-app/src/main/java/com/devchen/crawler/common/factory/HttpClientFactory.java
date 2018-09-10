package com.devchen.crawler.common.factory;

import com.devchen.crawler.common.AppProperty;
import com.devchen.crawler.pixiv.service.PixivService;
import com.devchen.crawler.zimuzu.service.ZimuzuService;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;

@Component
public class HttpClientFactory {

    private final static Logger logger = Logger.getLogger(HttpClientFactory.class);

    private PoolingHttpClientConnectionManager npccm = null;

    private PoolingHttpClientConnectionManager spccm = null;

    @Resource
    private AppProperty appProperty;

    @PostConstruct
    private void init() {
        npccm = createNormalConnectionManager();
        spccm = createSocksConnectionManager();
    }

    private PoolingHttpClientConnectionManager createNormalConnectionManager() {
        PoolingHttpClientConnectionManager npccm = new PoolingHttpClientConnectionManager();
        npccm.setMaxTotal(300); // 连接池最大并发连接数
        npccm.setDefaultMaxPerRoute(50); // 单路由最大并发数
        return npccm;
    }

    private PoolingHttpClientConnectionManager createSocksConnectionManager() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new HttpSocksConnectionSocketFactory())
                .register("https", new MyConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        PoolingHttpClientConnectionManager pccm = new PoolingHttpClientConnectionManager(reg);
        pccm.setMaxTotal(300); // 连接池最大并发连接数
        pccm.setDefaultMaxPerRoute(50); // 单路由最大并发数
        return pccm;
    }

    public CloseableHttpClient createSocksHttpClient() {
        return doCreateHttpClient(spccm);
    }

    public CloseableHttpClient createHttpClient() {
        return doCreateHttpClient(npccm);
    }

    private CloseableHttpClient doCreateHttpClient(PoolingHttpClientConnectionManager pccm) {
        RequestConfig params = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(1000).setSocketTimeout(4000)
                .setExpectContinueEnabled(true).build();

        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception , int executionCount , HttpContext context) {
                // 重试1次,从1开始
                if (executionCount > 1) {
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    logger.info(
                            "[NoHttpResponseException has retry request:" + context.toString() + "][executionCount:" + executionCount + "]");
                    return true;
                }
                else if (exception instanceof SocketException) {
                    logger.info("[SocketException has retry request:" + context.toString() + "][executionCount:" + executionCount + "]");
                    return true;
                }
                return false;
            }
        };
        return   HttpClients.custom().
                setConnectionManager(pccm).
                setDefaultRequestConfig(params).
                setRetryHandler(retryHandler)
                .setConnectionManagerShared(true)
                .build();
    }



    class HttpSocksConnectionSocketFactory extends PlainConnectionSocketFactory {
        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = new InetSocketAddress(appProperty.getProxyIp(), appProperty.getProxySocket());
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }
    }

    class MyConnectionSocketFactory extends SSLConnectionSocketFactory {

        public MyConnectionSocketFactory(final SSLContext sslContext) {
            super(sslContext);
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = new InetSocketAddress(appProperty.getProxyIp(), appProperty.getProxySocket());
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

    }

}

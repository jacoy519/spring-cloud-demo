package com.devchen.spider.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpUtils {

    public static String getHtml(String url, CloseableHttpClient httpClient) throws Exception {
        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
        CloseableHttpResponse response = null;
        String html = null;
        try {
            response = httpClient.execute(request); // 必须是同一个HttpClient！
            html = EntityUtils.toString(response.getEntity(), "utf-8");
        } finally {
            request.releaseConnection();
            if(response != null) {
                response.close();
            }

        }

        return html;
    }

    public static void downloadFile(HttpGet request, String filePath, CloseableHttpClient httpClient) throws Exception{
        OutputStream out = null;
        InputStream in = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();
            long length = entity.getContentLength();
            if (length <= 0) {
                return;
            }
            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength=in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            if(out !=null ) {
                out.close();
            }
            if(in != null) {
                in.close();
            }
            if(httpResponse!=null) {
                httpResponse.close();
            }
        }

    }
}

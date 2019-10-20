package com.devchen.proxy.service;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ChromeProxyService {

    private static Logger logger = LoggerFactory.getLogger(ChromeProxyService.class);

    private static String manifestJson = "{\n" +
            "    \"version\": \"1.0.0\",\n" +
            "    \"manifest_version\": 2,\n" +
            "    \"name\": \"Chrome Proxy\",\n" +
            "    \"permissions\": [\n" +
            "        \"proxy\",\n" +
            "        \"tabs\",\n" +
            "        \"unlimitedStorage\",\n" +
            "        \"storage\",\n" +
            "        \"<all_urls>\",\n" +
            "        \"webRequest\",\n" +
            "        \"webRequestBlocking\"\n" +
            "    ],\n" +
            "    \"background\": {\n" +
            "        \"scripts\": [\"background.js\"]\n" +
            "    },\n" +
            "    \"minimum_chrome_version\":\"22.0.0\"\n" +
            "}";

    private static String backGroundProxyTemplate = "var config = {\n" +
            "        mode: \"fixed_servers\",\n" +
            "        rules: {\n" +
            "          singleProxy: {\n" +
            "            scheme: \"http\",\n" +
            "            host: \"%s\",\n" +
            "            port: %s\n" +
            "          },\n" +
            "          bypassList: [\"\"]\n" +
            "        }\n" +
            "      };\n" +
            "\n" +
            "chrome.proxy.settings.set({value: config, scope: \"regular\"}, function() {});\n" +
            "\n" +
            "function callbackFn(details) {\n" +
            "    return {\n" +
            "        authCredentials: {\n" +
            "            username: \"jacoy519\",\n" +
            "            password: \"45cjheej\"\n" +
            "        }\n" +
            "    };\n" +
            "}\n" +
            "\n" +
            "chrome.webRequest.onAuthRequired.addListener(\n" +
            "            callbackFn,\n" +
            "            {urls: [\"<all_urls>\"]},\n" +
            "            ['blocking']\n" +
            ");";


    private final static String proxyDir = "/root/chromeProxy";

    public String createProxyZip(String ip) {
        String saveDir = proxyDir+ "/" + UUID.randomUUID().toString();
        String proxyZip = saveDir + "/proxy.zip" ;

        try {
            String[] ipArray = ip.split(":");
            String address = ipArray[0];
            String host = ipArray[1];
            String backGroundProxyJs = String.format(backGroundProxyTemplate,address,host);

            FileUtils.forceMkdir(new File(saveDir));
            String manifest = saveDir + "/manifest.json";
            String backGround = saveDir + "/background.js";
            File manifestFile = writeFile(manifestJson, manifest);
            File backGroundFile =writeFile(backGroundProxyJs, backGround);
            List<File> srcFile = new ArrayList<>();
            srcFile.add(manifestFile);
            srcFile.add(backGroundFile);
            File zipFile = new File(proxyZip);
            zipFiles(srcFile,zipFile);

        } catch (Exception e) {
            logger.error("error",e);
        }

        return proxyZip;
    }


    private File writeFile(String content,String filePath) throws Exception{
        File file =new File(filePath);
        file.createNewFile();
        FileUtils.writeStringToFile(file, content);
        return file;
    }


    public  void zipFiles(List<File> srcFiles, File zipFile) {
        FileOutputStream fileOutputStream = null;// 创建 ZipOutputStream
        ZipOutputStream zipOutputStream = null;// 创建 FileInputStream 对象
        FileInputStream fileInputStream = null;
        // 判断压缩后的文件存在不，不存在则创建
        try {
            if (!zipFile.exists()) {
                zipFile.createNewFile();
            }
            fileOutputStream = new FileOutputStream(zipFile);
            //实例化 ZipOutputStream 对象
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            // 创建 ZipEntry 对象
            ZipEntry zipEntry = null;
            for (int i = 0; i < srcFiles.size(); i++) {
                fileInputStream = new FileInputStream(srcFiles.get(i));
                zipEntry = new ZipEntry(srcFiles.get(i).getName());
                zipOutputStream.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
                zipOutputStream.closeEntry();

                fileInputStream.close();

            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }

        }
    }
}

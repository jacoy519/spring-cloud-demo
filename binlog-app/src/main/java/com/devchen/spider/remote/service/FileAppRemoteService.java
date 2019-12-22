package com.devchen.spider.remote.service;

import com.devchen.spider.remote.response.UnionResponse;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class FileAppRemoteService {

    @Resource
    private RestTemplate restTemplate;

    private final static Logger logger = Logger.getLogger(FileAppRemoteService.class);

    private String MAGNET_DOWNLOAD_TASK= "http://FILE-APP/download-task-resource/submit-magnet-download-task";

    private String MAGNET_DOWNLOAD_TASK_WITH_FILE_NAME = "http://FILE-APP/download-task-resource/submit-magnet-download-task-with-file-name";

    public UnionResponse<String> submitMagnetDownLoadTaskWithFileName(String magnetUrl, String saveDir, String fileName) {

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("magentAddress", magnetUrl);
        map.add("saveDir", saveDir);
        map.add("fileName", fileName);
        HttpEntity<MultiValueMap<String,String>> httpEntity =new HttpEntity<>(map, null);

        ParameterizedTypeReference<UnionResponse<String>> typeRef = new ParameterizedTypeReference<UnionResponse<String>>() {
        };

        ResponseEntity<UnionResponse<String>> responseEntity = restTemplate.exchange(MAGNET_DOWNLOAD_TASK_WITH_FILE_NAME, HttpMethod.POST,  httpEntity, typeRef);

        return responseEntity.getBody();
    }

    public UnionResponse<String> submitMagnetDownLoadTask(String magnetUrl, String saveDir) {

        logger.info(String.format("remote get url: %s", MAGNET_DOWNLOAD_TASK_WITH_FILE_NAME));

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("magentAddress", magnetUrl);
        map.add("saveDir", saveDir);
        HttpEntity<MultiValueMap<String,String>> httpEntity =new HttpEntity<>(map, null);

        ParameterizedTypeReference<UnionResponse<String>> typeRef = new ParameterizedTypeReference<UnionResponse<String>>() {
        };


        ResponseEntity<UnionResponse<String>> responseEntity = restTemplate.exchange(MAGNET_DOWNLOAD_TASK, HttpMethod.POST,  httpEntity, typeRef);

        return responseEntity.getBody();
    }
}

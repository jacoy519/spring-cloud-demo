package com.devchen.proxy.service;

import com.devchen.proxy.entity.ProxyIpList;
import com.devchen.proxy.entity.ProxyIpResponse;
import com.devchen.proxy.entity.ProxyIpVerfyResponse;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

@Service
public class ProxyIpService {

    private final static String GET_PROXY_URL = "http://dps.kdlapi.com/api/getdps/?orderid=975188279971610&num=1&area=国内&pt=1&ut=1&dedup=1&format=json&sep=1";

    private final static String VERFY_IP_TEMP = "http://dps.kdlapi.com/api/checkdpsvalid?orderid=%s&proxy=%s&signature=%s";

    private final static long MAX_IP = 5L;

    private final static long MIN_IP = 1L;

    private final static long MAX_TIME = 30L * 60L * 1000L;

    private final static String orderId = "975188279971610";

    private final static String apiKey = "rbfo5qpe3nvaug6fk3gh00zhhwoxhlph";

    private volatile Map<String, Date> ipMap = new HashMap<String, Date>();

    private final static Logger logger = LoggerFactory.getLogger(ProxyIpService.class);

    @Resource
    private RestTemplate restTemplate;


    @Scheduled(fixedDelay = 60L * 1000L)
    public void handleIpMap() {
        Map<String, Date> currentMap = new HashMap<>();
        for(Map.Entry<String,Date> entry : ipMap.entrySet()) {
            currentMap.put(entry.getKey(), entry.getValue());
        }
        cleanMap(currentMap);
        addNewIp(currentMap);
        ipMap = currentMap;
    }

    private boolean isNeedClean(Map<String,Date> mainMap) {
        return mainMap.keySet().size() > MIN_IP;
    }

    private void cleanMap(Map<String,Date> mainMap) {
        if(!isNeedClean(mainMap)) {
            return;
        }

        StringBuffer strb = new StringBuffer();
        Iterator<String> ipSetIter = mainMap.keySet().iterator();
        while(ipSetIter.hasNext()) {
            String ip = ipSetIter.next();
            strb.append(ip);
            if(ipSetIter.hasNext()) {
                strb.append(",");
            }
        }

        String verfyApi =String.format(VERFY_IP_TEMP, orderId, strb.toString(), apiKey);
        HttpHeaders header = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(header);
        ResponseEntity<ProxyIpVerfyResponse> response  = restTemplate.exchange(verfyApi, HttpMethod.GET, entity, ProxyIpVerfyResponse.class);

        ProxyIpVerfyResponse verfyResult = response.getBody();

        for(Map.Entry<String,Boolean> entry : verfyResult.getData().entrySet()) {
            if(isNeedClean(mainMap) && entry.getValue().equals(false)) {
                logger.info(String.format("remove %s from the ip pool due to invalidate", entry.getKey()));
                mainMap.remove(entry.getKey());
            }
        }

        if(!isNeedClean(mainMap)) {
            return;
        }
        Map<String, Date> tempMap = new HashMap<>();

        for(Map.Entry<String,Date> entry : mainMap.entrySet()) {
            tempMap.put(entry.getKey(), entry.getValue());
        }
        Date date =new Date();
        for(Map.Entry<String,Date> entry : tempMap.entrySet()) {
            Date currentDate = entry.getValue();
            long currentTime = date.getTime() - currentDate.getTime();
            if(isNeedClean(mainMap) && currentTime >= MAX_TIME ) {
                logger.info(String.format("remove %s from ip pool due to out of time. currentTime %s. requestTime %s",
                        entry.getKey(),date, currentDate));
                mainMap.remove(entry.getKey());
            }
        }


    }

    private boolean needNewIp(Map<String,Date> mainMap) {
        return mainMap.keySet().size() < MAX_IP;
    }

    private void addNewIp(Map<String,Date> mainMap) {
        if(!needNewIp(mainMap)) {
            return;
        }
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type","application/json");
        HttpEntity<String> entity = new HttpEntity<>(header);

        ResponseEntity<ProxyIpResponse> response  = restTemplate.exchange(GET_PROXY_URL, HttpMethod.GET, entity, ProxyIpResponse.class);

        ProxyIpResponse proxyIpResult = response.getBody();

        for(String ip : proxyIpResult.getData().getProxy_list()) {
            logger.info(String.format("add new ip %s", ip));
            mainMap.put(ip, new Date());
        }
    }

    public String getRandomIp() {
        List<String> ipList = new ArrayList<>();
        for(String ip : ipMap.keySet()) {
            ipList.add(ip);
        }
        Random rand = new Random();
        int index = Math.abs(rand.nextInt()%ipList.size());
        return ipList.get(index);
    }


}

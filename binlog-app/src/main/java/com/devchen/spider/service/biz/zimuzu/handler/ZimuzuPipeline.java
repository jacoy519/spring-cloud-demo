package com.devchen.spider.service.biz.zimuzu.handler;

import com.devchen.spider.common.AppProperty;
import com.devchen.spider.remote.response.UnionResponse;
import com.devchen.spider.remote.service.FileAppRemoteService;
import com.devchen.spider.service.biz.zimuzu.entity.FavInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ZimuzuPipeline implements Pipeline {


    private final static Logger logger = Logger.getLogger(ZimuzuPipeline.class);


    @Resource
    private FileAppRemoteService fileAppRemoteService;

    @Resource
    private AppProperty appProperty;

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<FavInfo> list = resultItems.get("favListInfo");
        if(list == null) {
            return;
        }
        logger.info("fetch list: " + list.size());
        for(FavInfo info : list) {
            String saveDir = appProperty.getVideSavaDir() + "/" +info.getSerialName();
            UnionResponse<String> response = fileAppRemoteService.submitMagnetDownLoadTaskWithFileName(info.getMagnetAddress(), saveDir, info.getVideoName());
            if(!isSuccessResponse(response)) {
                logger.warn(String.format("fail to submit download with params[magnetAddress:%s,savedir:%s,videoName:%s]", info.getMagnetAddress(), saveDir, info.getVideoName()));
            }

        }
    }

    private boolean isSuccessResponse(UnionResponse<String> response) {
        return "0000".equals(response.getResCode());
    }

}

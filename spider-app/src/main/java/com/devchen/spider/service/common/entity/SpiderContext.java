package com.devchen.spider.service.common.entity;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

public class SpiderContext {

    private PageProcessor pageProcessor;

    private List<Pipeline> pipelineList = new ArrayList<>();

    private List<Request> startRequestList = new ArrayList<>();


    public PageProcessor getPageProcessor() {
        return pageProcessor;
    }

    public void setPageProcessor(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    public List<Pipeline> getPipelineList() {
        return pipelineList;
    }

    public void setPipelineList(List<Pipeline> pipelineList) {
        this.pipelineList = pipelineList;
    }

    public List<Request> getStartRequestList() {
        return startRequestList;
    }

    public void setStartRequestList(List<Request> startRequestList) {
        this.startRequestList = startRequestList;
    }
}

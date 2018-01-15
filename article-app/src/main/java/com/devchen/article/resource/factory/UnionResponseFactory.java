package com.devchen.article.resource.factory;

import com.devchen.article.resource.entity.UnionResponse;

public class UnionResponseFactory {

    public static UnionResponse createSuccessResponse(Object data) {
        UnionResponse unionResponse = new UnionResponse();
        unionResponse.setResCode("0000");
        unionResponse.setData(data);
        return unionResponse;
    }
}

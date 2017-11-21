package com.devchen.acount.resource.factory;

import com.devchen.acount.resource.entity.UnionResponse;

public class UnionResponseFactory {

    public static UnionResponse createSuccessResponse(Object data) {
        UnionResponse unionResponse = new UnionResponse();
        unionResponse.setResCode("0000");
        unionResponse.setData(data);
        return unionResponse;
    }
}

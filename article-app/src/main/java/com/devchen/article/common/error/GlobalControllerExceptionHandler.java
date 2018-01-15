package com.devchen.article.common.error;

import com.devchen.article.resource.entity.UnionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Medivh on 2017/6/13.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {


    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(UnionRuntimeException.class)
    @ResponseBody
    public UnionResponse handleRuntimeException(UnionRuntimeException ex) {
        UnionResponse unionResponse = new UnionResponse();
        unionResponse.setResCode(ex.getCode());
        unionResponse.setResMsg(ex.getMessage());
        return unionResponse;
    }
}


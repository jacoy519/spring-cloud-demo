package com.devchen.sftp.controller;


import com.devchen.sftp.constant.ResultCode;
import com.devchen.sftp.controller.resp.UnionResponse;
import com.devchen.sftp.excpetion.FlowException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private final static Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class) //表示让Spring捕获到所有抛出的SignException异常，并交由这个被注解的方法处理。
    @ResponseStatus(HttpStatus.OK)  //表示设置状态码。
    UnionResponse handleException(Exception e) {
        logger.error("[handleException] error happen", e);
        UnionResponse unionResponse = null;
        if(e instanceof FlowException) {
            FlowException flowException = (FlowException)e;
            unionResponse = new UnionResponse(flowException.getResultCode());
        } else {
            unionResponse = new UnionResponse(ResultCode.INNER_EXCEPTION);
        }
        return unionResponse;
    }
}

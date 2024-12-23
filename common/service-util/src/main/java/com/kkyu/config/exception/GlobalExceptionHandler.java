package com.kkyu.config.exception;

import com.kkyu.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<String> error(Exception e) {
        logger.error("全局异常处理：", e);
        return Result.error("执行了全局异常处理");
    }

    // 自定义异常
    @ExceptionHandler(DiyException.class)
    @ResponseBody
    public Result<String> error(DiyException e) {
        logger.error("自定义异常处理：", e);
        Result<String> result = Result.error("自定义异常处理");
        result.setCode(e.getCode());
        result.setMsg(e.getMsg());
        return result;
    }
}

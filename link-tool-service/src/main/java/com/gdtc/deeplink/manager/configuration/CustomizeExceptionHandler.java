package com.gdtc.deeplink.manager.configuration;

import com.gdtc.deeplink.manager.core.ResultGenerator;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.link.api.core.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CustomizeExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomizeExceptionHandler.class);

    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public Result bizExceptionHandler(ServiceException e) {
        logger.error(e.getMessage(), e);
        return ResultGenerator.genFailResult(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResultGenerator.genSystemExceptionResult();
    }
}
package com.gdtc.deeplink.manager.core;

import com.gdtc.link.api.core.Result;
import com.gdtc.link.api.core.ResultCode;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "success";
    private static final String DEFAULT_SYSTEM_EXCEPTION_MESSAGE = "system error";

    public static Result genSuccessResult() {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE);
    }

    public static <T> Result<T> genSuccessResult(T data) {
        return new Result()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE)
                .setData(data);
    }

    public static Result genFailResult(String message) {
        return new Result()
                .setCode(ResultCode.FAIL)
                .setMessage(message);
    }

    public static Result genUnauthorizedResult(String url) {
        return new Result()
                .setCode(ResultCode.UNAUTHORIZED)
                .setData(url);
    }

    public static Result genSystemExceptionResult() {
        return new Result()
                .setCode(ResultCode.INTERNAL_SERVER_ERROR)
                .setMessage(DEFAULT_SYSTEM_EXCEPTION_MESSAGE);
    }
}

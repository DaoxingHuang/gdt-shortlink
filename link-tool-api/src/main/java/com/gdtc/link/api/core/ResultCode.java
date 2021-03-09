package com.gdtc.link.api.core;

/**
 * 响应码枚举，参考HTTP状态码的语义
 */
public enum ResultCode {
    SUCCESS(200, "success"),//成功
    FAIL(400, "fail"),//失败
    UNAUTHORIZED(401, "unauthorized"),//未认证（签名错误）
    NOT_FOUND(404, "not found"),//接口不存在
    INTERNAL_SERVER_ERROR(500, "server error");//服务器内部错误

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

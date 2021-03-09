package com.gdtc.link.api.core;

/**
 * 统一API响应结果封装
 */
public class Result<T> {
    /**
     * 状态码
     * @mock 200
     */
    private int code;
    /**
     *  信息
     * @mock success
     */
    private String message;
    /**
     * 返回数据对象
     *
     */
    private T data;

    public Result setCode(ResultCode resultCode) {
        this.code = resultCode.code();
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

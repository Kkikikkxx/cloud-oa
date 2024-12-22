package com.kkyu.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 *
 * @param <T> 返回数据的类型
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg;   //信息
    private T data;       //数据

    // 成功带数据
    public static <T> Result<T> successData(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        return result;
    }

    // 成功带信息
    public static Result<String> successMsg(String msg) {
        Result<String> result = new Result<>();
        result.msg = msg;
        result.code = 1;
        return result;
    }

    // 错误返回
    public static <T> Result<T> error(String msg) {
        if (msg == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}

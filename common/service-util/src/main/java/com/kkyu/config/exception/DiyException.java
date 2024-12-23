package com.kkyu.config.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiyException extends RuntimeException{

    // 状态码
    private Integer code;

    // 描述信息
    private String msg;

    public DiyException(Integer code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }

    @Override
    public String toString() {
        return "DiyException{" +
                "code=" + code +
                ", message=" + this.getMsg() +
                '}';
    }

}

package com.ago.searchtest.sync.config;


import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class AjaxResult<T> implements Serializable {

    private static final String SUCCESS_CODE = "0000";

    private static final String ERROR_CODE = "500";

    private String code;

    private String msg;

    private T data;

    public AjaxResult() {
    }

    public AjaxResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public AjaxResult(T data) {
        this.data = data;
    }

    public AjaxResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> AjaxResult<T> ok(T data) {
        return new AjaxResult(SUCCESS_CODE,"操作成功",data);
    }

    public static <T> AjaxResult<T> failed(T data) {
        return new AjaxResult(ERROR_CODE,"操作失败",data);
    }
}
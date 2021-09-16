package com.xxx.proj.dto;

import java.io.Serializable;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月01日 18:26:00
 */
public class Result implements Serializable {
    private boolean success;
    private String message;

    public Result() {
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

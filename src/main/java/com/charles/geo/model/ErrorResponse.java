package com.charles.geo.model;

/**
 * @author huqj
 *         错误返回数据
 */
public class ErrorResponse {

    private String message;

    private Exception e;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}

package com.tauros.kaleido.core.model;

/**
 * Created by zhy on 2017/11/5.
 */
public class Result<T> {

    private T model;
    private boolean success = true;
    private String message;
    private String code;

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

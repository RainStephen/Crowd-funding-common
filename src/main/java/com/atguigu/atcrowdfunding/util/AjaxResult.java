package com.atguigu.atcrowdfunding.util;

/**
 * 处理异步请求时，封装返回页面的数据
 */
public class AjaxResult {

    private String message;
    private boolean isSuccess;
    private Object data; //其他数据

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

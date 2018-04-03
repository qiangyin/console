package com.cloudzone.exception;


/**
 * ServiceException 业务服务异常
 *
 * @author zhoufei
 * @date 2018/3/22
 */
public class ServiceException extends Exception {
    private String errorMessage;

    public ServiceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public ServiceException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage + cause.getLocalizedMessage();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}


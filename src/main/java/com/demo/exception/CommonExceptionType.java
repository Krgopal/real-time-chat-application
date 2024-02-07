package com.demo.exception;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;

public enum CommonExceptionType {
    AUTH_FAILED("1401", Status.UNAUTHORIZED, "user authorization failed"),
    TOKEN_EXPIRED("1301", Status.UNAUTHORIZED, "user token expired"),
    TOKEN_NOT_VALID("1301", Status.UNAUTHORIZED, "user token not valid"),
    USER_EXISTS("1600", Status.CONFLICT, "user exists with username"),
    GROUP_EXISTS("1500", Status.CONFLICT, "Group exists with name"),
    USER_OPERATION_NOT_ALLOWED("1700", Status.NOT_FOUND, "user operation not allowed"),
    CLIENT_REQUEST_ERROR("1701", Status.NOT_FOUND, "Client request has errors"),
    BAD_REQUEST("2300", Status.BAD_REQUEST, "Mandatory params or Data missing"),
    NOT_FOUND("2301", Status.NOT_FOUND, "Not Found"),
    INTERNAL_SERVER_ERROR("5000", Status.INTERNAL_SERVER_ERROR, "Internal server error");
    private Response.Status status;
    private ExceptionInfo exceptionInfo;

    private CommonExceptionType(String code, Response.Status status, String userMessage) {
        this.status = status;
        this.exceptionInfo = new ExceptionInfo(status.getStatusCode(), code, userMessage, (String)null);
    }

    public CustomException getException() {
        return new CustomException(this.status, this.exceptionInfo, (String)null, (Throwable)null);
    }

    public CustomException getException(Throwable a) {
        return new CustomException(this.status, this.exceptionInfo, (String)null, a);
    }

    public CustomException getException(String description) {
        return new CustomException(this.status, this.exceptionInfo, description, (Throwable)null);
    }

    public Boolean isSameException(CustomException ze) {
        return this.exceptionInfo.getCode().equals(ze.getCode());
    }
}


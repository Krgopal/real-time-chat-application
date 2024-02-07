package com.demo.exception;

import lombok.*;

import java.io.Serializable;
import javax.ws.rs.core.Response;
import javax.ws.rs.ProcessingException;


@Getter
@Setter
public class CustomException extends ProcessingException implements Serializable {
    private Response.Status status;
    private ExceptionInfo exceptionInfo;

    public CustomException() {
        this((Response.Status)null, (ExceptionInfo)null, (String)null, (Throwable)null);
    }

    public CustomException(Response.Status status, ExceptionInfo exceptionInfo, String description, Throwable a) {
        super(description, a);
        this.status = status;
        this.exceptionInfo = exceptionInfo;
        if (this.exceptionInfo != null && description != null) {
            this.exceptionInfo.setDescription(description);
        }

    }
    public String getCode() {
        return this.exceptionInfo.getCode();
    }


    public String toString() {
        return "Exception{status=" + this.status + ", ExceptionInfo=" + this.exceptionInfo + '}';
    }
}
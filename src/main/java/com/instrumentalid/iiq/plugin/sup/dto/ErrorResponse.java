package com.instrumentalid.iiq.plugin.sup.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorResponse {
    @JsonProperty
    private String message;

    @JsonProperty
    private String exceptionClass;

    @JsonProperty
    private String stackTrace;

    public ErrorResponse(Exception e) {
        this.message = e.getMessage();
        this.exceptionClass = e.getClass().getName();

        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            this.stackTrace = sw.toString();
        } catch (Exception ex) {
            this.stackTrace = "Error generating stack trace: " + ex.getMessage();
        }
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}

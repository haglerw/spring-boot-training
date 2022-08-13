package com.caehag.autocase.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Response domain
 *
 * @author Hagler Wafula
 * @version 1.0
 */
@Data
@SuperBuilder
@JsonInclude(NON_NULL)
public class Response {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Africa/Nairobi")
    protected LocalDateTime timestamp;
    protected int statusCode;
    protected HttpStatus status;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Object data;

    public Response(int statusCode, HttpStatus status, String reason, String message) {
        this.statusCode = statusCode;
        this.status = status;
        this.reason = reason;
        this.message = message;
    }
}

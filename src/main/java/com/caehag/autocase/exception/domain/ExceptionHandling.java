package com.caehag.autocase.exception.domain;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.caehag.autocase.domain.Response;
import com.caehag.autocase.exception.domain.department.DepartmentExistException;
import com.caehag.autocase.exception.domain.department.DepartmentNotFoundException;
import com.caehag.autocase.exception.domain.privilege.PrivilegeExistException;
import com.caehag.autocase.exception.domain.privilege.PrivilegeNotFoundException;
import com.caehag.autocase.exception.domain.role.RoleCodeExistException;
import com.caehag.autocase.exception.domain.role.RoleNotFoundException;
import com.caehag.autocase.exception.domain.user.EmailExistException;
import com.caehag.autocase.exception.domain.user.EmailNotFoundException;
import com.caehag.autocase.exception.domain.user.UserNotFoundException;
import com.caehag.autocase.exception.domain.user.UsernameExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandling implements ErrorController {
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a %s request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username/Password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If you think this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    public static final String NO_MAPPING = "There is no mapping for this url";

    @Value("${server.error.path}")
    private String errorPath;

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Response> accountDisabledException() {
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Response> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Response> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<Response> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RoleCodeExistException.class)
    public ResponseEntity<Response> roleCodeExistException(RoleCodeExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DepartmentExistException.class)
    public ResponseEntity<Response> departmentExistException(DepartmentExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(PrivilegeExistException.class)
    public ResponseEntity<Response> privilegeExistException(PrivilegeExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<Response> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<Response> emailNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> userNotFoundException(UserNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Response> roleNotFoundException(RoleNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<Response> departmentNotFoundException(DepartmentNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(PrivilegeNotFoundException.class)
    public ResponseEntity<Response> privilegeNotFoundException(PrivilegeNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<Response> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<Response> notAnImageFileException(NotAnImageFileException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Response> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @RequestMapping("{errorPath}")
    public ResponseEntity<Response> notFound404() {
        return createHttpResponse(NOT_FOUND, NO_MAPPING);
    }

    private ResponseEntity<Response> createHttpResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
                .body(
                        Response.builder()
                                .timestamp(now())
                                .statusCode(httpStatus.value())
                                .status(httpStatus)
                                .reason(httpStatus.getReasonPhrase())
                                .message(message)
                                .build()
                );
    }
}

package com.caehag.autocase.utility;

import com.caehag.autocase.domain.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.time.LocalDateTime.now;

/**
 * Commonly used utility methods.
 * @author Hagler Wafula
 *
 */

public class Utility {

    /**
     * Returns a random string of numeric values
     * @return String
     */
    public static String generateRandomId() {
        return RandomStringUtils.randomNumeric(10);
    }

    /**
     * Method to generate alphanumeric password
     * Returns a random string of alphanumeric values
     * @return String
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    /**
     * Method to prepare a response of HttpStatus.OK
     * Returns a ResponseEntity of type Response
     * @return ResponseEntity<Response>
     */
    public static ResponseEntity<Response> returnOkResponse(HttpStatus status, String message, Object data) {
        if (data != null) {
            return ResponseEntity.ok(
                    Response.builder()
                            .timestamp(now())
                            .data(data)
                            .message(message)
                            .status(status)
                            .statusCode(status.value())
                            .build()
            );
        } else {
            return ResponseEntity.ok(
                    Response.builder()
                            .timestamp(now())
                            .message(message)
                            .status(status)
                            .statusCode(status.value())
                            .build()
            );
        }
    }
}

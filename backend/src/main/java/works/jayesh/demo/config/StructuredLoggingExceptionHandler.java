package works.jayesh.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import works.jayesh.demo.exception.ErrorResponse;

import java.time.LocalDateTime;

/**
 * Enhanced exception handler with structured logging
 * Logs all exceptions with correlation IDs and stack traces
 */
@ControllerAdvice
public class StructuredLoggingExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(StructuredLoggingExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        // Log with structured information
        log.error("Unhandled exception occurred | Path: {} | Message: {}",
                request.getDescription(false), ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

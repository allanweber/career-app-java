package com.career.app.handler;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class ExceptionAttributes extends DefaultErrorAttributes {

    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String EXCEPTION = "exception";
    private static final String MESSAGE = "message";
    private static final String SYSTEM_ERROR = "System Error";
    private static final int STATUS_500 = 500;

    @Override
    public Map<String, Object> getErrorAttributes(final ServerRequest request, final boolean includeStackTrace) {
        final Map<String, Object> attributes = super.getErrorAttributes(request, includeStackTrace);

        final Throwable error = getError(request);
        if (error instanceof HttpClientErrorException) {
            final HttpClientErrorException exception = (HttpClientErrorException) error;
            attributes.put(STATUS, exception.getStatusCode().value());
            attributes.put(ERROR, exception.getStatusText());
        } else {
            attributes.put(STATUS, STATUS_500);
            attributes.put(ERROR, SYSTEM_ERROR);
        }

        attributes.put(EXCEPTION, error.getClass().getSimpleName());
        attributes.put(MESSAGE, error.getMessage());
        return attributes;
    }
}

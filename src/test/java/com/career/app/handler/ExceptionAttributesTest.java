package com.career.app.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionAttributesTest {

    private static final boolean INCLUDE_STACK = false;

    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String EXCEPTION = "exception";
    private static final String MESSAGE = "message";
    private static final String PATH = "path";

    private List<HttpMessageReader<?>> readers = ServerCodecConfigurer.create().getReaders();

    private MockServerHttpRequest request = MockServerHttpRequest.get("/test/1").build();

    private ExceptionAttributes exceptionAttributes = new ExceptionAttributes();

    @Test
    public void given_IllegalArgumentException_When_GetErrorAttributes_Then_Return500Attributes() {
        IllegalArgumentException exception = new IllegalArgumentException("any message");

        Map<String, Object> attributes = exceptionAttributes.getErrorAttributes(buildServerRequest(request, exception), INCLUDE_STACK);

        assertEquals(500, attributes.get(STATUS));
        assertEquals("System Error", attributes.get(ERROR));
        assertEquals("IllegalArgumentException", attributes.get(EXCEPTION));
        assertEquals("any message", attributes.get(MESSAGE));
        assertEquals("/test/1", attributes.get(PATH));
    }

    @Test
    public void given_HttpClientErrorException404_When_GetErrorAttributes_Then_Return404Attributes() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        Map<String, Object> attributes = exceptionAttributes.getErrorAttributes(buildServerRequest(request, exception), INCLUDE_STACK);

        assertEquals(404, attributes.get(STATUS));
        assertEquals("NOT_FOUND", attributes.get(ERROR));
        assertEquals("HttpClientErrorException", attributes.get(EXCEPTION));
        assertEquals("404 NOT_FOUND", attributes.get(MESSAGE));
    }

    @Test
    public void given_HttpClientErrorException404WithMessage_When_GetErrorAttributes_Then_Return404Attributes() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND, "any type of message");

        Map<String, Object> attributes = exceptionAttributes.getErrorAttributes(buildServerRequest(request, exception), INCLUDE_STACK);

        assertEquals(404, attributes.get(STATUS));
        assertEquals("any type of message", attributes.get(ERROR));
        assertEquals("HttpClientErrorException", attributes.get(EXCEPTION));
        assertEquals("404 any type of message", attributes.get(MESSAGE));
    }

    private ServerRequest buildServerRequest(MockServerHttpRequest request, Throwable error) {
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        exceptionAttributes.storeErrorInformation(error, exchange);
        return ServerRequest.create(exchange, this.readers);
    }
}
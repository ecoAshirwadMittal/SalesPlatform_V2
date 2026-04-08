package com.ecoatm.salesplatform.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidation_returns400WithFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "Email cannot be blank"));
        bindingResult.addError(new FieldError("request", "password", "Password cannot be blank"));

        MethodParameter mockParam = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mockParam, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("details");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> details = (List<Map<String, String>>) response.getBody().get("details");
        assertThat(details).hasSize(2);
        assertThat(details.get(0).get("field")).isEqualTo("email");
    }

    @Test
    void handleBadRequest_returns400() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleBadRequest(new IllegalArgumentException("Device not valid"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Device not valid");
    }

    @Test
    void handleAccessDenied_returns403() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDenied(new AccessDeniedException("nope"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().get("message")).isEqualTo("Access denied");
    }

    @Test
    void handleRuntime_notFound_returns404() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleRuntime(new RuntimeException("User not found: 42"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("User not found: 42");
    }

    @Test
    void handleRuntime_generic_returns500WithoutStackTrace() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleRuntime(new RuntimeException("something broke"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Internal server error");
        assertThat(response.getBody().toString()).doesNotContain("something broke");
    }

    @Test
    void handleGeneric_returns500() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleGeneric(new Exception("bad"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Internal server error");
    }

    @Test
    void errorBody_containsTimestamp() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleBadRequest(new IllegalArgumentException("test"));

        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsKey("status");
        assertThat(response.getBody()).containsKey("error");
    }
}

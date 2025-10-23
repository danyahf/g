package com.danya.auth.filter;

import com.danya.exception.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {
    private final ObjectMapper objectMapper;

    public void write(
            HttpServletResponse resp,
            HttpServletRequest req,
            HttpStatus status,
            String message
    ) throws IOException {

        resp.setStatus(status.value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding("UTF-8");
        var body = new ErrorDto(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI()
        );

        objectMapper.writeValue(resp.getWriter(), body);
    }
}

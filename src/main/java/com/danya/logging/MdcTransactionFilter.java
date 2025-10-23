package com.danya.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class MdcTransactionFilter extends OncePerRequestFilter {
    private static final String TX_HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        request.setAttribute("transactionId", transactionId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            response.setHeader(TX_HEADER, transactionId);
            MDC.clear();
        }
    }
}

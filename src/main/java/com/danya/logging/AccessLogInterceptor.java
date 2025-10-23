package com.danya.logging;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AccessLogInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger("ACCESS");

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        req.setAttribute("startNanos", System.nanoTime());
        MDC.put("method", req.getMethod());
        MDC.put("path", req.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest req,
            HttpServletResponse res,
            Object handler,
            Exception ex
    ) {
        long start = (long) req.getAttribute("startNanos");
        long durationMs = (System.nanoTime() - start) / 1_000_000L;

        String query = (req.getQueryString() != null) ? "?" + req.getQueryString() : "";
        String err = (ex != null) ? ex.getMessage() : "-";

        log.info("REST method={} path={}{} status={} durationMs={} error={}",
                req.getMethod(), req.getRequestURI(), query, res.getStatus(), durationMs, err);

        MDC.remove("method");
        MDC.remove("path");
    }
}

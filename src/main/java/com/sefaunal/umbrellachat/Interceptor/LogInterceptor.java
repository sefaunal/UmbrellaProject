package com.sefaunal.umbrellachat.Interceptor;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

/**
 * @author github.com/sefaunal
 * @since 2023-11-30
 */

public class LogInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        // Log user details if logged in
        logUserDetails();

        // Log requested method type
        String method = request.getMethod();
        LOG.info("Requested Method Type: " + method);

        // Log requested URI
        String uri = request.getRequestURI();
        LOG.info("Requested URI: " + uri);

        // Log user's environment
        String userAgent = request.getHeader("User-Agent");
        LOG.info("User's Environment: " + userAgent);

        // Log user's IP address
        extractIpAddress(request);

        // Log request parameters
        extractBody(request);

        return true;
    }

    private void logUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            LOG.info("User: " + authentication.getName());
        }
    }

    private void extractIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        LOG.info("User's IP Address: " + ipAddress);
    }

    private void extractBody(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!paramName.equalsIgnoreCase("password")) {
                String paramValue = request.getParameter(paramName);
                LOG.info("Parameter: " + paramName + " = " + paramValue);
            }
        }
    }
}

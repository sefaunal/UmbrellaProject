package com.sefaunal.umbrellachat.Interceptor;

import com.sefaunal.umbrellachat.Util.CommonUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author github.com/sefaunal
 * @since 2023-11-30
 */

public class LogInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        // Log user's mail address if logged in
        LOG.info("User: {}", CommonUtils.getUserInfo());

        // Log requested method type
        String method = request.getMethod();
        LOG.info("Requested Method Type: " + method);

        // Log requested URI
        String uri = request.getRequestURI();
        LOG.info("Requested URI: " + uri);

        // Log user's environment
        LOG.info("User's Environment: " + CommonUtils.getUserEnvironment(request));

        // Log user's IP address
        LOG.info("User's IP Address: {}", CommonUtils.getIpAddress(request));

        // Log request parameters
        LOG.info("Params: {}", CommonUtils.extractRequestBody(request));

        return true;
    }
}

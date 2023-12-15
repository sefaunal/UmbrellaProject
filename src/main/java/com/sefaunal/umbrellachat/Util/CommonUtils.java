package com.sefaunal.umbrellachat.Util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-12-07
 */
public class CommonUtils {
    public static String extractRequestBody(HttpServletRequest request) {
        List<String> messageList = new ArrayList<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!paramName.equalsIgnoreCase("password")) {
                String paramValue = request.getParameter(paramName);
                messageList.add("Parameter: " + paramName + " = " + paramValue);
            }
        }
        return messageList.toString();
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static String getUserEnvironment(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getUserInfo() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }
}

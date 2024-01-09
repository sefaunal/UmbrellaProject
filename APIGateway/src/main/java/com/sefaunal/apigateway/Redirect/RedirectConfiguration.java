package com.sefaunal.apigateway.Redirect;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author github.com/sefaunal
 * @since 2024-01-08
 */
@Configuration
public class RedirectConfiguration {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://UmbrellaAuth"))
                .route("blog-service", r -> r.path("/api/blog/**")
                        .uri("lb://UmbrellaBlog"))
                .route("user-service", r -> r.path("/api/user/**")
                        .uri("lb://UmbrellaUser"))
                .build();
    }
}

package com.sefaunal.umbrellablog.Controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author github.com/sefaunal
 * @since 2024-01-09
 */
@FeignClient(name = "UmbrellaAuth")
public interface DemoFeign {

    @GetMapping("/api/auth/info")
    String getFromSec();
}

package com.sefaunal.umbrellablog.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author github.com/sefaunal
 * @since 2024-01-08
 */
@RestController
public class DemoController {
    private final DemoFeign demoFeign;

    public DemoController(DemoFeign demoFeign) {
        this.demoFeign = demoFeign;
    }

    @GetMapping("/api/blog/info")
    public String hello() {
        return demoFeign.getFromSec();
    }

}

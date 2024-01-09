package com.sefaunal.umbrellasecurity.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {
    @GetMapping("/home")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}

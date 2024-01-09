package com.sefaunal.umbrellaauth.Controller;

import com.sefaunal.umbrellaauth.Model.LoginHistory;
import com.sefaunal.umbrellaauth.Service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author github.com/sefaunal
 * @since 2023-12-04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/loginHistory")
public class LoginHistoryController {
    private final LoginHistoryService loginHistoryService;

    @GetMapping("/fetch")
    public ResponseEntity<Page<LoginHistory>> fetchLoginHistory(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "25", required = false) int size) {
        // Set maximum limit for size
        int maxSize = Math.min(size, 100);

        // Create the Pageable object with the specified page, size, and sorting options
        Pageable pageable = PageRequest.of(page, maxSize);

        return ResponseEntity.ok().body(loginHistoryService.getLoginHistory(pageable));
    }
}

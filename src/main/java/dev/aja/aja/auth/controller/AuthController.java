package dev.aja.aja.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.aja.aja.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> isServerAlive() {
        return ResponseEntity.ok(Map.of("success", true, "message", "El servidor está activo"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password,
            HttpServletRequest request) {

        authService.login(username, password, request);

        return ResponseEntity.ok(Map.of("success", true, "message", "Has realizado petición a login"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {

        authService.logout();

        return ResponseEntity.ok(Map.of("success", true, "message", "Has realizado petición de logout"));
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {

        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());

        return ResponseEntity.ok(Map.of("success", true, "message", "HAS ACCEDIDO A TEST AUTHENTICADO"));
    }

}

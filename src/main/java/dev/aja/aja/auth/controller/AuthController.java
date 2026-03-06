package dev.aja.aja.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login() {

        System.out.println("LOGIN");

        return ResponseEntity.ok(Map.of("success", true, "message", "Has realizado petición a login"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {

        System.out.println("LOGOUT");

        return ResponseEntity.ok(Map.of("success", true, "message", "Has realizado petición de logout"));
    }

}

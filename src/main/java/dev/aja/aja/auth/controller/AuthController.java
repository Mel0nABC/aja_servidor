package dev.aja.aja.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password,
            HttpServletRequest request) {

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("La contraseña o el usuario es incorrecto");
        }

        // AÑADIR USUARIO CORRECTO AL CONTEXTO

        // ChatGPT Prompt : si he comprobado que usuario existe y la contraseña es
        // ok, cómo añado ese usuario al contexto?

        // userDetails ya fue verificado
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, // principal
                null, // credenciales (ya verificadas)
                userDetails.getAuthorities() // roles
        );

        // Guardar en SecurityContext del hilo actual
        SecurityContextHolder.getContext().setAuthentication(authToken);

        HttpSession session = request.getSession(true);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(Map.of("success", true, "message", "Has realizado petición a login"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {

        System.out.println("LOGOUT");

        SecurityContextHolder.getContext().setAuthentication(null);

        return ResponseEntity.ok(Map.of("success", true, "message", "Has realizado petición de logout"));
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {

        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());

        return ResponseEntity.ok(Map.of("success", true, "message", "HAS ACCEDIDO A TEST AUTHENTICADO"));
    }

}

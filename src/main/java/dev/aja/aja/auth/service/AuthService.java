package dev.aja.aja.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public void login(String username, String password, HttpServletRequest request) {
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
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        System.out.println("LOGOUT");
    }

}

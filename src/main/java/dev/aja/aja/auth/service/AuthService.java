package dev.aja.aja.auth.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import dev.aja.aja.auth.entity.UserEntity;
import dev.aja.aja.auth.repository.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    private final UserEntityRepository userEntityRepository;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserEntityRepository userEntityRepository, AuthenticationManager authenticationManager) {
        this.userEntityRepository = userEntityRepository;
        this.authenticationManager = authenticationManager;
    }

    public UserEntity login(String username, String password, HttpServletRequest request) {

        System.out.println("PETICIÓN DE LOGIN DEL USUARIO:");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // Identificamos el usuario
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Guardar en SecurityContext del hilo actual
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Se añade el contexto a la sessión http
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return getUserEntityFromActualUserContext();
    }

    public boolean logout() {
        SecurityContextHolder.getContext().setAuthentication(null);

        if (SecurityContextHolder.getContext().getAuthentication() != null)
            return false;

        System.out.println("LOGOUT");

        return true;
    }

    public UserEntity getUserEntityFromActualUserContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userEntityRepository.findByUsername(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found: " + username);

        return userOptional.get();
    }

}

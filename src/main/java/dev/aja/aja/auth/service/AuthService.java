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

/**
 * Clase que se declara como servicio para la carga durante el inicio de Spring
 * Boot. Dispondremos de todas las funciones para acceder a la información del
 * usuario en la base de datos y realizar acciones sobre el contexto de la
 * sesión actual
 */
@Service
public class AuthService {

    private final UserEntityRepository userEntityRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param userEntityRepository  inyección par el acceso a la base de datos de la
     *                              entidad UserEntity
     * @param authenticationManager inyección para authenticar el usuario
     */
    public AuthService(UserEntityRepository userEntityRepository, AuthenticationManager authenticationManager) {
        this.userEntityRepository = userEntityRepository;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Gestión del login, se comprueba que el usuario existe y se ha proporcionado
     * la contraseña correcta. Si es todo correcto se añade al contexto de la sesión
     * actual y a la sesión HTTP
     * 
     * @param username nombre del usuario que quiere hacer login
     * @param password contraseña del usuario que quiere hacer login
     * @param request  request con la información de la petición HTTP actual,
     *                 headers, body, etcétera.
     * 
     * @return devuelve una entidad UserEntity con toda la información del usuario
     *         que acaba de iniciar sessión
     */
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

    /**
     * Eliminamos el contexto de la sesión actual y permitimos el logout del usuario
     * 
     * @return true si todo ha salido bien, false si algo ha ocurrido
     */
    public boolean logout() {
        SecurityContextHolder.getContext().setAuthentication(null);

        if (SecurityContextHolder.getContext().getAuthentication() != null)
            return false;

        System.out.println("LOGOUT");

        return true;
    }

    /**
     * obtenemos toda la información de la entidad del tipo UserEntity del usuario
     * que está con sesión iniciada. Obtenemos el username del contexto actual
     * 
     * @return entidad UserEntity con la información del usuario del contexto
     */
    public UserEntity getUserEntityFromActualUserContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userEntityRepository.findByUsername(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found: " + username);

        return userOptional.get();
    }

}

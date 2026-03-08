package dev.aja.aja.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Clase que se declara como servicio para la carga durante el inicio de Spring
 * Boot. Dispondremos de todas las funciones para realizar la authentificación
 * de los usuarios
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    /**
     * 
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param authenticationManager inyección para authenticar el usuario
     * @param userService           inyección para servicio de usuario, obtenemos
     *                              acceso a la lígica referente a usuarios
     */
    public AuthService(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
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

        // Identificamos el usuario
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Guardar en SecurityContext del hilo actual
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Se añade el contexto a la sessión http
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return userService.getUserEntityFromActualUserContext();
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

        return true;
    }
}

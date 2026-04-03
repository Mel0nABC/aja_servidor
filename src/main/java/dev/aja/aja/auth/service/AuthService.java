package dev.aja.aja.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;
import dev.aja.aja.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase que se declara como servicio para la carga durante el inicio de Spring
 * Boot. Dispondremos de todas las funciones para realizar la authentificación
 * de los usuarios
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    /**
     * Constante para indicar el nombre de la cookie que se usará para el JWT TOKEN
     */
    public static final String JWT_TOKEN_COOKIE_NAME = "JWT_TOKEN";
    private final int UNIT_EXPIRATION_TOKEN = 3;

    /**
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param authenticationManager inyección para authenticar el usuario
     * @param userService           inyección para servicio de usuario, obtenemos
     *                              acceso a la lígica referente a usuarios
     * @param jwtEncoder            Bean de instancia JwtEncoder para poder crear el
     *                              JWT_TOKEN
     */
    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtEncoder jwtEncoder,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    /**
     * Gestión del login, se comprueba que el usuario existe y se ha proporcionado
     * la contraseña correcta. Si es todo correcto se crear la cookie con el JWT y
     * se añade al contexto de la sesión actual y a la sesión HTTP
     * 
     * @param username nombre del usuario que quiere hacer login
     * @param password contraseña del usuario que quiere hacer login
     * @param request  request con la información de la petición HTTP actual,
     *                 headers, body, etcétera.
     * @param response objeto con el que se creará la respuesta al cliente
     * 
     * @return devuelve una entidad UserEntity con toda la información del usuario
     *         que acaba de iniciar sessión
     */
    public UserEntity login(String username, String password, HttpServletRequest request,
            HttpServletResponse response) {

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isEmpty()) {
            if (!userOptional.get().getIsActive()) {
                throw new DisabledException("Tu usuario está deshabilitado consulta por mail con un admin");
            }
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        if (auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal())) {

            String token = makeJwt(username);
            Cookie cookie = new Cookie(JWT_TOKEN_COOKIE_NAME, token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60 * 3); // Para que expire en 3 días.
            response.addCookie(cookie);
        }

        SecurityContextHolder.getContext().setAuthentication(auth);

        return userService.getUserEntityFromActualUserContext();
    }

    /**
     * 
     * Creamos un empaquetado de datos (claims, clave:valor) con información del
     * usuario, para luego, codificarlo como JWT
     * 
     * @param username nombre del usuario de la sesión
     * @return JWT con empaquetado de datos del usuario
     */
    public String makeJwt(String username) {

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("Nombre de usuario o contraseña incorrectos");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(UNIT_EXPIRATION_TOKEN, ChronoUnit.DAYS))
                .claim("roles", userOptional.get().getRole())
                .claim("jti", UUID.randomUUID().toString()) // <-- UUID único para más seguridad
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    /**
     * Eliminamos el contexto de la sesión actual y permitimos el logout del usuario
     * 
     * @param response objeto con el que se creará la respuesta al cliente
     * @return true si todo ha salido bien, false si algo ha ocurrido
     */
    public boolean logout(HttpServletResponse response) {

        Cookie cookie = new Cookie(JWT_TOKEN_COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        SecurityContextHolder.getContext().setAuthentication(null);

        if (SecurityContextHolder.getContext().getAuthentication() != null)
            return false;

        return true;
    }
}

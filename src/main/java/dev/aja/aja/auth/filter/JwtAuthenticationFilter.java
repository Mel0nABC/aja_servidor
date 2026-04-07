package dev.aja.aja.auth.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.aja.aja.auth.service.AuthService;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase que actua como filtro, para poderlo usar en el securityfilterchain,
 * antes que el propio authentication manager. Con esto conseguimos, que si el
 * usuario dispone de JWT, no hace falta que se authentifique
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    /**
     * Constructor del filtro para comprobar si hay Jwt Token o no cuando se realiza
     * una conexión
     * 
     * @param jwtDecoder     inyección de JwtDecoder para decodificar posible token
     *                       de
     *                       entrada
     * @param userRepository repositorio para realizar CRUD a la base de datos y la
     *                       tabla usuarios
     */
    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    /***
     * Se sobre escribe el método para ajustarlo a las necesidades. En este método
     * comprobamos si en el request viene la cookie con el nombre en
     * AuthService.JWT_TOIKEN_COOKIE_NAME, si viene, guardamos su valor en la
     * variable jwtCookie.
     * Cuando esta cookie existe, realizamos los siguientes procesos:
     * - Decodificamos la cookie y obtenemos un objeto del tipo Jwt.
     * - Obtenemos el username que viene en el jwt.
     * - Obtenemos los roles.
     * - Creamos una colección de GrantedAuthority, en este caso para los roles del
     * usuario y se le asignan todos los que tenga.
     * - Creamos el Authentication para poderselo añadir al contexto
     * - Añadimos el Authentication al contexto
     * - Buscamos el usuario con el username que venía en el JWT.
     * - Siexiste el usuario, comprobamos si este está activo o no.
     * - Si no está activo, enviamos una respuesta con response indicando qué
     * acciones realizar.
     * - Si está activo, su petición pasará por /api/auth/login y si la
     * authentificación es correcta, le generará una nueva cookie con el JWT.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie jwtCookie = null;
        Cookie[] cookieList = request.getCookies();

        if (cookieList != null)
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals(AuthService.JWT_TOKEN_COOKIE_NAME)) {
                    jwtCookie = cookie;
                    break;
                }
            }

        if (jwtCookie != null) {

            Jwt jwt = jwtDecoder.decode(jwtCookie.getValue());

            String username = jwt.getSubject();

            List<String> roles = jwt.getClaimAsStringList("roles");

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

            Optional<UserEntity> userOptional = userRepository.findByUsername(username);

            if (!userOptional.isEmpty()) {
                if (!userOptional.get().getIsActive()) {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    response.setContentType("application/json");

                    response.getWriter().write("""
                                {
                                    "success": "false",
                                    "message": "Tu usuario está deshabilitado consulta por mail con un admin"
                                }
                            """);
                    return;
                }
            }

        }

        filterChain.doFilter(request, response);
    }

}
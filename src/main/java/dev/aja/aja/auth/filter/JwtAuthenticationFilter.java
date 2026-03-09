package dev.aja.aja.auth.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /***
     * Se sobre escribe el método para ajustarlo a las necesidades
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
        }

        filterChain.doFilter(request, response);
    }

}

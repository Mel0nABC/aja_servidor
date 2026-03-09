package dev.aja.aja.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import dev.aja.aja.auth.filter.JwtAuthenticationFilter;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.repository.UserEntityRepository;

/**
 * Clase donde se va a especificar toda la confiuración relativa a seguridad de
 * la aplicación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Método que se genera como Bean para tenerlo en el contexto de la aplicación,
     * se encarga del filtrado de las entradas a nuestros end points. Aplica medidas
     * de seguridad, como deshabilitar csrf
     * como no necesitamos un formulario de login y logout, están deshabilitados
     * también.
     * 
     * @param http, inyección de HttpSecurity para poder configurar el
     *              SecurityFilterChain
     * @return devuelve HttpSecurity para que spring lo obtenga
     * @throws Exception delegamos cualquier excepción que pudiera producirse a
     *                   quien llama este método.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
        return http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .csrf((crfs) -> crfs.disable())
                .formLogin((form) -> form.disable())
                .logout((logout) -> logout.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/auth/health", "/api/auth/login").permitAll()
                        .requestMatchers("/api/user/**").hasRole(RoleEnum.ADMIN.getName())
                        .anyRequest().authenticated())
                .build();
    }

    /**
     * Codigicador para poder generar el JWT
     * 
     * @param jwkSource
     * @return
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * 
     * Mediante la clave privada y la publica, creamos par de claves
     * 
     * @param privateKey clave private
     * @param publicKey  clave pública
     * @return par de claves
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * Para poder obtener la información de nuestro JWT
     * 
     * @param publicKey clave privada para decodificar
     * @return Objeto JwtDecoder con toda la información decodificada
     */
    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * Obtenemos la clave privada generada en loca, filtramos limpiando texto que no
     * es necesario
     * 
     * @return
     * @throws Exception
     */
    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        String privateKeyContent = Files.readString(Paths.get("clave_privada.pem"))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    /**
     * Obtenemos la clave pública generada en loca, filtramos limpiando texto que no
     * es necesario
     * 
     * @return
     * @throws Exception
     */
    @Bean
    public RSAPublicKey publicKey() throws Exception {
        String key = Files.readString(Paths.get("clave_publica.pem"));
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /**
     * Bean para authenticar al usuario y obtenemos la información para añadir al
     * contexto y a la sesión.
     * 
     * @param userDetailsService, información del usuario para authenticarlo
     * @param passwordEncoder,    sistema de codificación de contraseña implementado
     * @return información de authenticación del usuario
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    /**
     * Se sobre escribe UserDetailsService, que es el usuario que se envia al
     * AuthenticationManager
     * así, lo obtenemos de la bbdd.
     * 
     * @param userEntityRepository, repositorio par acceder a los datos de
     *                              UserEntity.
     * 
     * @return información del usuario que está haciendo login
     */
    @Bean
    public UserDetailsService userDetailsService(UserEntityRepository userEntityRepository) {
        return username -> userEntityRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    }

    /**
     * Implementación del sistema de codificación de contraseñas.
     * 
     * @return objeto BCryptPasswordEncoder para coficiar contraseñas
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Método que se ejecuta cuando spring ha iniciado el contexto de la aplicación.
     * En este caso se usa para añadir dos usuarios de pruebas
     * 
     * @param userEntityRepository repoitorio para acceder a la base de datos de
     *                             entidad UserEntity
     * @return
     */
    // @Bean
    // CommandLineRunner runner(UserEntityRepository userEntityRepository) {
    // // https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html
    // return args -> {
    // UserEntity admin = UserEntity.builder()
    // .username("admin")
    // .password(passwordEncoder().encode("1234"))
    // .email("admin@aja.dev")
    // .role(RoleEnum.ADMIN.getName())
    // .build();

    // UserEntity user = UserEntity.builder()
    // .username("user")
    // .password(passwordEncoder().encode("1234"))
    // .email("user@aja.dev")
    // .role(RoleEnum.USER.getName())
    // .build();

    // userEntityRepository.saveAll(List.of(admin, user));
    // };
    // }
}

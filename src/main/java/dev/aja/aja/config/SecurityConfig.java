package dev.aja.aja.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import dev.aja.aja.directmessage.repository.DirectMessageRepository;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.post.repository.PostRepository;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase donde se va a especificar toda la confiuración relativa a seguridad de
 * la aplicación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        /**
         * Constructor creado para ignorar warnings cuando se crea javadoc
         */
        public SecurityConfig() {
        }

        /**
         * Método que se genera como Bean para tenerlo en el contexto de la aplicación,
         * se encarga del filtrado de las entradas a nuestros end points. Aplica medidas
         * de seguridad, como deshabilitar csrf
         * como no necesitamos un formulario de login y logout, están deshabilitados
         * también.
         * 
         * @param http                    inyección de HttpSecurity para poder
         *                                configurar el
         *                                SecurityFilterChain
         * @param jwtAuthenticationFilter filtro para generar authenticación si existe
         *                                un JWT_TOKEN válido
         * @return devuelve HttpSecurity para que spring lo obtenga
         * @throws Exception delegamos cualquier excepción que pudiera producirse a
         *                   quien llama este método.
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        JwtAuthenticationFilter jwtAuthenticationFilter)
                        throws Exception {
                // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
                return http
                                /**
                                 * Fuente ChatGPT y editado por mi.
                                 * 
                                 * Prompt: esta excepciópn, AccessDeniedException no la puedo capturar con el
                                 * GlobalExceptionHandler?
                                 * 
                                 * Se añade la captura de excepciones, para cuando hay un forbidden en el
                                 * auuthorizetHttpRequests poder devolver el diccionario habitual en las
                                 * respuestas, así, el cliente podrá gestionarlo de una manera genérica.
                                 */
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler((request, response, ex) -> {
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                        response.setContentType("application/json");
                                                        response.getWriter()
                                                                        .write("{\"success\":" + false
                                                                                        + ", \"message\": \"No tienes acceso a este recurso\"}");
                                                }))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .csrf((crfs) -> crfs.disable())
                                .formLogin((form) -> form.disable())
                                .logout((logout) -> logout.disable())
                                .authorizeHttpRequests((authorize) -> authorize

                                                // Apidocs

                                                .requestMatchers("/apidocs/**").anonymous()

                                                // AuthController
                                                .requestMatchers("/api/auth/health", "/api/auth/login").permitAll()

                                                // UserController
                                                .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/user").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/user/dm").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/user/**")
                                                .hasRole(RoleEnum.ADMIN.getName())
                                                .requestMatchers(HttpMethod.PATCH, "/api/user/**")
                                                .hasRole(RoleEnum.ADMIN.getName())

                                                // ForumController

                                                .requestMatchers(HttpMethod.POST, "/api/forum")
                                                .hasRole(RoleEnum.ADMIN.getName())
                                                .requestMatchers(HttpMethod.DELETE, "/api/forum/**")
                                                .hasRole(RoleEnum.ADMIN.getName())
                                                .requestMatchers(HttpMethod.PUT, "/api/forum")
                                                .hasRole(RoleEnum.ADMIN.getName())
                                                .requestMatchers(HttpMethod.GET, "/api/forum/**", "/api/forum")
                                                .authenticated()

                                                // TopicController

                                                .requestMatchers(HttpMethod.POST, "/api/topic").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/topic").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/topic/**", "/api/topic")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/topic/**")
                                                .hasRole(RoleEnum.ADMIN.getName())

                                                .anyRequest().authenticated())
                                .build();
        }

        /**
         * Crea el componente encargado de codificar y firmar los JSON Web Tokens (JWT)
         * utilizados en la autenticación de la aplicación.
         *
         * Utiliza la fuente de claves JWK proporcionada para generar los tokens
         * firmados.
         *
         * @param jwkSource fuente de claves, privada y p´
         * @return instancia de JwtEncoder configurada para generar tokens JWT firmados
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
         * Obtenemos la clave privada generada en local, filtramos limpiando texto que
         * no
         * es necesario
         * 
         * @return devolvemos la clase privada
         * @throws IOException              si hay un error leyendo la clave
         * @throws NoSuchAlgorithmException si el algoritmo de firma no existe
         * @throws InvalidKeySpecException  si la clave no es válida
         * @throws Exception                cualquier otra excepciónF
         */
        @Bean
        public RSAPrivateKey privateKey()
                        throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, Exception {
                String privateKeyContent = Files.readString(Paths.get("clave_privada.pem"))
                                .replace("-----BEGIN PRIVATE KEY-----", "")
                                .replace("-----END PRIVATE KEY-----", "")
                                .replaceAll("\\s", "");
                byte[] decoded = Base64.getDecoder().decode(privateKeyContent);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
                return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }

        /**
         * Obtenemos la clave pública generada en local, filtramos limpiando texto que
         * no
         * es necesario
         * 
         * @return clve pública
         * @throws IOException              si hay un error leyendo la clave
         * @throws NoSuchAlgorithmException si el algoritmo de firma no existe
         * @throws InvalidKeySpecException  si la clave no es válida
         * @throws Exception                cualquier otra excepción
         */
        @Bean
        public RSAPublicKey publicKey()
                        throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, Exception {
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
        public UserDetailsService userDetailsService(UserRepository userEntityRepository) {
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
         * // * @return objeto BCryptPasswordEncoder para coficiar contraseñas
         */
        @Bean
        public static BCryptPasswordEncoder passwordEncoder() {
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
        @Bean
        CommandLineRunner runner(UserRepository userEntityRepository, ForumRepository forumRepository,
                        PostRepository postRepository, DirectMessageRepository directMessageRepository) {
                // https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html

                return args -> {
                        // DemoUsers demoUsers = new DemoUsers(userEntityRepository, passwordEncoder());
                        // DemoForums demoForums = new DemoForums(forumRepository);
                        // new DemoTopics(demoForums.getForumList(), demoUsers, forumRepository);
                        // new DemoPosts(forumRepository.findAll(), userEntityRepository.findAll(),
                        // forumRepository, postRepository);
                        // new DemoDirectMessage(userEntityRepository,directMessageRepository);
                        // System.out.println("¡Datos para demo añadidos satisfactoriamente!");
                };
        }
}

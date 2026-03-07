package dev.aja.aja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import dev.aja.aja.auth.repository.UserEntityRepository;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
        return http
                .csrf((crfs) -> crfs.disable())
                .formLogin((form) -> form.disable())
                .logout((logout) -> logout.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/auth/health", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .build();
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
                        .roles(user.getRole().getName())
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
    //     // https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html
    //     return args -> {
    //         UserEntity admin = UserEntity.builder()
    //                 .username("admin")
    //                 .password(passwordEncoder().encode("1234"))
    //                 .email("admin@aja.dev")
    //                 .role(RoleEnum.ADMIN)
    //                 .build();

    //         UserEntity user = UserEntity.builder()
    //                 .username("user")
    //                 .password(passwordEncoder().encode("1234"))
    //                 .email("user@aja.dev")
    //                 .role(RoleEnum.USER)
    //                 .build();

    //         userEntityRepository.saveAll(List.of(admin, user));
    //     };
    // }
}

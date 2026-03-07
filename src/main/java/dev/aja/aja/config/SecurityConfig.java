package dev.aja.aja.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import dev.aja.aja.auth.RoleEnum;
import dev.aja.aja.auth.entity.UserEntity;
import dev.aja.aja.auth.repository.UserEntityRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf((crfs) -> crfs.disable())
                .formLogin((form) -> form.disable())
                .logout((logout) -> logout.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/auth/health", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html#servlet-authentication-unpwd
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails userAdmin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("1234"))
                .roles("ADMIN")
                .build();

        UserDetails userDetails = User.builder()
                .username("user")
                .password(passwordEncoder().encode("1234"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userAdmin, userDetails);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner runner(UserEntityRepository userEntityRepository) {
        // https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html
        return args -> {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .password(passwordEncoder().encode("1234"))
                    .email("admin@aja.dev")
                    .role(RoleEnum.ADMIN)
                    .build();

            UserEntity user = UserEntity.builder()
                    .username("user")
                    .password(passwordEncoder().encode("1234"))
                    .email("user@aja.dev")
                    .role(RoleEnum.USER)
                    .build();

            userEntityRepository.saveAll(List.of(admin, user));
        };
    }
}

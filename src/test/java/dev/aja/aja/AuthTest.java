package dev.aja.aja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import dev.aja.aja.auth.service.AuthService;
import dev.aja.aja.config.SecurityConfig;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserEntityRepository;
import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

// https://docs.spring.io/spring-framework/reference/testing/mockmvc/hamcrest/setup.html
// Contultas varias a ChatGPT, es la primera vez que hago test a endpoints

/**
 * Clase para realizar test de los endpoint de AuthController.
 * 
 * Utilizamos @Transactional para realizar accesos a la bbdd concretos y,
 * dejarla como estaba.
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Mock
    private AuthService authService;

    /**
     * Añadimos un usuario con nombre de usuario test antes de cada ejecución de
     * cada test
     */
    @BeforeEach
    public void setup() {

        UserEntity userEntity = UserEntity.builder()
                .username("test")
                .password(SecurityConfig.passwordEncoder().encode("test"))
                .email("test@mel0n.dev")
                .isActive(true)
                .build();

        userEntityRepository.save(userEntity);

    }

    /**
     * Test de login correcto con usuario test, se comprueba que la respueta del
     * message (UserEntityDTO), su nombre de usuario corresponda con el que se logeó
     * 
     * @throws Exception
     */
    @Test
    @Order(1)
    public void checkLoginUserResponse200() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                .param("username", "test")
                .param("password", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value("test"))
                .andDo(print());
    }

    /**
     * Test de login incorrecto, se intenta logear con nombre de usuario
     * badUsername, que no existe
     * 
     * @throws Exception
     */
    @Test
    @Order(2)
    public void checkLoginUserResponse404() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                .param("username", "badUsername")
                .param("password", "test"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * Se realiza logout, primeramente, se hace login, se captura la cookie llamada
     * JWT_TOKEN y se utiliza para hacer logout, si no existe la cookie, dará error
     * de forbidden
     * 
     * @throws Exception
     */
    @Test
    @Order(3)
    public void checkLogoutnUserResponse200() throws Exception {

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .param("username", "test")
                .param("password", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value("test"))
                .andDo(print());

        Cookie cookie = result.andReturn().getResponse().getCookie("JWT_TOKEN");

        mockMvc.perform(post("/api/auth/logout")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * Test de logout, se logea un usuario, pero como no se captura la cookie, pues
     * obtenemos un forbidden al intentar el logout
     * 
     * @throws Exception
     */
    @Test
    @Order(3)
    public void checkLogoutnUserResponse403() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                .param("username", "test")
                .param("password", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value("test"))
                .andDo(print());

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

}

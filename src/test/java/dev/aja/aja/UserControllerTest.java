package dev.aja.aja;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import dev.aja.aja.config.SecurityConfig;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserEntityRepository;
import jakarta.servlet.http.Cookie;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Clase para realizar test de los endpoint de UserController.
 * 
 * Utilizamos @Transactional para realizar accesos a la bbdd concretos y,
 * dejarla como estaba.
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    UserEntity userAdminTest1, deleteUser2, editUser3;
    private Cookie cookie;

    /**
     * Añadimos varios usuarios para las pruebas que se van a ir realizando. Nos
     * identificamos con un usuario de rol admin y obtenemos la cookie para poder ir
     * haciendo el resto de pruebas
     * 
     * @throws Exception
     */
    @BeforeEach
    public void setup() throws Exception {

        this.userAdminTest1 = UserEntity.builder()
                .username("userAdminTest1")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("userAdminTest1@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.ADMIN.getName())
                .build();

        this.deleteUser2 = UserEntity.builder()
                .username("deleteUser2")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("deleteUser2@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .build();

        this.editUser3 = UserEntity.builder()
                .username("user3")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("user3@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .build();

        userEntityRepository.saveAll(List.of(this.userAdminTest1, this.deleteUser2, this.editUser3));

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .param("username", "userAdminTest1")
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value("userAdminTest1"))
                .andDo(print());

        cookie = result.andReturn().getResponse().getCookie("JWT_TOKEN");

    }

    /**
     * Prueba donde añadimos un usuario y seguidamente lo obtenemos directamente del
     * repositorio para obtener su id, luego, mediante /api/user/{id} lo volvemos a
     * recuperar pero esta vez desde el controlador
     */
    @Order(1)
    @Test
    public void addUserAndGetThisUserWithOK() {

        try {

            UserEntity user4 = UserEntity.builder()
                    .username("user4")
                    .password(SecurityConfig.passwordEncoder().encode("1234"))
                    .email("user4@mel0n.dev")
                    .isActive(true)
                    .role(RoleEnum.ADMIN.getName())
                    .build();

            mockMvc.perform(post("/api/user")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user4)))
                    .andExpect(status().isOk());

            UserEntity user4FromDB = userEntityRepository.findByUsername(user4.getUsername()).get();

            MvcResult resultUser = mockMvc.perform(get("/api/user/{id}", user4FromDB.getId().toString())
                    .cookie(cookie))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            String response = resultUser.getResponse().getContentAsString();

            assertTrue(response.contains(user4.getUsername()));

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uno de los usarios que en el setup se añadiendo se elimina, se comprueba que
     * el string de respuesta contiene true para validar sin tener que serializar en
     * objetos
     * 
     * @throws Exception
     */
    @Order(2)
    @Test
    public void deleteUserWithOk() throws Exception {

        MvcResult result = mockMvc.perform(delete("/api/user/{id}", this.deleteUser2.getId())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("true"));

    }

    /**
     * Obtenemos el usuario para comprobar, que está guardado y contiene el nombre
     * con el que se guardó. Seguidamente, editamos su nombre de usuario, lo
     * guardamos y, desde el repositorio volvemos a obtener ese usuario utilizando
     * su id para comprobar que el nombre de usuario se cambió con éxito
     */
    @Order(3)
    @Test
    public void updateUserWithOk() {

        try {

            MvcResult resultUser = mockMvc.perform(get("/api/user/{id}", this.editUser3.getId().toString())
                    .cookie(cookie))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            String response = resultUser.getResponse().getContentAsString();

            assertTrue(response.contains(this.editUser3.getUsername()));

            this.editUser3.setUsername("usernamEdited");
            mockMvc.perform(put("/api/user")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(this.editUser3)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            assertEquals(userEntityRepository.findById(this.editUser3.getId()).get().getUsername(),
                    this.editUser3.getUsername());
        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Enviamos un username de más de 20 carácteres para forzar que se lance la
     * excepción IllegalARgumentException que tenemos aplicada en UserService en su
     * método addUser
     */
    @Order(4)
    @Test
    public void addNewUserWithBigUsername() {
        try {

            UserEntity userBigUsername = UserEntity.builder()
                    .username(
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .password(SecurityConfig.passwordEncoder().encode("1234"))
                    .email("user4@mel0n.dev")
                    .isActive(true)
                    .role(RoleEnum.ADMIN.getName())
                    .build();

            mockMvc.perform(post("/api/user")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userBigUsername)))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof IllegalArgumentException));

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enviamos un username de más de 65 carácteres para forzar que se lance la
     * excepción IllegalARgumentException que tenemos aplicada en UserService en su
     * método addUser. Se ha utilizado 65 carácteres, porque el SHA256 del hash de
     * contraseña aplica 64 de tamaño máximo
     */
    @Order(5)
    @Test
    public void addNewUserWithBigPassword() {
        try {

            UserEntity userBigUsername = UserEntity.builder()
                    .username(
                            "abc")
                    .password("f9K2mX8vQzR4bLp7TnJ1sV6wGhD0aCeY3uBqMzE5rLkNpSxFoWjUiaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .email("user4@mel0n.dev")
                    .isActive(true)
                    .role(RoleEnum.ADMIN.getName())
                    .build();

            mockMvc.perform(post("/api/user")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userBigUsername)))
                    .andExpect(status().isOk())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof IllegalArgumentException));

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

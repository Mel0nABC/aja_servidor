package dev.aja.aja;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
import dev.aja.aja.user.dto.UserEntityNewDTO;
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

    UserEntity userAdminTest1, deleteUser2, editUser3, userForCookie;
    private Cookie adminCookie, userCookie, otherUserCookie;

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
                .registerDate(LocalDate.now())
                .build();

        this.deleteUser2 = UserEntity.builder()
                .username("deleteUser2")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("deleteUser2@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        this.editUser3 = UserEntity.builder()
                .username("user3")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("user3@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        this.userForCookie = UserEntity.builder()
                .username("usercookie")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("usercookie@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        userEntityRepository
                .saveAll(List.of(this.userAdminTest1, this.deleteUser2, this.editUser3, this.userForCookie));

        ResultActions resultAdmin = mockMvc.perform(post("/api/auth/login")
                .param("username", userAdminTest1.getUsername())
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value(userAdminTest1.getUsername()))
                .andDo(print());

        this.adminCookie = resultAdmin.andReturn().getResponse().getCookie("JWT_TOKEN");

        ResultActions resultUser = mockMvc.perform(post("/api/auth/login")
                .param("username", editUser3.getUsername())
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value(editUser3.getUsername()))
                .andDo(print());

        this.userCookie = resultUser.andReturn().getResponse().getCookie("JWT_TOKEN");

        ResultActions resultUserCookie = mockMvc.perform(post("/api/auth/login")
                .param("username", userForCookie.getUsername())
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value(userForCookie.getUsername()))
                .andDo(print());

        this.otherUserCookie = resultUserCookie.andReturn().getResponse().getCookie("JWT_TOKEN");

    }

    /**
     * Prueba donde añadimos un usuario y seguidamente lo obtenemos directamente del
     * repositorio para obtener su id, luego, mediante /api/user/{id} lo volvemos a
     * recuperar pero esta vez desde el controlador
     */
    @Test
    public void addUserAndGetThisUserWithOK() {

        try {

            UserEntityNewDTO user4 = UserEntityNewDTO.builder()
                    .username("user4")
                    .password("1234")
                    .email("user4@mel0n.dev")
                    .build();

            mockMvc.perform(post("/api/user")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user4)))
                    .andExpect(status().isOk());

            UserEntity user4FromDB = userEntityRepository.findByUsername(user4.username()).get();

            MvcResult resultUser = mockMvc.perform(get("/api/user/{id}", user4FromDB.getId().toString())
                    .cookie(this.adminCookie))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            String response = resultUser.getResponse().getContentAsString();

            assertTrue(response.contains(user4.username()));

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
    @Test
    public void deleteUserWithAdminResultOk() throws Exception {

        MvcResult result = mockMvc.perform(delete("/api/user/{id}", this.deleteUser2.getId())
                .cookie(this.adminCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("true"));

    }

    /**
     * Un usuario intenta eliminar su propia cuenta, obteniendo resultado favorable
     * 
     * @throws Exception
     */
    @Test
    public void autoDeleteUserAccountWithResultOk() throws Exception {

        MvcResult result = mockMvc.perform(delete("/api/user/{id}", this.userForCookie.getId())
                .cookie(this.otherUserCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("true"));

    }

    /**
     * Un usuario de role User intentan eliminar otro usuario, no es posible
     * 
     * @throws Exception
     */
    @Test
    public void deleteUserWithUserRoleWithResultForbidden() throws Exception {

        MvcResult result = mockMvc.perform(delete("/api/user/{id}", this.userAdminTest1.getId())
                .cookie(this.otherUserCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("false"));

    }

    /**
     * Obtenemos el usuario para comprobar, que está guardado y contiene el nombre
     * con el que se guardó. Seguidamente, editamos su correo electrónico, lo
     * guardamos y, desde el repositorio volvemos a obtener ese usuario utilizando
     * su id para comprobar que el correo electrónico se cambió con éxito
     * Repetimos los pasos con username.
     * 
     * Se sigue ese orden con el fin de no invalidar el JWT y tener que hacer login
     * nuevamente
     */
    @Test
    public void updateUserWithOk() {

        try {

            MvcResult resultUser = mockMvc.perform(get("/api/user/{id}", this.editUser3.getId().toString())
                    .cookie(this.adminCookie))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            String response = resultUser.getResponse().getContentAsString();

            assertTrue(response.contains(this.editUser3.getUsername()));

            UserEntity tmp = new UserEntity(editUser3.getId(), editUser3.getUsername(), editUser3.getPassword(),
                    editUser3.getEmail(), editUser3.getRole(), editUser3.getIsActive(), editUser3.getRegisterDate());

            tmp.setEmail("newemail@ajateam.dev");

            mockMvc.perform(put("/api/user")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tmp)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            assertEquals(userEntityRepository.findByUsername(tmp.getEmail()).get().getEmail(),
                    tmp.getEmail());

            // Se cambia el username por último, porque si no la cookie de sesión no es
            // válida
            tmp.setUsername("NewName");

            mockMvc.perform(put("/api/user")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tmp)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

            assertEquals(userEntityRepository.findByUsername(tmp.getUsername()).get().getUsername(),
                    tmp.getUsername());

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
                    .cookie(this.adminCookie)
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
                    .cookie(this.adminCookie)
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
     * Intentamos obtener la información del usuario edtUser3 mediante su id,
     * utilizando un usuario de rol admin. En este caso, el servidor nos devuelve un
     * response ok, así que también incluiría el user DTO
     */
    @Test
    public void getUserWithAdminWithResultOK() {
        try {

            mockMvc.perform(get("/api/user/{id}", this.editUser3.getId().toString())
                    .cookie(this.adminCookie))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Intentamos obtener la información del usuario edtUser3 mediante su id,
     * utilizando un usuario de rol user. En este caso, el servidor nos devuelve un
     * response forbidden, que significa que no se ha podido realizar la petición
     */
    @Test
    public void getUserWithUserWithResultNoOK() {
        try {

            mockMvc.perform(get("/api/user/{id}", this.editUser3.getId().toString())
                    .cookie(this.userCookie))
                    .andExpect(status().isForbidden())
                    .andDo(print())
                    .andReturn();

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Intentamos obtener la lista completa de usuarios del foro. Con respuesta ok
     * al utilizar un usuario de role Admin
     */
    @Test
    public void getAllUsersWithAdminRoleResultOk() {
        try {

            mockMvc.perform(get("/api/user")
                    .cookie(this.adminCookie))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Intentamos obtener la lista completa de usuarios del foro. Con respuesta
     * forbidden al utilizar un usuario de role Admin
     */
    @Test
    public void getAllUsersWithUserRoleResultNoOk() {
        try {

            mockMvc.perform(get("/api/user")
                    .cookie(this.userCookie))
                    .andExpect(status().isForbidden())
                    .andDo(print())
                    .andReturn();

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

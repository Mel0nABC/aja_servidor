package dev.aja.aja;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import dev.aja.aja.config.SecurityConfig;
import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.forum.service.ForumService;
import dev.aja.aja.topic.dto.ForumEntityNewDTO;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Clase para realizar test de los endpoint de ForumController.
 * 
 * Utilizamos @Transactional para realizar accesos a la bbdd concretos y,
 * dejarla como estaba.
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ForumService forumService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie adminCookie, userCookie;
    private ForumEntity forumEntity;

    /**
     * Añadimos un usuario de cada role para poder realizar las pruebas
     */
    @BeforeEach
    public void setup() throws Exception {

        UserEntity userAdmin = UserEntity.builder()
                .username("adminTest")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("adminTest@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.ADMIN.getName())
                .registerDate(LocalDate.now())
                .build();

        UserEntity userUser = UserEntity.builder()
                .username("userTest")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("userTest@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        userRepository.saveAll(List.of(userAdmin, userUser));

        ResultActions resultAdmin = mockMvc.perform(post("/api/auth/login")
                .param("username", userAdmin.getUsername())
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value(userAdmin.getUsername()))
                .andDo(print());

        this.adminCookie = resultAdmin.andReturn().getResponse().getCookie("JWT_TOKEN");

        ResultActions resultUser = mockMvc.perform(post("/api/auth/login")
                .param("username", userUser.getUsername())
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value(userUser.getUsername()))
                .andDo(print());

        this.userCookie = resultUser.andReturn().getResponse().getCookie("JWT_TOKEN");

        this.forumEntity = ForumEntity.builder()
                .title("toDelete")
                .creationDate(LocalDate.now())
                .lastModification(LocalDate.now())
                .build();

        forumRepository.save(forumEntity);

    }

    /**
     * Añadimos un nuevo foro de manera satisfactoria
     */
    @Test
    public void addNewForumWithRoleAdminResultOk() {
        try {

            ForumEntityNewDTO newForumDTO = ForumEntityNewDTO.builder()
                    .title("Forum Test")
                    .build();

            mockMvc.perform(post("/api/forum")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newForumDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test para comprobar que se valida que el title del forum existe
     */
    @Test
    public void addNewForumWithRoleAdminWithTitleAlreadyExistResultError() {
        try {

            ForumEntity forumEntity = ForumEntity.builder()
                    .title("TestExist")
                    .creationDate(LocalDate.now())
                    .lastModification(LocalDate.now())
                    .build();

            forumRepository.save(forumEntity);

            ForumEntityNewDTO newForumDTO = ForumEntityNewDTO.builder()
                    .title("TestExist")
                    .build();

            mockMvc.perform(post("/api/forum")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newForumDTO)))
                    .andExpect(status().isConflict())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * añadimos un nuevo foro y nos da error por no tener privilegios
     */
    @Test
    public void addNewForumWithroleUserResultForbidden() {
        try {

            ForumEntityNewDTO newForumDTO = ForumEntityNewDTO.builder()
                    .title("Forum Test")
                    .build();

            mockMvc.perform(post("/api/forum")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newForumDTO)))
                    .andExpect(status().isForbidden())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eliminamos un forum correctamente con el usuario de role ADMIN
     */
    @Test
    public void delForumWithRoleAdminResultOk() {
        try {

            ForumEntity forumDDBB = forumRepository.findByTitle(forumEntity.getTitle()).get();

            mockMvc.perform(delete("/api/forum/" + forumDDBB.getId())
                    .cookie(this.adminCookie))
                    .andExpect(status().isOk())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Se intenta eliminar un forum con un usuario de role USER, da error de acceso
     */
    @Test
    public void delForumWithRoleUserResultForbidden() {
        try {

            ForumEntity forumDDBB = forumRepository.findByTitle(forumEntity.getTitle()).get();

            mockMvc.perform(delete("/api/forum/" + forumDDBB.getId())
                    .cookie(this.userCookie))
                    .andExpect(status().isForbidden())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Intentamos eliminar una forum con un id que no existe
     */
    @Test
    public void delForumWithRoleAdminWithTitleForumErrorResultConflict() {
        try {

            Long id = 9127830127380123L;

            mockMvc.perform(delete("/api/forum/" + id)
                    .cookie(this.adminCookie))
                    .andExpect(status().isConflict())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

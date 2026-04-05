package dev.aja.aja;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import dev.aja.aja.config.SecurityConfig;
import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.forum.service.ForumService;
import dev.aja.aja.topic.dto.TopicNewEditDTO;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.topic.repository.TopicRepository;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Mock
    private ForumService forumService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie adminCookie, userCookie;
    private ForumEntity forumEntity;
    private TopicEntity topicToDel;

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

        TopicNewEditDTO topicNewEditDTO = TopicNewEditDTO.builder()
                .title("Topic to del Test")
                .forumId(this.forumEntity.getId())
                .build();

        mockMvc.perform(post("/api/topic")
                .cookie(this.userCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(topicNewEditDTO)))
                .andExpect(status().isOk())
                .andDo(print());

        Optional<TopicEntity> topicOptional = topicRepository.findByTitle(topicNewEditDTO.title());

        this.topicToDel = topicOptional.get();
    }

    /**
     * Test para añadir un nuevo TopicEntity, se realiza chequeo que la respuesta de
     * la api sea un HttpStatus ok y además se consulta la base de datos para
     * comprobar que exista un topic con ese título añadido
     */
    @Test
    public void addNewTopicNotExistWithResultOk() {

        try {

            TopicNewEditDTO topicNewEditDTO = TopicNewEditDTO.builder()
                    .title("Topic de prueba")
                    .forumId(this.forumEntity.getId())
                    .build();

            mockMvc.perform(post("/api/topic")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicNewEditDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            /**
             * Si findByTitle que es un Optional, no está vacío, significa que se encontro
             * la entidad en la base de datos.
             */
            assertTrue(!topicRepository.findByTitle(topicNewEditDTO.title()).isEmpty());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Test para añadir un nuevo TopicEntity, se realiza chequeo que la respuesta de
     * la api sea un HttpStatus Found y además se consulta la base de datos para
     * comprobar que exista un topic con ese título añadido
     */
    @Test
    public void addNewTopicExistWithResultError() {
        try {

            TopicNewEditDTO topicNewEditDTO = TopicNewEditDTO.builder()
                    .title("Topic de prueba")
                    .forumId(this.forumEntity.getId())
                    .build();

            mockMvc.perform(post("/api/topic")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicNewEditDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            /**
             * Si findByTitle que es un Optional, no está vacío, significa que se encontro
             * la entidad en la base de datos.
             */
            assertTrue(!topicRepository.findByTitle(topicNewEditDTO.title()).isEmpty());

            /**
             * Respuesta del test en cuestión
             */
            mockMvc.perform(post("/api/topic")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicNewEditDTO)))
                    .andExpect(status().isFound())
                    .andDo(print());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Eliminamos un topic con una coopie con role ADMIN
     */
    @Test
    public void delTopicExistWithResultOk() {
        try {
            mockMvc.perform(delete("/api/topic/" + this.topicToDel.getId())
                    .cookie(this.adminCookie))
                    .andExpect(status().isOk())
                    .andDo(print());

            /**
             * Si findByTitle que es un Optional, no está vacío, significa que se encontro
             * la entidad en la base de datos.
             */
            assertTrue(!topicRepository.findById(this.topicToDel.getId()).isEmpty());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Se intenta eliminar un topic con una cookie de rol USER
     */
    @Test
    public void delTopicExistWithUserRoleAndResultForbidden() {
        try {
            mockMvc.perform(delete("/api/topic/" + this.topicToDel.getId())
                    .cookie(this.userCookie))
                    .andExpect(status().isForbidden())
                    .andDo(print());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

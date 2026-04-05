package dev.aja.aja;

import java.time.LocalDate;
import java.util.ArrayList;
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
import dev.aja.aja.topic.dto.TopicEditDTO;
import dev.aja.aja.topic.dto.TopicEntityDTO;
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
    private ForumEntity forumEntity, newForumToEditTopic;
    private TopicEntity topicToDel, topicToEdit;
    private List<TopicEntity> topicList = new ArrayList<>();

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

        this.newForumToEditTopic = ForumEntity.builder()
                .title("Forum to edit topic")
                .creationDate(LocalDate.now())
                .lastModification(LocalDate.now())
                .build();

        forumRepository.save(this.newForumToEditTopic);

        this.forumEntity = ForumEntity.builder()
                .title("toDelete")
                .creationDate(LocalDate.now())
                .lastModification(LocalDate.now())
                .build();

        forumRepository.save(this.forumEntity);

        TopicNewEditDTO newTopic = TopicNewEditDTO.builder()
                .title("Topic to del Test")
                .forumId(this.forumEntity.getId())
                .build();

        mockMvc.perform(post("/api/topic")
                .cookie(this.adminCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTopic)))
                .andExpect(status().isOk())
                .andDo(print());

        Optional<TopicEntity> topicOptional = topicRepository.findByTitle(newTopic.title());

        this.topicToDel = topicOptional.get();

        TopicNewEditDTO editTopic = TopicNewEditDTO.builder()
                .title("Topic to edit Test")
                .forumId(this.forumEntity.getId())
                .build();

        mockMvc.perform(post("/api/topic")
                .cookie(this.adminCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editTopic)))
                .andExpect(status().isOk())
                .andDo(print());

        Optional<TopicEntity> editTopicOptional = topicRepository.findByTitle(editTopic.title());

        this.topicToEdit = editTopicOptional.get();

        topicList.add(topicToDel);
        topicList.add(topicToEdit);
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

    /**
     * Editamos un TopicEntity y comprobamos que el nuevo título existe
     */
    @Test
    public void editTopicTitleWithResultOk() {
        try {

            TopicEditDTO topicEditDTO = TopicEditDTO.builder()
                    .title("NEW EDITED TOPIC TITLE")
                    .id(this.topicToEdit.getId())
                    .currentForumId(this.topicToEdit.getForum().getId())
                    .newForumId(this.topicToEdit.getForum().getId())
                    .build();

            mockMvc.perform(put("/api/topic")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicEditDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            /**
             * Si findByTitle que es un Optional, no está vacío, significa que se encontro
             * la entidad en la base de datos buscándola con el nuevo título.
             */
            assertTrue(!topicRepository.findByTitle(this.topicToEdit.getTitle()).isEmpty());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Editamos un topic el título de otro topic que ya existe
     */
    @Test
    public void editTopicTitleWithResultAlreadyExist() {
        try {

            TopicEditDTO topicEditDTO = TopicEditDTO.builder()
                    .title(this.topicToDel.getTitle())
                    .id(this.topicToEdit.getId())
                    .currentForumId(this.topicToEdit.getForum().getId())
                    .newForumId(this.topicToEdit.getForum().getId())
                    .build();

            mockMvc.perform(put("/api/topic")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicEditDTO)))
                    .andExpect(status().isFound())
                    .andDo(print());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Editar un título de un topic que el owner del usuario es diferente. Los users
     * role ADMIN no tienen esta restricción
     */
    @Test
    public void editTopicTitleWithOtherUserResultNotEditPermissions() {
        try {

            TopicEditDTO topicEditDTO = TopicEditDTO.builder()
                    .title("NEW EDITED TOPIC TITLE")
                    .id(this.topicToEdit.getId())
                    .currentForumId(this.topicToEdit.getForum().getId())
                    .newForumId(this.topicToEdit.getForum().getId())
                    .build();

            mockMvc.perform(put("/api/topic")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicEditDTO)))
                    .andExpect(status().isForbidden())
                    .andDo(print());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Editamos un topic cambiándole el forum
     */
    @Test
    public void editTopicSetNewForumWithResultOk() {
        try {

            TopicEditDTO topicEditDTO = TopicEditDTO.builder()
                    .title(this.topicToEdit.getTitle())
                    .id(this.topicToEdit.getId())
                    .currentForumId(this.topicToEdit.getForum().getId())
                    .newForumId(this.newForumToEditTopic.getId())
                    .build();

            mockMvc.perform(put("/api/topic")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicEditDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            // Recorremos todos los topics del newForumToEditTopic en busca del id del
            // topicEditDTO que es el que se ha editado. Así comprobamos que ese topic ha
            // pasado a ser parte del nuevo Forum
            assertTrue(forumRepository.findById(this.newForumToEditTopic.getId()).get().getTopicList().stream()
                    .anyMatch(topic -> topic.getId().equals(topicEditDTO.id())));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Editamos un topic cambiándole el forum por uno que no existe
     */
    @Test
    public void editTopicSetNewForumWithResultForumNotExist() {
        try {

            TopicEditDTO topicEditDTO = TopicEditDTO.builder()
                    .title(this.topicToEdit.getTitle())
                    .id(this.topicToEdit.getId())
                    .currentForumId(this.topicToEdit.getForum().getId())
                    .newForumId(197826379182L)
                    .build();

            mockMvc.perform(put("/api/topic")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(topicEditDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Obtenemos un topic con resultado ok.
     */
    @Test
    public void getTopicWithResultOk() {
        try {

            mockMvc.perform(get("/api/topic/" + this.topicToEdit.getId())
                    .cookie(this.userCookie))
                    .andExpect(status().isOk())
                    .andDo(print());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * No podemos obtener el topic porque el id no existe
     */
    @Test
    public void getTopicWithResultNotFound() {
        try {

            mockMvc.perform(get("/api/topic/" + 91287391823L)
                    .cookie(this.userCookie))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Obtenemos una lista de TopicEntity, esta puede tener o no entidades (puede
     * estar vacía o no)
     */
    @Test
    public void getAllTopicWithResultOk() {
        try {
            ResultActions result = mockMvc.perform(get("/api/topic")
                    .cookie(this.userCookie))
                    .andExpect(status().isOk())
                    .andDo(print());

            String json = result.andReturn().getResponse().getContentAsString();

            @SuppressWarnings("unchecked")
            List<TopicEntityDTO> topicEntityDTOList = objectMapper.treeToValue(
                    objectMapper.readTree(json).get("message"),
                    List.class);

            assertTrue(topicEntityDTOList.size() >= topicList.size());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

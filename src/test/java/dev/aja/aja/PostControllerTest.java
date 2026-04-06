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
import dev.aja.aja.post.dto.PostEditDTO;
import dev.aja.aja.post.dto.PostEntityDTO;
import dev.aja.aja.post.dto.PostNewDTO;
import dev.aja.aja.post.entity.PostEntity;
import dev.aja.aja.post.repository.PostRepository;
import dev.aja.aja.topic.dto.TopicNewEditDTO;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.topic.repository.TopicRepository;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ForumService forumService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie adminCookie, userCookie;
    private ForumEntity forumEntity;
    private TopicEntity topicEntity;

    /**
     * Configuración que se ejecuta antes de cada test para poder tener disponibles
     * usuarios, forums y topics donde añadis lost nuevos post
     * 
     * @throws Exception
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
                .title("Forum for testing PostEntity")
                .creationDate(LocalDate.now())
                .lastModification(LocalDate.now())
                .build();

        forumRepository.save(forumEntity);

        TopicNewEditDTO newTopic = TopicNewEditDTO.builder()
                .title("Topic for testing PostEntity")
                .forumId(this.forumEntity.getId())
                .build();

        mockMvc.perform(post("/api/topic")
                .cookie(this.adminCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTopic)))
                .andExpect(status().isOk())
                .andDo(print());

        this.topicEntity = topicRepository.findByTitle(newTopic.title()).get();
    }

    /**
     * Añadimos un nuevo post a un topic con resultado satisfactorio
     */
    @Test
    public void addPostWithResultOk() {

        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());
        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Añadimos un nuevo post a un topic que no existe con resultado topic not found
     */
    @Test
    public void addPostWithResultTopicNotFound() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(129387L)
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Añadimos un nuevo post con postNewDTO, lo obtenemos mediante el
     * postRepository, creamos un PostEditDTO para editar el que hemos obtenido del
     * repositorio, lo guardamos, volvemos a obtenerlo del repositorio y comparamos
     * que el texto del PostEditDTO y el último post del repositorio postEntity2
     * tengan el mismo texto
     */
    @Test
    public void editPostWithResultOk() {

        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            PostEntity postEntity = postOptional.get();

            PostEditDTO postEditDTO = PostEditDTO.builder()
                    .id(postEntity.getId())
                    .text("Nuevo texto del post: " + postEntity.getId())
                    .build();

            mockMvc.perform(put("/api/post")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postEditDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional2 = postRepository.findByText(postEditDTO.text());

            assertTrue(!postOptional2.isEmpty());

            PostEntity postEntity2 = postOptional2.get();

            assertTrue(postEntity2.getText().equals(postEditDTO.text()));

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Añadimos un nuevo post con el user con role USER, luego, intentamos editarlo
     * con el user de role ADMIN y nos dará un forbidden
     */
    @Test
    public void editPostWithOtherUserWithResultForbidden() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            PostEntity postEntity = postOptional.get();

            PostEditDTO postEditDTO = PostEditDTO.builder()
                    .id(postEntity.getId())
                    .text("Nuevo texto del post: " + postEntity.getId())
                    .build();

            mockMvc.perform(put("/api/post")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postEditDTO)))
                    .andExpect(status().isForbidden())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Añadimos un post, para seguidamente eliminarlo. Comprobamos que
     * postOptionalDeleted está vacío para comprobar que dicho psot se ha eliminado,
     * anteriormente se comprobó que sí existía
     */
    @Test
    public void delPostWithResultOk() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            mockMvc.perform(delete("/api/post/" + postOptional.get().getId())
                    .cookie(this.userCookie))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptionalDeleted = postRepository.findByText(postNewDTO.text());

            assertTrue(postOptionalDeleted.isEmpty());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Añadimos un post con un usuario de role USER, para seguidamente eliminarlo
     * con un usuario de role ADMIN. Comprobamos que postOptionalDeleted está vacío
     * para comprobar que dicho psot se ha eliminado, anteriormente se comprobó que
     * sí existía
     */
    @Test
    public void delPostWithUserRoleAdminResultOk() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.userCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            mockMvc.perform(delete("/api/post/" + postOptional.get().getId())
                    .cookie(this.userCookie))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptionalDeleted = postRepository.findByText(postNewDTO.text());

            assertTrue(postOptionalDeleted.isEmpty());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Se añade un nuevo post y seguidamente se intenta eliminar, como el usuario
     * que lo añade es el user con role ADMIN y el que lo intenta eliminar con role
     * USER que son dos usuarios diferentes, no es posible eliminarlo
     */
    @Test
    public void delPostWithOtherUserWithResultForbidden() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            mockMvc.perform(delete("/api/post/" + postOptional.get().getId())
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
     * Guardamos un post y seguidamente lo obtenemos desde el controlador con el
     * método GET con resultado OK
     */
    @Test
    public void getPostInformationToEditWithResultOk() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            mockMvc.perform(get("/api/post/" + postOptional.get().getId())
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
     * Guardamos un post y seguidamente lo obtenemos desde el controlador con el
     * método GET con resultado forbidden, porque el usuario que quiere obtener toda
     * la información es otro
     */
    @Test
    public void getPostInformationToEditWithResultForbidden() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            mockMvc.perform(get("/api/post/" + postOptional.get().getId())
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
     * Se obtienen todos los post y se comprueba que en la lista que devuelve, al
     * menos, hay un post que es el añadido al principio del método
     */
    @Test
    public void getAllPostWithResultOk() {
        try {

            PostNewDTO postNewDTO = PostNewDTO.builder()
                    .text("Este es el texto para añadir de ejemplo a un Post")
                    .topicId(this.topicEntity.getId())
                    .build();

            mockMvc.perform(post("/api/post")
                    .cookie(this.adminCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

            Optional<PostEntity> postOptional = postRepository.findByText(postNewDTO.text());

            assertTrue(!postOptional.isEmpty());

            ResultActions result = mockMvc.perform(get("/api/post")
                    .cookie(this.userCookie))
                    .andExpect(status().isOk())
                    .andDo(print());

            String json = result.andReturn().getResponse().getContentAsString();

            @SuppressWarnings("unchecked")
            List<PostEntityDTO> postList = objectMapper.treeToValue(objectMapper.readTree(json).get("message"),
                    List.class);

            assertTrue(postList.size() >= 1);

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
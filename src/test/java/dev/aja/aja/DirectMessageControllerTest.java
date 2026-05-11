package dev.aja.aja;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import dev.aja.aja.config.SecurityConfig;
import dev.aja.aja.directmessage.dto.DirectMessageNewDTO;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DirectMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie sendUserCookie, receiveUserCookie;
    private UserEntity userSendDmDB, userReceiveDmDB, otherUserDmDB;

    /**
     * Añadimos tres usuarios, para tener uno que envía DM, otro que recibe y otro
     * para errores
     */
    @BeforeEach
    public void setup() throws Exception {

        UserEntity userSendDM = UserEntity.builder()
                .username("adminTest")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("adminTest@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.ADMIN.getName())
                .registerDate(LocalDate.now())
                .build();

        UserEntity userReceiveDM = UserEntity.builder()
                .username("userTest")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("userTest@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        UserEntity otherUser = UserEntity.builder()
                .username("userOther")
                .password(SecurityConfig.passwordEncoder().encode("1234"))
                .email("userOther@mel0n.dev")
                .isActive(true)
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        userRepository.saveAll(List.of(userSendDM, userReceiveDM, otherUser));

        ResultActions resultAdmin = mockMvc.perform(post("/api/auth/login")
                .param("username", userSendDM.getUsername())
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value(userSendDM.getUsername()))
                .andDo(print());

        this.sendUserCookie = resultAdmin.andReturn().getResponse().getCookie("JWT_TOKEN");

        ResultActions resultUser = mockMvc.perform(post("/api/auth/login")
                .param("username", userReceiveDM.getUsername())
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message.username").value(userReceiveDM.getUsername()))
                .andDo(print());

        this.receiveUserCookie = resultUser.andReturn().getResponse().getCookie("JWT_TOKEN");

        Optional<UserEntity> userSendDmDBOptional = userRepository.findByUsername(userSendDM.getUsername());

        assertFalse(userSendDmDBOptional.isEmpty());

        this.userSendDmDB = userSendDmDBOptional.get();

        Optional<UserEntity> userReceiveDmDBOptional = userRepository.findByUsername(userReceiveDM.getUsername());

        assertFalse(userReceiveDmDBOptional.isEmpty());

        this.userReceiveDmDB = userReceiveDmDBOptional.get();

        Optional<UserEntity> otherUserDmOptional = userRepository.findByUsername(otherUser.getUsername());

        assertFalse(otherUserDmOptional.isEmpty());

        this.otherUserDmDB = otherUserDmOptional.get();

        sendDirectMessageResultOk();

    }

    /**
     * Test para enviar DM con resultado ok
     */
    @Test
    public void sendDirectMessageResultOk() {

        try {

            String dmTestMsg = "DM To test";

            DirectMessageNewDTO directMessageNewDTO = DirectMessageNewDTO.builder()
                    .idUserTo(this.userReceiveDmDB.getId())
                    .text(dmTestMsg)
                    .build();

            mockMvc.perform(put("/api/dm")
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(directMessageNewDTO)))
                    .andExpect(status().isOk())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test para enviar DM con resultado erróneo, porque se envía un dm al mismo
     * usuario que lo envía.
     */
    @Test
    public void sendDirectMessageWithSameUserException() {
        try {

            String dmTestMsg = "DM To test";

            DirectMessageNewDTO directMessageNewDTO = DirectMessageNewDTO.builder()
                    .idUserTo(this.userSendDmDB.getId())
                    .text(dmTestMsg)
                    .build();

            mockMvc.perform(put("/api/dm")
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(directMessageNewDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test para enviar DM con resultado erróneo, porque se envía un dm a un usuario
     * quen o existe
     */
    @Test
    public void sendDirectMessageWithUserDestinationNotFoundExcepcion() {
        try {

            String dmTestMsg = "DM To test";

            DirectMessageNewDTO directMessageNewDTO = DirectMessageNewDTO.builder()
                    .idUserTo(1234L)
                    .text(dmTestMsg)
                    .build();

            mockMvc.perform(put("/api/dm")
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(directMessageNewDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test con solicitud de una conversación con resultado OK
     */
    @Test
    public void getDirectMessageWithResultOk() {
        try {

            mockMvc.perform(get("/api/dm/" + this.userReceiveDmDB.getId())
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test con solicitud de una conversación con resultado erróneo, el usuario no
     * existe
     */
    @Test
    public void getDirectMessageWithResultUserDestinationNotFoundExcepcion() {
        try {

            mockMvc.perform(get("/api/dm/" + 1234L)
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test con solicitud de una conversación con resultado erróneo, no se tiene una
     * conversación con ese usuario
     */
    @Test
    public void getDirectMessageWithResultDirectMessageNotFoundException() {
        try {

            mockMvc.perform(get("/api/dm/" + this.otherUserDmDB.getId())
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eliminamos una conversación con un usuario con resultado ok
     */
    @Test
    public void delDirectMessageWithResultOk() {
        try {

            mockMvc.perform(delete("/api/dm/" + this.userReceiveDmDB.getId())
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eliminamos una conversación con un usuario que no existe con resultado
     * erróneo
     */
    @Test
    public void delDirectMessageWithResultUserDestinationNotFoundExcepcion() {
        try {

            mockMvc.perform(delete("/api/dm/" + 1234L)
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eliminamos una conversación que no existe con resultado erróneo
     */
    @Test
    public void delDirectMessageWithResultDirectMessageNotFoundException() {
        try {

            mockMvc.perform(delete("/api/dm/" + otherUserDmDB.getId())
                    .cookie(this.sendUserCookie)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());

        } catch (JacksonException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

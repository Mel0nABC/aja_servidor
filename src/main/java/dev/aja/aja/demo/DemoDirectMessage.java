package dev.aja.aja.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import dev.aja.aja.directmessage.entity.DirectMessageEntity;
import dev.aja.aja.directmessage.entity.MessageEntity;
import dev.aja.aja.directmessage.repository.DirectMessageRepository;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;

/**
 * Clase para crear DirectMessageEntity para la versión de prueba
 */
public class DemoDirectMessage {

    private UserRepository userRepository;
    private DirectMessageRepository directMessageRepository;

    public DemoDirectMessage(UserRepository userRepository, DirectMessageRepository directMessageRepository) {
        this.userRepository = userRepository;
        this.directMessageRepository = directMessageRepository;
        createDemoData();
    }

    /**
     * Se crean varios mensajes mediante un bucle del primer usuario al resto, todos
     * responden
     */
    @Transactional
    public void createDemoData() {

        List<UserEntity> userList = userRepository.findAll();

        UserEntity userSendMessage = userList.getFirst();

        userList.forEach(user -> {

            if (!user.getId().equals(userSendMessage.getId())) {

                // SALUDOS

                DirectMessageEntity helloDM = DirectMessageEntity.builder()
                        .participants(List.of(userSendMessage, user))
                        .build();

                MessageEntity messageEntity = MessageEntity.builder()
                        .dateTime(LocalDateTime.now())
                        .fromId(userSendMessage.getId())
                        .fromName(userSendMessage.getUsername())
                        .message("Hola, ¿cómo estás?, soy " + userSendMessage.getUsername())
                        .directMessage(helloDM)
                        .build();

                helloDM.addMessage(messageEntity);

                userSendMessage.getDirectMessages().add(helloDM);
                user.getDirectMessages().add(helloDM);

                // RESPONSE

                DirectMessageEntity responseDM = DirectMessageEntity.builder()
                        .participants(List.of(userSendMessage, user))
                        .build();

                MessageEntity messageEntityResponse = MessageEntity.builder()
                        .dateTime(LocalDateTime.now())
                        .fromId(user.getId())
                        .fromName(user.getUsername())
                        .message("Estoy muy bien!, gracias por preguntar. ¿De dónde eres?")
                        .directMessage(responseDM)
                        .build();

                responseDM.addMessage(messageEntityResponse);

                userSendMessage.getDirectMessages().add(responseDM);
                user.getDirectMessages().add(responseDM);
            }
        });

        directMessageRepository.saveAll(userList.stream().flatMap(user -> user.getDirectMessages().stream()).toList());
    }

}

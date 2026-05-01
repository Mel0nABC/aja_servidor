package dev.aja.aja.directmessage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.aja.aja.directmessage.dto.DirectMessageDTO;
import dev.aja.aja.directmessage.dto.DirectMessageNewDTO;
import dev.aja.aja.directmessage.entity.DirectMessageEntity;
import dev.aja.aja.directmessage.entity.MessageEntity;
import dev.aja.aja.directmessage.exception.DirectMessageNotFoundException;
import dev.aja.aja.directmessage.exception.SameUserException;
import dev.aja.aja.directmessage.exception.UserDestinationNotFoundExcepcion;
import dev.aja.aja.directmessage.repository.DirectMessageRepository;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;
import dev.aja.aja.user.service.UserService;

@Service
public class DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public DirectMessageService(DirectMessageRepository directMessageRepository, UserService userService,
            UserRepository userRepository) {
        this.directMessageRepository = directMessageRepository;
        this.userService = userService;
        this.userRepository = userRepository;

    }

    public void addDirectMessage(DirectMessageNewDTO newMessageDTO) {

        UserEntity userFrom = userService.getUserEntityFromActualUserContext();

        if (userFrom.getId().equals(newMessageDTO.idUserTo()))
            throw new SameUserException("No puedes enviarte un mensaje privado a ti mismo");

        Optional<UserEntity> userToOptional = userRepository.findById(newMessageDTO.idUserTo());

        if (userToOptional.isEmpty())
            throw new UserDestinationNotFoundExcepcion("El usuario de destino del mensaje no existe");

        UserEntity userTo = userToOptional.get();

        Optional<DirectMessageEntity> dmOptional = userFrom.getDirectMessages().stream()
                .filter(dm -> dm.getParticipants().stream()
                        .anyMatch(user -> user.getId().equals(userTo.getId()) & !user.getId().equals(userFrom.getId())))
                .findFirst();

        MessageEntity messageEntity = MessageEntity.builder()
                .dateTime(LocalDateTime.now())
                .fromId(userFrom.getId())
                .fromName(userFrom.getUsername())
                .message(newMessageDTO.text())

                .build();

        // Conversation not exist
        if (dmOptional.isEmpty()) {

            DirectMessageEntity directMessageEntity = DirectMessageEntity.builder()
                    .participants(List.of(userFrom, userTo))
                    .build();

            messageEntity.setDirectMessage(directMessageEntity);

            directMessageEntity.addMessage(messageEntity);

            userFrom.getDirectMessages().add(directMessageEntity);
            userTo.getDirectMessages().add(directMessageEntity);

        } else {

            DirectMessageEntity directMessageEntity = dmOptional.get();
            messageEntity.setDirectMessage(directMessageEntity);
            directMessageEntity.addMessage(messageEntity);

        }

        directMessageRepository.saveAll(
                List.of(userFrom, userTo).stream().flatMap(user -> user.getDirectMessages().stream()).toList());

    }

    public DirectMessageDTO getDirectMessage(Long otherUserId) {

        UserEntity userEntity = userService.getUserEntityFromActualUserContext();

        Optional<UserEntity> otherUserOptional = userRepository.findById(otherUserId);

        if (otherUserOptional.isEmpty())
            throw new UserDestinationNotFoundExcepcion("El usuario del que busca una conversación no existe");

        UserEntity otherUser = otherUserOptional.get();

        Optional<DirectMessageEntity> dmOptional = userEntity.getDirectMessages().stream()
                .filter(dm -> dm.getParticipants().stream().anyMatch(user -> user.getId().equals(otherUserId)))
                .findFirst();

        if (dmOptional.isEmpty())
            throw new DirectMessageNotFoundException("No tienes una conversación con el usuario solicitado");

        return dmOptional.get().toDTO();
    }

    public void delDirectMessage(Long otherUserId) {
        System.out.println("ELIMINAMOS CONVERSACIÓN DE DOS USUARIOS");
    }

}

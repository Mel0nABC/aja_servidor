package dev.aja.aja.directmessage.dto;

import java.util.List;

import dev.aja.aja.user.dto.UserEntityDTO;
import lombok.Builder;

/**
 * Record para proporcionar información simplificada de DirectMessageEntity
 */
@Builder
public record DirectMessageDTO(
        Long id,
        List<UserEntityDTO> participants,
        List<MessageEntityDTO> messages) {

}

package dev.aja.aja.directmessage.dto;

import java.util.List;

import lombok.Builder;

/**
 * Record para proporcionar información simplificada de DirectMessageEntity
 */
@Builder
public record DirectMessageDTO(
        Long id,
        List<String> participants,
        List<MessageEntityDTO> messages) {

}

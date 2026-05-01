package dev.aja.aja.directmessage.dto;

import java.time.LocalDateTime;

import lombok.Builder;

/**
 * Record para enviar información simplificada al front
 */
@Builder
public record MessageEntityDTO(
        Long id,
        LocalDateTime dateTime,
        Long fromId,
        String fromName,
        String message) {

}

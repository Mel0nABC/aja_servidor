package dev.aja.aja.directmessage.dto;

import lombok.Builder;

/**
 * Record/DTO para enviar nuevos mensajes
 */
@Builder
public record DirectMessageNewDTO(
                Long idUserTo,
                String text) {

}

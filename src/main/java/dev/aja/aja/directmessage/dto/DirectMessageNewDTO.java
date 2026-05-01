package dev.aja.aja.directmessage.dto;

/**
 * Record/DTO para enviar nuevos mensajes
 */
public record DirectMessageNewDTO(
                Long idUserTo,
                String text) {

}

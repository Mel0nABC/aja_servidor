package dev.aja.aja.forumstatus.dto;

/**
 * Record para enviar info básica de dónde está escribiendo el usuario y qué
 * usuario
 */
public record NotifyStatusDTO(
                Long userId,
                String username,
                Long topicId,
                String topicTitle) {

}

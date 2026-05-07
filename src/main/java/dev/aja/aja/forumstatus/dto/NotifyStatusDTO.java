package dev.aja.aja.forumstatus.dto;

import java.time.Instant;

import lombok.Builder;

/**
 * Record para enviar info básica de dónde está escribiendo el usuario y qué
 * usuario
 */
@Builder
public record NotifyStatusDTO(
                Long userId,
                String username,
                Long topicId,
                String topicTitle) {

}

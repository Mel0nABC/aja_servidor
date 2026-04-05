package dev.aja.aja.topic.dto;

import lombok.Builder;

/**
 * DTO para simplificar el editar un TopicEntity
 */
@Builder
public record TopicEditDTO(
                Long id,
                String title,
                Long currentForumId,
                Long newForumId) {

}

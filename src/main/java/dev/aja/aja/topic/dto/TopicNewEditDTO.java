package dev.aja.aja.topic.dto;

import lombok.Builder;

/**
 * DTO para simplificar el añadir un nuevo TopicEntity
 */
@Builder
public record TopicNewEditDTO(
        String title,
        Long forumId) {

}

package dev.aja.aja.topic.dto;

import lombok.Builder;

/**
 * DTO para simplificar el añadir un nuevo TopicEntity
 * 
 * @param title   título del topic
 * @param forumId id del forum al que pertenece el topic
 */
@Builder
public record TopicNewEditDTO(
                String title,
                Long forumId) {

}

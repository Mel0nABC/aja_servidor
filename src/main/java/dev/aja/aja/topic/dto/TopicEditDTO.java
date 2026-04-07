package dev.aja.aja.topic.dto;

import lombok.Builder;

/**
 * DTO para simplificar el editar un TopicEntity
 * 
 * @param id             id del topic
 * @param title          título del topic
 * @param currentForumId el id del forum al que pertenece este topic
 * @param newForumId     id de un posible nuevo forum, si el topic se cambia de
 *                       forum
 */
@Builder
public record TopicEditDTO(
        Long id,
        String title,
        Long currentForumId,
        Long newForumId) {

}

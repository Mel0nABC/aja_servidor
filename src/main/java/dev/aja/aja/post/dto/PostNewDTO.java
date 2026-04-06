package dev.aja.aja.post.dto;

import lombok.Builder;

/**
 * DTO para simplificar la obtención de datos para crear un nuevo Post
 */
@Builder
public record PostNewDTO(
        String text,
        Long topicId) {

}

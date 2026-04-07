package dev.aja.aja.post.dto;

import lombok.Builder;

/**
 * DTO para simplificar la obtención de datos para crear un nuevo Post
 * 
 * @param text    texto contenido del post original
 * @param topicId id del topic al que pertenece el post
 */
@Builder
public record PostNewDTO(
                String text,
                Long topicId) {

}

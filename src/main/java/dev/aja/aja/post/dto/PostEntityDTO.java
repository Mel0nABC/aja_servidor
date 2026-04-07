package dev.aja.aja.post.dto;

import java.time.LocalDate;

import dev.aja.aja.topic.dto.TopicEntityDTO;
import dev.aja.aja.user.dto.UserEntityDTO;
import lombok.Builder;

/**
 * Datos simplificados de post para enviar al cliente
 * 
 * @param id               id del PostEntity original
 * @param messageNumber    número de mensaje respecto a todos los que hjay en el
 *                         Topic padre
 * @param user             usuario que ha generado el Post
 * @param text             texto contenido del post
 * @param creationDate     fecha de creación
 * @param lastModification fecha última edición
 * @param topic            topic al que pertenece este post
 */
@Builder
public record PostEntityDTO(
        Long id,
        Long messageNumber,
        UserEntityDTO user,
        String text,
        LocalDate creationDate,
        LocalDate lastModification,
        TopicEntityDTO topic) {

}

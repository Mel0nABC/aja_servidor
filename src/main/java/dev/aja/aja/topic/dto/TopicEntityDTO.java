package dev.aja.aja.topic.dto;

import java.time.LocalDate;

import dev.aja.aja.forum.dto.ForumEntityDTO;
import dev.aja.aja.user.dto.UserEntityDTO;
import lombok.Builder;

/**
 * DTO para enviar la información resumida de TopicEntity
 * 
 * @param id               id del topic original
 * @param title            título del topic
 * @param creationDate     fecha de creación
 * @param lastModification fecha última modificación
 * @param userOwner        usuario que creó el topic
 * @param forum            forum al que pertenece el topic
 */
@Builder
public record TopicEntityDTO(
                Long id,
                String title,
                LocalDate creationDate,
                LocalDate lastModification,
                UserEntityDTO userOwner,
                ForumEntityDTO forum

) {

}
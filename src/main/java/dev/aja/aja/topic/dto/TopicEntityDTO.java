package dev.aja.aja.topic.dto;

import java.time.LocalDate;

import dev.aja.aja.forum.dto.ForumEntityDTO;
import dev.aja.aja.user.dto.UserEntityDTO;
import lombok.Builder;

/**
 * DTO para enviar la información resumida de TopicEntity
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
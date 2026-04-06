package dev.aja.aja.post.dto;

import java.time.LocalDate;

import dev.aja.aja.topic.dto.TopicEntityDTO;
import dev.aja.aja.user.dto.UserEntityDTO;
import lombok.Builder;

/**
 * Datos simplificados de post para enviar al cliente
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

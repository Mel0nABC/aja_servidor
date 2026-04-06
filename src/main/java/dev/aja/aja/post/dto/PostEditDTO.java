package dev.aja.aja.post.dto;

import lombok.Builder;

/**
 * Dto para editar PostEntity
 */
@Builder
public record PostEditDTO(
        Long id,
        String text) {

}

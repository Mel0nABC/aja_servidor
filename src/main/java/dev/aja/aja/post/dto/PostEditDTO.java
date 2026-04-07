package dev.aja.aja.post.dto;

import lombok.Builder;

/**
 * Dto para editar PostEntity
 * 
 * @param id    id del PostEntity original
 * @param text título del PostEntity original
 */
@Builder
public record PostEditDTO(
                Long id,
                String text) {

}

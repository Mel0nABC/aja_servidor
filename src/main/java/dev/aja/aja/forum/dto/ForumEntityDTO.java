package dev.aja.aja.forum.dto;

import lombok.Builder;

/**
 * Dto para crear nuevos ForumEntity
 * 
 * @param id    id del ForumEntity original
 * @param title título del ForumEntity original
 */
@Builder
public record ForumEntityDTO(
        Long id,
        String title) {
}
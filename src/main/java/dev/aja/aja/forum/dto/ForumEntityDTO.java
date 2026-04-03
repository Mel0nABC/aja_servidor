package dev.aja.aja.forum.dto;

import lombok.Builder;

/**
 * Dto para crear nuevos ForumEntity
 */
@Builder
public record ForumEntityDTO(
                Long id,
                String title) {
}
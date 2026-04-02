package dev.aja.aja.topic.dto;

import lombok.Builder;

/**
 * Dto para crear nuevos ForumEntity
 */
@Builder
public record ForumEntityNewDTO(
        String title,
        Long forumId) {
}
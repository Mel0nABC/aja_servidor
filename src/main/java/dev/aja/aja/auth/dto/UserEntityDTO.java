package dev.aja.aja.auth.dto;

import lombok.Builder;

/**
 * Record para proporcionar información simplificada al front de UserEntity
 */
@Builder
public record UserEntityDTO(
                Long id,
                String username,
                String email,
                String role,
                boolean isActive) {

}

package dev.aja.aja.auth.dto;

import dev.aja.aja.auth.RoleEnum;
import lombok.Builder;

/**
 * Record para proporcionar información simplificada al front de UserEntity
 */
@Builder
public record UserEntityDTO(
                Long id,
                String username,
                String email,
                RoleEnum role,
                boolean isActive) {

}

package dev.aja.aja.user.dto;

import lombok.Builder;

/**
 * Dto para enviar lista para poder hacer privados (DM's)
 */
@Builder
public record UserEntityDmDTO(
        Long id,
        String username) {

}

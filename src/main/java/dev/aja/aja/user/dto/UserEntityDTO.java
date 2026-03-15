package dev.aja.aja.user.dto;

import lombok.Builder;

/**
 * Record para proporcionar información simplificada de UserEntity al front
 * 
 * @param id,       identificador único del usuario
 * @param username, nombre de usuario, único, del usuario
 * @param email,    email del usuario
 * @param role,     role asignado al usuario, provienen de RoleEnum
 * @param isActive, estado de la cuenta del usuario
 */
@Builder
public record UserEntityDTO(
        Long id,
        String username,
        String email,
        String role,
        boolean isActive) {

}

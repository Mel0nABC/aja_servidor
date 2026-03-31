package dev.aja.aja.user.dto;

import lombok.Builder;

/**
 * Record para obtener información necesaria para registrar un nuevo usuario
 * 
 * @param username, nombre del nuevo usuario
 * @param password, password del nuevo usuario
 * @param email,    email del nuevo usuario
 */
@Builder
public record UserEntityNewDTO(
        String username,
        String password,
        String email) {

}

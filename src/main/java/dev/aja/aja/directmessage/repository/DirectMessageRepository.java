package dev.aja.aja.directmessage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.directmessage.entity.DirectMessageEntity;

/**
 * Interface para acceder a los métodos de consulta de la base de datos de las
 * entidades de DirectMessageEntity
 */
public interface DirectMessageRepository extends JpaRepository<DirectMessageEntity, Long> {

}

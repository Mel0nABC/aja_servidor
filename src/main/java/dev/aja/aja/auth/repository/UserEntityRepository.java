package dev.aja.aja.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.aja.aja.auth.entity.UserEntity;

/**
 * Interface para acceder a los métodos de consulta de la base de datos de las
 * entidades de UserEntity
 */
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    // https://docs.spring.io/spring-data/jpa/reference/data-commons/repositories/definition.html
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String mail);
}

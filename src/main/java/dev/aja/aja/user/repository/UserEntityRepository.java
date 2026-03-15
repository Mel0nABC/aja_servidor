package dev.aja.aja.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.aja.aja.user.entity.UserEntity;

/**
 * Interface para acceder a los métodos de consulta de la base de datos de las
 * entidades de UserEntity
 */
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    // https://docs.spring.io/spring-data/jpa/reference/data-commons/repositories/definition.html

    /**
     * Para obtener la información del usuario mediante su nombre de usuario
     * 
     * @param username nombre de usuario para buscar
     * @return devolvemos objeto tipo Optional, que puede estar vacio o contener el
     *         UserEntity, pero nunca será null
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Para obtener la información del usuario mediante su correo electrónico
     * 
     * @param mail correo electrónico del usuario para buscar
     * @return devolvemos objeto tipo Optional, que puede estar vacio o contener el
     *         UserEntity, pero nunca será null
     */
    Optional<UserEntity> findByEmail(String mail);
}

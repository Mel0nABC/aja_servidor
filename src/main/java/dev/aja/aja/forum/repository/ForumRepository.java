package dev.aja.aja.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.forum.entity.ForumEntity;

/**
 * Interface para acceder a los métodos de consulta de la base de datos de las
 * entidades de ForumEntity
 */
public interface ForumRepository extends JpaRepository<ForumEntity, Long> {

    /**
     * Devolvemos optional que puede o no contener un ForumEntity con el título
     * proporcionado
     * 
     * @param title título del Forum a buscar
     * @return Optional, puede o no contener un ForumEntity
     */
    Optional<ForumEntity> findByTitle(String title);

    /**
     * Comprobar si un ForumEntity existe.
     * 
     * @param title nombre/título del Forum
     * @return true si existe false si no existe
     */
    Boolean existsByTitle(String title);

}

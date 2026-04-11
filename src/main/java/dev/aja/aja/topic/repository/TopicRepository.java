package dev.aja.aja.topic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.topic.entity.TopicEntity;

/**
 * Interface para acceder a los métodos de consulta de la base de datos de las
 * entidades de TopicEntity
 */
public interface TopicRepository extends JpaRepository<TopicEntity, Long> {

    /**
     * Devolvemos optional que puede o no contener un TopicEntity con el título
     * proporcionado
     * 
     * @param title título del topic a buscar
     * @return Optional, puede o no contener un TopicEntity
     */
    Optional<TopicEntity> findByTitle(String title);

    /**
     * Devolvemos una lista de todos los TopicEntity que el usuario es el owner
     * 
     * @param id del usuario
     * @return Lista de TopicEntity
     */
    List<TopicEntity> findAllByUserOwnerId(Long id);

}

package dev.aja.aja.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.post.entity.PostEntity;

/**
 * Interface para acceder a los métodos de consulta de la base de datos de las
 * entidades de PostEntity
 */
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    /**
     * Devolvemos optional que puede o no contener un PostEntity con el texto
     * proporcionado
     * 
     * @param text texto del post a buscar
     * @return Optional, puede o no contener un PostEntity
     */
    Optional<PostEntity> findByText(String text);

}

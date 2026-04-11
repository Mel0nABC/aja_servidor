package dev.aja.aja.post.entity;

import java.time.LocalDate;

import dev.aja.aja.post.dto.PostEntityDTO;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase para los mensaje que se podrán postear en TopicEntity
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private Long messageNumber = 0L;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity user;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private LocalDate lastModification;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private TopicEntity topic;

    /**
     * Constructor vacío requerido por JPA.
     */
    public PostEntity() {

    }

    /**
     * Método para convertir un PostEntity en un PostEntityDTO simplificando alguna
     * información
     * 
     * @return PostEntityDTO
     */
    public PostEntityDTO toDTO() {
        return PostEntityDTO.builder()
                .id(this.id)
                .messageNumber(this.messageNumber)
                .user(this.user.toDTO())
                .text(this.text)
                .creationDate(creationDate)
                .lastModification(lastModification)
                .topic(this.topic.toDTO())
                .build();
    }

}
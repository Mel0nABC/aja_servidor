package dev.aja.aja.forum.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dev.aja.aja.forum.dto.ForumEntityDTO;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.topic.exception.TopicAlreadyExistException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase forum, son la temáticas principales que generaran el contenido del foro
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "forums")
public class ForumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private LocalDate lastModification;

    @Builder.Default
    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TopicEntity> topicList = new ArrayList<>();

    public ForumEntityDTO toDTO() {
        return ForumEntityDTO.builder()
                .id(this.id)
                .title(this.title)
                .build();
    }

    public void addTopic(TopicEntity topicEntity) {

        Boolean result = topicList.stream().anyMatch(topic -> topic.getTitle().equals(topicEntity.getTitle()));

        if (result)
            throw new TopicAlreadyExistException("El topic con el títutlo <" + topicEntity.getTitle() + "> ya existe");

        topicList.add(topicEntity);
        topicEntity.setForum(this);
    }

    public void delTopic(TopicEntity topicEntity) {
        topicList.remove(topicEntity);
        topicEntity.setForum(null);
    }
}
package dev.aja.aja.forum.entity;

import java.time.LocalDate;
import java.util.List;

import dev.aja.aja.forum.dto.ForumEntityDTO;
import dev.aja.aja.topic.entity.TopicEntity;
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

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL)
    private List<TopicEntity> topicList;

    public ForumEntityDTO toDTO() {
        return ForumEntityDTO.builder()
                .id(this.id)
                .title(this.title)
                .build();
    }
}
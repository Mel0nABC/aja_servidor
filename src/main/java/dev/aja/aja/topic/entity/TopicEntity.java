package dev.aja.aja.topic.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.post.entity.PostEntity;
import dev.aja.aja.topic.dto.TopicEntityDTO;
import dev.aja.aja.user.entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Topic son los temas que habrán dentro de ForumEntity
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "topics")
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private LocalDate lastModification;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userOwner;

    @ManyToOne
    @JoinColumn(name = "forum_id", nullable = false)
    private ForumEntity forum;

    @Builder.Default
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PostEntity> postList = new ArrayList<>();

    public void addPost(PostEntity postEntity) {
        postList.add(postEntity);
        postEntity.setTopic(this);
    }

    public void delPost(PostEntity postEntity) {
        postList.remove(postEntity);
        postEntity.setTopic(null);
    }

    /**
     * Método para crear un DTO, un resumen de TopicEntity para enviar por red
     * 
     * @return DTO resumen de TopicEntity
     */
    public TopicEntityDTO toDTO() {
        return TopicEntityDTO.builder()
                .id(this.id)
                .title(this.title)
                .creationDate(this.creationDate)
                .lastModification(this.lastModification)
                .userOwner(this.userOwner.toDTO())
                .forum(this.forum.toDTO())
                .build();
    }
}
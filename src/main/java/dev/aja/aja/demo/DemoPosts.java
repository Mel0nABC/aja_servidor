package dev.aja.aja.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.post.entity.PostEntity;
import dev.aja.aja.post.repository.PostRepository;
import dev.aja.aja.user.entity.UserEntity;

/**
 * Clase para generar cientos de PostEntity de ejemplo, para tener contenido a
 * la hora de realizar pruebas con los clientes
 */
public class DemoPosts {

    /**
     * Constructor de DemoPosts, el mismoya crea, añade y guarda en la bbdd los post
     * 
     * @param forumList       lista de forums creados
     * @param userList        lista de usuarios creados
     * @param forumRepository repositorio para hacer peticiones a la base de datos
     *                        tabla forums
     * @param postRepository  repositorio para hacer peticiones a la base de datos
     *                        tabla posts
     */
    public DemoPosts(List<ForumEntity> forumList, List<UserEntity> userList, ForumRepository forumRepository,
            PostRepository postRepository) {

        forumList.forEach(forum -> {
            ForumEntity forumEntity = forumRepository.findById(forum.getId()).get();
            forumEntity.getTopicList().forEach(topic -> {
                Long count = 1L;
                List<PostEntity> postList = new ArrayList<>();
                for (UserEntity user : userList) {
                    PostEntity postEntity = PostEntity.builder()
                            .text("texto de ejemplo escrito por el usuario: " + user.getUsername() + ", en el topic: "
                                    + topic.getTitle() + ", dentro del forum: " + forumEntity.getTitle())
                            .messageNumber(count)
                            .user(user)
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .build();
                    postList.add(postEntity);
                    topic.addPost(postEntity);
                    count++;
                }
                postRepository.saveAll(postList);
            });

        });
        forumRepository.saveAll(forumList);
    }

}

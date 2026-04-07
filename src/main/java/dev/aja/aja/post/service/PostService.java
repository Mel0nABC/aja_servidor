package dev.aja.aja.post.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.aja.aja.post.dto.PostEditDTO;
import dev.aja.aja.post.dto.PostEntityDTO;
import dev.aja.aja.post.dto.PostNewDTO;
import dev.aja.aja.post.entity.PostEntity;
import dev.aja.aja.post.exception.PostNotFoundException;
import dev.aja.aja.post.repository.PostRepository;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.topic.exception.TopicNotFoundException;
import dev.aja.aja.topic.repository.TopicRepository;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.exception.UserInvalidToEditInformation;
import dev.aja.aja.user.service.UserService;

/**
 * Service para gestionar las consultas realizadas al PostController y guardar,
 * editar, eliminar o devolver la información requerida desde el mismo
 */
@Service
public class PostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final UserService userService;

    /**
     * Constructor para inyección de dependencias
     * 
     * @param postRepository  repositorio para hacer CRUD a la base de datos a la
     *                        tabla posts
     * @param topicRepository repositorio para hacer CRUD a la base de datos a la
     *                        tabla topics
     * @param userService     acceso a la lógica de neogocio de usuarios
     */
    public PostService(PostRepository postRepository, TopicRepository topicRepository, UserService userService) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
        this.userService = userService;
    }

    /**
     * Añadimos una nueva entidad PostEntity. Se comprueba que el Topic de destino
     * exista
     * 
     * @param postNewDTO datos mínimos para poder añadi un nuevo post
     */
    public void addPost(PostNewDTO postNewDTO) {

        Optional<TopicEntity> topicOptional = topicRepository.findById(postNewDTO.topicId());

        if (topicOptional.isEmpty())
            throw new TopicNotFoundException("El topic al que quieres añadir el post no existe");

        TopicEntity topicEntity = topicOptional.get();

        UserEntity userEntity = userService.getUserEntityFromActualUserContext();

        PostEntity postEntity = PostEntity.builder()
                .messageNumber((topicEntity.getPostList().size() + 1L))
                .user(userEntity)
                .text(postNewDTO.text())
                .creationDate(LocalDate.now())
                .lastModification(LocalDate.now())
                .build();

        topicEntity.addPost(postEntity);

        topicRepository.save(topicEntity);
    }

    /**
     * Editamos un post utilizando un DTO con la información básica, PostEditDTO. Se
     * valida que el usuario que edita el post sea el mismo que creó el post
     * 
     * @param postEditDTO parámetro con la información básica para editar un post
     */
    public void editPost(PostEditDTO postEditDTO) {

        Optional<PostEntity> posOptional = postRepository.findById(postEditDTO.id());

        if (posOptional.isEmpty())
            throw new PostNotFoundException("El post que quieres editar no existe");

        PostEntity postEntity = posOptional.get();

        UserEntity userEntity = userService.getUserEntityFromActualUserContext();

        if (!postEntity.getUser().getId().equals(userEntity.getId()))
            throw new UserInvalidToEditInformation("No tienes permiso para editar este post");

        postEntity.setText(postEditDTO.text());
        postEntity.setLastModification(LocalDate.now());

        postRepository.save(postEntity);
    }

    /**
     * Eliminamos post del Topic al que pertenece. Se valida que el usuario que lo
     * elimina sea el que creó el post. Si quien quiere eliminar el post es role
     * ADMIN esta validación se salta y se procede
     * 
     * @param id id del post a eliminar
     */
    public void delPost(Long id) {
        Optional<PostEntity> posOptional = postRepository.findById(id);

        if (posOptional.isEmpty())
            throw new PostNotFoundException("El post que quieres eliminar no existe");

        PostEntity postEntity = posOptional.get();

        UserEntity userEntity = userService.getUserEntityFromActualUserContext();

        if (!userEntity.getRole().equals(RoleEnum.ADMIN.getName()))
            if (!postEntity.getUser().getId().equals(userEntity.getId()))
                throw new UserInvalidToEditInformation("No tienes permiso para eliminar este post");

        TopicEntity topicEntity = postEntity.getTopic();

        topicEntity.delPost(postEntity);

        topicRepository.save(topicEntity);
    }

    /**
     * Para obtener toda la información de un PostEntity para poder editarlo
     * 
     * @param id id del Post que se requiere la información
     * @return PostEntityDTO con información simplificada del PostEntity
     */
    public PostEntityDTO getPost(Long id) {
        Optional<PostEntity> posOptional = postRepository.findById(id);

        if (posOptional.isEmpty())
            throw new PostNotFoundException("El post del que quieres obtener toda la información no existe");

        UserEntity userEntity = userService.getUserEntityFromActualUserContext();

        PostEntity postEntity = posOptional.get();

        if (!postEntity.getUser().getId().equals(userEntity.getId()))
            throw new UserInvalidToEditInformation(
                    "No puedes obtener la información para editar este post no eres el creador");

        return posOptional.get().toDTO();
    }

    /**
     * Obtener toda la lista de PostEntity y se devuelve transformándolo a una lista
     * de PostEntityDTO
     * 
     * @return lista de PostEntityDTO
     */
    public List<PostEntityDTO> getAllPost() {
        return postRepository.findAll().stream().map(post -> post.toDTO()).toList();
    }

}

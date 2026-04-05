package dev.aja.aja.topic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.exception.ForumNotFoundException;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.topic.dto.TopicEditDTO;
import dev.aja.aja.topic.dto.TopicNewEditDTO;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.topic.exception.TopicAlreadyExistException;
import dev.aja.aja.topic.exception.TopicNotFoundException;
import dev.aja.aja.topic.repository.TopicRepository;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.service.UserService;

/**
 * Service para gestionar las consultas realizadas al TopicController y guardar,
 * editar, eliminar o devolver la información requerida desde el mismo
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final ForumRepository forumRepository;
    private final UserService userService;

    public TopicService(TopicRepository topicRepository, ForumRepository forumRepository, UserService userService) {
        this.topicRepository = topicRepository;
        this.forumRepository = forumRepository;
        this.userService = userService;

    }

    /**
     * Añadimos una nueva entidad TopicEntity. Se comprueba que no exista, si existe
     * lanza excepción indicándolo si no, añade el topic al forum que toca y lo
     * guarda en la base de datos
     * 
     * @param topicNewEditDTO información básica para añadir un nuevo TopicEntity
     */
    public void addTopic(TopicNewEditDTO topicNewEditDTO) {

        Optional<ForumEntity> forumOptional = forumRepository.findById(topicNewEditDTO.forumId());

        if (forumOptional.isEmpty())
            throw new ForumNotFoundException("No se ha localizado el forum donde se desea añadir un nuevo topic");

        ForumEntity forumEntity = forumOptional.get();

        Boolean result = forumEntity.getTopicList().stream()
                .anyMatch(topic -> topic.getTitle().equals(topicNewEditDTO.title()));

        if (result)
            throw new TopicAlreadyExistException("Ya existe un topic con el título proporcionado");

        UserEntity userEntity = userService.getUserEntityFromActualUserContext();

        TopicEntity topicEntity = TopicEntity.builder()
                .title(topicNewEditDTO.title())
                .userOwner(userEntity)
                .creationDate(LocalDate.now())
                .lastModification(LocalDate.now())
                .build();

        forumEntity.addTopic(topicEntity);

        forumRepository.save(forumEntity);
    }

    /**
     * Eliminamos un topic del forum y guardamos el forum. Esto sólo se puede
     * realizar con un rol ADMIN
     * 
     * @param id id del topic que se desea eliminar
     */
    public void delTopic(Long id) {

        userService.checkRoleAdminFromUserContext();

        Optional<TopicEntity> topicOptional = topicRepository.findById(id);

        if (topicOptional.isEmpty())
            throw new TopicNotFoundException("No se ha localizado el topic que se quiere eliminar");

        TopicEntity topicEntity = topicOptional.get();

        ForumEntity forumEntity = topicEntity.getForum();

        forumEntity.delTopic(topicEntity);

        forumRepository.save(forumEntity);
    }

    public void editTopic(TopicEditDTO topicEditDTO) {

    }

    public TopicEntity getTopic(Long id) {
        return null;
    }

    public List<TopicEntity> getAllTopic() {
        return null;
    }

}

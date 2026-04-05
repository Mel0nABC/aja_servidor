package dev.aja.aja.topic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.exception.ForumNotFoundException;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.topic.dto.TopicEditDTO;
import dev.aja.aja.topic.dto.TopicEntityDTO;
import dev.aja.aja.topic.dto.TopicNewEditDTO;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.topic.exception.TopicAlreadyExistException;
import dev.aja.aja.topic.exception.TopicNotFoundException;
import dev.aja.aja.topic.repository.TopicRepository;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.exception.UserInvalidToEditInformation;
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

    /**
     * Mediante un TopicEditDTO que trae la información justa para poder editar un
     * TopiEntity, realizamos las validaciones correspondientes y editamos si todo
     * es ok
     * 
     * @param topicEditDTO información básica para editar un TopicEntity
     */
    public void editTopic(TopicEditDTO topicEditDTO) {

        Optional<TopicEntity> topicOptional = topicRepository.findById(topicEditDTO.id());

        if (topicOptional.isEmpty())
            throw new TopicNotFoundException("El topic que desea editar no existe");

        TopicEntity topicEntity = topicOptional.get();

        UserEntity userEntity = userService.getUserEntityFromActualUserContext();

        if (!userEntity.getRole().equals(RoleEnum.ADMIN.getName()))
            if (!topicEntity.getUserOwner().getId().equals(userEntity.getId()))
                throw new UserInvalidToEditInformation("No puedes editar este topic");

        Optional<TopicEntity> titleTopicOptional = topicRepository.findByTitle(topicEditDTO.title());

        if (!topicEditDTO.title().equals(topicEntity.getTitle()))
            if (!titleTopicOptional.isEmpty())
                throw new TopicAlreadyExistException("El nuevo título del topic ya existe");

        topicEntity.setTitle(topicEditDTO.title());
        topicEntity.setLastModification(LocalDate.now());

        if (!topicEntity.getForum().getId().equals(topicEditDTO.newForumId())) {

            Optional<ForumEntity> newForumEntityOptional = forumRepository.findById(topicEditDTO.newForumId());

            if (newForumEntityOptional.isEmpty())
                throw new ForumNotFoundException("El nuevo forum elegido no existe");

            ForumEntity oldForumEntity = topicEntity.getForum();

            oldForumEntity.delTopic(topicEntity);

            newForumEntityOptional.get().addTopic(topicEntity);

            forumRepository.saveAll(List.of(oldForumEntity, newForumEntityOptional.get()));
        } else {
            topicRepository.save(topicEntity);
        }
    }

    /**
     * Mediante la id obtenemos toda la información de un TopicEntity y lo
     * tranformamos a TopicEntityDTO
     * 
     * @param id del topic para buscarlo.
     * @return entidad del tipo TopicEntityDTO, resument de TopicEntity
     */
    public TopicEntityDTO getTopic(Long id) {

        Optional<TopicEntity> topicOptional = topicRepository.findById(id);

        if (topicOptional.isEmpty())
            throw new TopicNotFoundException("El topic seleccionado no existe");

        return topicOptional.get().toDTO();
    }

    /**
     * Obtenemos la lista de todas las entidades del tipo TopicEntity y las
     * transformamos al tipo TopicEntityDTO
     * 
     * @return lista de entidades del tipo TopicEntityDTO
     */
    public List<TopicEntityDTO> getAllTopic() {
        return topicRepository.findAll().stream().map(topic -> topic.toDTO()).toList();
    }

}

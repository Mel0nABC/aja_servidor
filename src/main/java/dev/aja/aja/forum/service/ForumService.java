package dev.aja.aja.forum.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.exception.ForumAlreadyExistException;
import dev.aja.aja.forum.exception.ForumNotFoundException;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.topic.dto.ForumEntityNewDTO;
import dev.aja.aja.user.service.UserService;

/**
 * Clase que se declara como servicio para la carga durante el inicio de Spring
 * Boot. Dispondremos de todas las funciones para acceder a la información de
 * Forums en la base de datos y realizar acciones sobre el contexto de la
 * sesión actual, incluye la lógicade negocio que sea necesaria cara a Forums
 */
@Service
public class ForumService {

    private final ForumRepository forumRepository;
    private final UserService userService;

    /**
     * 
     * Constructor con la inyecciónd de dependencias necesarias para el servicio
     * 
     * @param forumRepository repositorio que nos da acceso a la tabla de
     *                        forums en la base de datos
     * @param userService     servicio que nos da acceso a toda la lógica de negocio
     *                        de usuarios
     */
    public ForumService(ForumRepository forumRepository, UserService userService) {
        this.forumRepository = forumRepository;
        this.userService = userService;
    }

    /**
     * Añadir un nuevo foro, se realizan validaciones que el usuario que lo hace sea
     * role ADMIN, que no exista su nombre
     * 
     * @param forumEntityNewDTO información para poder crear un nuevo forum
     */
    public void addForum(ForumEntityNewDTO forumEntityNewDTO) {

        userService.checkRoleAdminFromUserContext();

        checkForumExist(forumEntityNewDTO.title());

        LocalDate now = LocalDate.now();

        forumRepository.save(ForumEntity.builder()
                .title(forumEntityNewDTO.title())
                .creationDate(now)
                .lastModification(now)
                .build());
    }

    /**
     * Eliminamos un foro, comprobamos que el role sea ADMIN y que este forum exista
     * 
     * @param id id del forum a eliminar
     */
    public void delForum(Long id) {

        userService.checkRoleAdminFromUserContext();

        if (!forumRepository.existsById(id)) {
            throw new ForumNotFoundException("No se ha localizado el forum que se quiere eliminar");
        }

        forumRepository.deleteById(id);
    }

    /**
     * Comprobar si el título del forum ya existe. Si existe lanza una excepción
     * 
     * @param title titulo del forum a buscar
     */
    public void checkForumExist(String title) {
        if (forumRepository.existsByTitle(title)) {
            throw new ForumAlreadyExistException("El título del forum que quieres añadir ya existe");
        }
    }

}

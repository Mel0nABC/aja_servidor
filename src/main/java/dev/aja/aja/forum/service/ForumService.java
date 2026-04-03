package dev.aja.aja.forum.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.aja.aja.forum.dto.ForumEntityDTO;
import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.exception.ForumAlreadyExistException;
import dev.aja.aja.forum.exception.ForumNotFoundException;
import dev.aja.aja.forum.repository.ForumRepository;
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
    public void addForum(ForumEntityDTO forumEntityNewDTO) {

        this.userService.checkRoleAdminFromUserContext();

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

        this.userService.checkRoleAdminFromUserContext();

        if (!forumRepository.existsById(id)) {
            throw new ForumNotFoundException("No se ha localizado el forum que se quiere eliminar");
        }

        forumRepository.deleteById(id);
    }

    /**
     * Editar ForumEntit, se recibe con ForumEntityDTO sólo con el id y el nuevo
     * título. Se obtiene de la base de datos dicho ForumEntity que corresponde al
     * id de ForumEntityDTO y editamos título y la fecha de modificación.
     * Sólo administradores pueden editar.
     * 
     * @param forumEditDTO Información que se va a modificar
     */
    public void editForum(ForumEntityDTO forumEditDTO) {

        this.userService.checkRoleAdminFromUserContext();

        Optional<ForumEntity> forumOptional = forumRepository.findById(forumEditDTO.id());

        if (forumOptional.isEmpty())
            throw new ForumNotFoundException("El forum que quiere editar no existe");

        ForumEntity forumToEdit = forumOptional.get();

        Optional<ForumEntity> forumOptionalTitle = forumRepository.findByTitle(forumEditDTO.title());

        if (!forumOptionalTitle.isEmpty())
            if (!forumToEdit.getId().equals(forumOptionalTitle.get().getId()))
                throw new ForumAlreadyExistException("El título que quieres asignar ya existe en otro foro");

        forumToEdit.setTitle(forumEditDTO.title());
        forumToEdit.setLastModification(LocalDate.now());

        forumRepository.save(forumToEdit);
    }

    /**
     * Para obtener toda la información de un forum
     * 
     * @param id id del forum requerido
     * @return retornamos una entidad ForumEntity si existe dicho forum o una
     *         excepción ForumNotFoundException si no existe
     */
    public ForumEntity getForum(Long id) {

        Optional<ForumEntity> forumOptional = forumRepository.findById(id);

        if (forumOptional.isEmpty())
            throw new ForumNotFoundException("No se ha localizado la información del forum requerido");

        return forumOptional.get();
    }

    /**
     * Para obtener todos los forums que existan
     * 
     * @return devuelve una lista de ForumEntity con o sin entidades
     */
    public List<ForumEntity> getAllForums() {
        return forumRepository.findAll();
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

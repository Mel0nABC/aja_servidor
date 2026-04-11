package dev.aja.aja.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.aja.aja.post.entity.PostEntity;
import dev.aja.aja.post.repository.PostRepository;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.topic.repository.TopicRepository;
import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.dto.UserEntityDTO;
import dev.aja.aja.user.dto.UserEntityNewDTO;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.exception.EmailAlreadyExistException;
import dev.aja.aja.user.exception.UserInvalidRoleException;
import dev.aja.aja.user.exception.UserInvalidToEditInformation;
import dev.aja.aja.user.exception.UsernameAlreadyExistException;
import dev.aja.aja.user.repository.UserRepository;

/**
 * Clase que se declara como servicio para la carga durante el inicio de Spring
 * Boot. Dispondremos de todas las funciones para acceder a la información del
 * usuario en la base de datos y realizar acciones sobre el contexto de la
 * sesión actual, incluye la lógicade negocio que sea necesaria cara a usuarios
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userEntityRepository;
    private final TopicRepository topicRepository;
    private final PostRepository postRepository;
    /**
     * Tamaño máximo que puede tener un nombre de usuario
     */
    public static final int USERNAME_SIZE = 20;

    /**
     * Tamaño máximo que puede tener una contraseña
     */
    public static final int PASSWORD_SIZE = 65;

    /**
     * 
     * Constructor con la inyecciónd de dependencias necesarias para el servicio
     * 
     * @param userEntityRepository repositorio que nos da acceso a la tabla de
     *                             usuarios en la base de datos
     */
    public UserService(UserRepository userEntityRepository, TopicRepository topicRepository,
            PostRepository postRepository) {
        this.userEntityRepository = userEntityRepository;
        this.topicRepository = topicRepository;
        this.postRepository = postRepository;
    }

    /**
     * obtenemos toda la información de la entidad del tipo UserEntity del usuario
     * que está con sesión iniciada. Obtenemos el username del contexto actual
     * 
     * @return entidad UserEntity con la información del usuario del contexto
     * 
     * @throws UsernameNotFoundException si el usuario del actual contexto no
     *                                   existe
     */
    public UserEntity getUserEntityFromActualUserContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userEntityRepository.findByUsername(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found: " + username);

        return userOptional.get();
    }

    /**
     * Comprobamos si el usuario del contexto actual es admin
     * 
     * @return devuelve USerEntity si es admin o lanza excepción si no lo es.
     * 
     * @throws UserInvalidRoleException si el usuario del actual contexto no es
     *                                  admin
     */
    public UserEntity checkRoleAdminFromUserContext() {
        UserEntity userContext = getUserEntityFromActualUserContext();

        if (!userContext.getRole().equals(RoleEnum.ADMIN.getName()))
            throw new UserInvalidRoleException();

        return userContext;
    }

    /**
     * Añadimos nuevo usuario,se comprueba que el nombre de usuario y el correo
     * electrónico no existan. Validamos manualmente para evitar excepciones de
     * DataIntegrity
     * 
     * @param userEntityDTO información básica para poder añadir un nuevo UserEntity
     */
    public void addUser(UserEntityNewDTO userEntityDTO) {

        checkArgumentSize(userEntityDTO.username(), userEntityDTO.password());

        Optional<UserEntity> userUsernameOptional = userEntityRepository.findByUsername(userEntityDTO.username());

        if (!userUsernameOptional.isEmpty())
            throw new UsernameAlreadyExistException("El nombre de usuario que quieres añadir ya existe");

        Optional<UserEntity> userMailOptional = userEntityRepository.findByEmail(userEntityDTO.email());

        if (!userMailOptional.isEmpty())
            throw new EmailAlreadyExistException("El email de usuario que quieres añadir ya existe");

        UserEntity userEntity = UserEntity.builder()
                .username(userEntityDTO.username())
                .password(passwordEncoder().encode(userEntityDTO.password()))
                .email(userEntityDTO.email())
                .role(RoleEnum.USER.getName())
                .isActive(true)
                .registerDate(LocalDate.now())
                .build();

        userEntityRepository.save(userEntity);
    }

    /**
     * Eliminar usuario proporcionando su id. Se asigna un usuario denominado Ghost
     * a todos los Topic y Post de dicho usuario
     * 
     * @param id identificador del usuario a eliminar
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public void delUSer(Long id) {

        UserEntity user = getUserEntityFromActualUserContext();

        if (!user.getId().equals(id))
            checkRoleAdminFromUserContext();

        Optional<UserEntity> userEntity = userEntityRepository.findById(id);

        if (userEntity.isEmpty())
            throw new UsernameNotFoundException(null);

        UserEntity userGhostToDeletedUsers = userEntityRepository.findByUsername("Deleted User").get();

        List<TopicEntity> topicList = topicRepository.findAll();

        List<PostEntity> postList = new ArrayList<>();

        topicList.forEach(topic -> {
            if (topic.getUserOwner().getId().equals(id)) {
                topic.setUserOwner(userGhostToDeletedUsers);
            }

            topic.getPostList().forEach(post -> {
                if (post.getUser().getId().equals(id)) {
                    post.setUser(userGhostToDeletedUsers);
                    postList.add(post);
                }
            });

        });

        postRepository.saveAll(postList);

        topicRepository.saveAllAndFlush(topicList);

        userEntityRepository.delete(userEntity.get());
    }

    /**
     * Deshabilitar usuario, sólo permitido por rol ADMIN
     * 
     * @param id id del usuario a deshabilitar
     */
    public void disableUser(Long id) {

        checkRoleAdminFromUserContext();

        Optional<UserEntity> userOptional = userEntityRepository.findById(id);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException(null);

        UserEntity userEntity = userOptional.get();

        userEntity.setIsActive(false);

        userEntityRepository.save(userEntity);

    }

    /**
     * Habilitar usuario, sólo permitido por rol ADMIN
     * 
     * @param id id del usuario a habilitar
     */
    public void enableUser(Long id) {

        checkRoleAdminFromUserContext();

        Optional<UserEntity> userOptional = userEntityRepository.findById(id);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException(null);

        UserEntity userEntity = userOptional.get();

        userEntity.setIsActive(true);

        userEntityRepository.save(userEntity);

    }

    /**
     * Actualizar UserEntity, sólo el propio usuario puede editar su información
     * 
     * @param userEntity userEntity del usuario a actualizar
     * 
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public void editUser(UserEntity userEntity) {

        UserEntity userModifyYourInformation = getUserEntityFromActualUserContext();

        if (!userModifyYourInformation.getId().equals(userEntity.getId()))
            throw new UserInvalidToEditInformation("Sólo puedes editar tu información");

        checkArgumentSize(userEntity.getUsername(), userEntity.getPassword());

        Optional<UserEntity> userEntityDB = userEntityRepository.findById(userEntity.getId());

        if (userEntityDB.isEmpty())
            throw new UsernameNotFoundException(null);

        if (userEntityRepository.existsByUsername(userEntity.getUsername())
                & !userEntityDB.get().getUsername().equals(userEntity.getUsername()))
            throw new UsernameAlreadyExistException("El nombre de usuario elegido ya existe");

        if (userEntityRepository.existsByEmail(userEntity.getEmail())
                & !userEntityDB.get().getEmail().equals(userEntity.getEmail()))
            throw new EmailAlreadyExistException("El correo electrónico elegido ya existe");

        UserEntity user = userEntityDB.get();

        if (passwordEncoder().matches(user.getPassword(), passwordEncoder().encode(userEntity.getPassword())))
            user.setPassword(passwordEncoder().encode(userEntity.getPassword()));

        user.setUsername(userEntity.getUsername());
        user.setEmail(userEntity.getEmail());

        System.out.println("USER: " + user.getUsername());
        System.out.println("PASSWORD: " + user.getPassword());

        userEntityRepository.save(user);
    }

    /**
     * Proporcionamos el UserEntity de usuario que se solicita por su id. Sólo para
     * administrador
     * 
     * @param id UserEntity id
     * 
     * @return Devolvemos un UserEntity si todo ha ido bien
     * 
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public UserEntityDTO getUserDTO(Long id) {

        checkRoleAdminFromUserContext();

        Optional<UserEntity> userOptional = userEntityRepository.findById(id);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("");

        return userOptional.get().toDTO();
    }

    /**
     * Para obtener todos los usuarios, sólo con role dmin
     * 
     * @return lista de usuarios
     */
    public List<UserEntityDTO> getAllUSerDTO() {

        checkRoleAdminFromUserContext();

        return userEntityRepository.findAll().stream().map(UserEntity::toDTO).toList();
    }

    /**
     * Implementación del sistema de codificación de contraseñas.
     * 
     * @return objeto BCryptPasswordEncoder para coficiar contraseñas
     */
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Se comprueba si username o password exceden de un tamaño máximo
     * 
     * @param username nombre de usuario
     * @param password contraseña del usaurio
     */
    public void checkArgumentSize(String username, String password) {

        if (username.length() > USERNAME_SIZE)
            throw new IllegalArgumentException(
                    "El nombre de usuario es demasiado largo, no puede exceder de " + USERNAME_SIZE);

        if (password.length() > PASSWORD_SIZE)

            throw new IllegalArgumentException(
                    "La contraseña es demasiado larga, no puede exceder de " + PASSWORD_SIZE);
    }

}

package dev.aja.aja.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import dev.aja.aja.auth.RoleEnum;
import dev.aja.aja.auth.dto.UserEntityDTO;
import dev.aja.aja.auth.entity.UserEntity;
import dev.aja.aja.auth.exception.UserAlreadyExistException;
import dev.aja.aja.auth.exception.UserInvalidRoleException;
import dev.aja.aja.auth.repository.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Clase que se declara como servicio para la carga durante el inicio de Spring
 * Boot. Dispondremos de todas las funciones para acceder a la información del
 * usuario en la base de datos y realizar acciones sobre el contexto de la
 * sesión actual
 */
@Service
public class AuthService {

    private final UserEntityRepository userEntityRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param userEntityRepository  inyección par el acceso a la base de datos de la
     *                              entidad UserEntity
     * @param authenticationManager inyección para authenticar el usuario
     */
    public AuthService(UserEntityRepository userEntityRepository, AuthenticationManager authenticationManager) {
        this.userEntityRepository = userEntityRepository;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Gestión del login, se comprueba que el usuario existe y se ha proporcionado
     * la contraseña correcta. Si es todo correcto se añade al contexto de la sesión
     * actual y a la sesión HTTP
     * 
     * @param username nombre del usuario que quiere hacer login
     * @param password contraseña del usuario que quiere hacer login
     * @param request  request con la información de la petición HTTP actual,
     *                 headers, body, etcétera.
     * 
     * @return devuelve una entidad UserEntity con toda la información del usuario
     *         que acaba de iniciar sessión
     */
    public UserEntity login(String username, String password, HttpServletRequest request) {

        // Identificamos el usuario
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Guardar en SecurityContext del hilo actual
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Se añade el contexto a la sessión http
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return getUserEntityFromActualUserContext();
    }

    /**
     * Eliminamos el contexto de la sesión actual y permitimos el logout del usuario
     * 
     * @return true si todo ha salido bien, false si algo ha ocurrido
     */
    public boolean logout() {
        SecurityContextHolder.getContext().setAuthentication(null);

        if (SecurityContextHolder.getContext().getAuthentication() != null)
            return false;

        return true;
    }

    /**
     * obtenemos toda la información de la entidad del tipo UserEntity del usuario
     * que está con sesión iniciada. Obtenemos el username del contexto actual
     * 
     * @return entidad UserEntity con la información del usuario del contexto
     * 
     * @throws UsernameNotFoundException, si el usuario del actual contexto no
     *                                    existe
     * 
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
     * @throws UserInvalidRoleException, si el usuario del actual contexto no es
     *                                   admin
     * 
     */
    public UserEntity checkRoleAdminFromUserContext() {
        UserEntity userContext = getUserEntityFromActualUserContext();

        if (!userContext.getRole().equals(RoleEnum.ADMIN.getName()))
            throw new UserInvalidRoleException();

        return userContext;
    }

    /**
     * Añadimos nuevo usuario, realizamos validaciones del usuario del contexto que
     * lo añade y que no exista nada repetido. Validamos manualmente para evitar
     * excepciones de DataIntegrity
     * 
     * @param userEntity usuario para añadir
     * 
     * @return Devolvemos la entidad obtenida de la base de datos
     * 
     * @throws UserAlreadyExistException, si el nombre de usuario o email ya existen
     * 
     */
    public void addUser(UserEntity userEntity) {

        checkRoleAdminFromUserContext();

        Optional<UserEntity> userUsernameOptional = userEntityRepository.findByUsername(userEntity.getUsername());

        if (!userUsernameOptional.isEmpty())
            throw new UserAlreadyExistException("El nombre de usuario que quieres añadir ya existe");

        Optional<UserEntity> userMailOptional = userEntityRepository.findByEmail(userEntity.getEmail());

        if (!userMailOptional.isEmpty())
            throw new UserAlreadyExistException("El email de usuario que quieres añadir ya existe");

        userEntity.setPassword(passwordEncoder().encode(userEntity.getPassword()));

        userEntityRepository.save(userEntity);
    }

    /**
     * Eliminar usuario proporcionando su id
     * 
     * @param id UserEntity id
     * 
     * @return devuelve false si el usuario no fue eliminado, true si sí.
     */
    public void delUSer(Long id) {
        checkRoleAdminFromUserContext();

        Optional<UserEntity> userEntity = userEntityRepository.findById(id);

        if (userEntity.isEmpty())
            throw new UsernameNotFoundException(null);

        userEntityRepository.delete(userEntity.get());
    }

    /**
     * Actualizar UserEntity
     * 
     * @param userEntity userEntity del usuario a actualizar
     * 
     * @throws UsernameNotFoundException, si el usuario no existe
     */
    public void editUser(UserEntity userEntity) {
        checkRoleAdminFromUserContext();

        Optional<UserEntity> userEntityDB = userEntityRepository.findById(userEntity.getId());

        if (userEntityDB.isEmpty())
            throw new UsernameNotFoundException(null);

        UserEntity user = userEntityDB.get();

        if (!user.getPassword().equals(userEntity.getPassword()))
            user.setPassword(passwordEncoder().encode(userEntity.getPassword()));

        userEntityRepository.save(userEntity);
    }

    /**
     * Proporcionamos el UserEntity de usuario que se proporciona su id
     * 
     * @param id UserEntity id
     * 
     * @return Devolvemos un UserEntity si todo ha ido bien
     * 
     * @throws UsernameNotFoundException, si el usuario no existe
     */
    public UserEntity getUser(Long id) {
        checkRoleAdminFromUserContext();

        Optional<UserEntity> userOptional = userEntityRepository.findById(id);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("");

        return userOptional.get();
    }

    /**
     * Para obtener todos los usuarios, sólo con role dmin
     * 
     * @return lista de usuarios
     */
    public List<UserEntityDTO> getAllUSersDTO() {

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

}

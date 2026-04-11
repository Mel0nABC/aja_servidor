package dev.aja.aja.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.repository.UserRepository;

/**
 * Clase para crear todos los ejemplos de la entity User
 */
public class DemoUsers {

    private final UserRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private UserEntity admin, user, ghost;
    private List<UserEntity> userList;

    /**
     * Constructor, al cual le pasamos el userEntityRepository para guardar las
     * instancias de UserEntity y passwordEncoder para codificar contraseñas
     * 
     * @param userEntityRepository repositorio para realizar acciones en la base de
     *                             datos de usuarios
     * @param passwordEncoder      objeto para poder codificar las contraseñas
     */
    public DemoUsers(UserRepository userEntityRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;

        createUserAndAdminUsers();
        createUserListWithNames();
        createUserList();
    }

    /**
     * Crear los dos usuarios principales, uno role ADMIN y otro role USER
     */
    public void createUserAndAdminUsers() {
        this.admin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .email("admin@aja.dev")
                .role(RoleEnum.ADMIN.getName())
                .registerDate(LocalDate.now())
                .build();

        this.user = UserEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .email("user@aja.dev")
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        /**
         * Este usuario es para cuándo se elimina un usuario, asignarlo a Topics y Posts
         */
        this.ghost = UserEntity.builder()
                .username("Deleted User")
                .password(passwordEncoder.encode("X8917283712jaijsd"))
                .email("ghost@aja.dev")
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        userEntityRepository.saveAll(List.of(admin, user, ghost));
    }

    /**
     * Para crear una lista de muchos usuarios y poder manejarlos en las vistas del
     * front
     */
    public void createUserList() {
        List<UserEntity> userList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            Boolean active = true;

            if (i <= 1)
                active = false;

            userList.add(UserEntity.builder()
                    .username("User" + i)
                    .password(passwordEncoder.encode("1234"))
                    .email("user" + i + "@aja.dev")
                    .role(RoleEnum.USER.getName())
                    .registerDate(LocalDate.now())
                    .isActive(active)
                    .build());
        }

        userEntityRepository.saveAll(userList);
    }

    /**
     * Crear una lista de usuarios con nombres reales para luego añadir post con
     * ellos
     * 
     * @return lista de UserEntity
     */
    public List<UserEntity> createUserListWithNames() {

        List<UserEntity> userList = new ArrayList<>();
        /**
         * Usuarios creados con chatgpt.
         * Prompt:
         * con la misma estructura que te voy a mostrar, creame 4 usuarios con
         * diferentes
         * usernames y emails y todos que sean RoleEnum.USER.getName() de role
         * 
         * UserEntity admin = UserEntity.builder()
         * .username("admin")
         * .password(passwordEncoder.encode("1234"))
         * .email("admin@aja.dev")
         * .role(RoleEnum.ADMIN.getName())
         * .registerDate(LocalDate.now())
         * .build();
         * 
         * Luego edité para cambiarlo de lugar y añadirlo a una lista
         */
        userList.add(UserEntity.builder()
                .username("juan")
                .password(passwordEncoder.encode("1234"))
                .email("juan@aja.dev")
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build());

        userList.add(UserEntity.builder()
                .username("maria")
                .password(passwordEncoder.encode("1234"))
                .email("maria@aja.dev")
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build());

        userList.add(UserEntity.builder()
                .username("carlos")
                .password(passwordEncoder.encode("1234"))
                .email("carlos@aja.dev")
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build());

        userList.add(UserEntity.builder()
                .username("laura")
                .password(passwordEncoder.encode("1234"))
                .email("laura@aja.dev")
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build());

        userEntityRepository.saveAll(userList);

        this.userList = userList;

        return userList;
    }

    /**
     * Retorna el usuario con role ADMIN
     * 
     * @return UserEntity del usuario
     */
    public UserEntity getAdmin() {
        return userEntityRepository.findByUsername(this.admin.getUsername()).get();
    }

    /**
     * Retorna el usuario con role USER
     * 
     * @return UserEntity del usuario
     */
    public UserEntity getUser() {
        return userEntityRepository.findByUsername(this.user.getUsername()).get();
    }

    /**
     * Retorna el usuario con role USER pero denominado GHOST
     * 
     * @return UserEntity del usuario
     */
    public UserEntity getGhost() {
        return userEntityRepository.findByUsername(this.ghost.getUsername()).get();
    }

    /**
     * Retorna una lista de usuarios con nombres reales
     * 
     * @return lista UserEntity
     */
    public List<UserEntity> getUserList() {
        return userList;
    }

}

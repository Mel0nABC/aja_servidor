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

    /**
     * Constructor, al cual le pasamos el forumRepository para guardar las
     * instancias de UserEntity y passwordEncoder para codificar contraseñas
     * 
     * @param forumRepository para guardar las entidades Forum creadas
     * @param passwordEncoder para codificar las contraseñas
     */
    public DemoUsers(UserRepository userEntityRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;

        createUserAndAdminUsers();
        createUserList();
    }

    /**
     * Crear los dos usuarios principales, uno role ADMIN y otro role USER
     */
    public void createUserAndAdminUsers() {
        UserEntity admin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .email("admin@aja.dev")
                .role(RoleEnum.ADMIN.getName())
                .registerDate(LocalDate.now())
                .build();

        UserEntity user = UserEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .email("user@aja.dev")
                .role(RoleEnum.USER.getName())
                .registerDate(LocalDate.now())
                .build();

        userEntityRepository.saveAll(List.of(admin, user));
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

}

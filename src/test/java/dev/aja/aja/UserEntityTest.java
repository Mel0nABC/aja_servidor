package dev.aja.aja;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestConstructor;

import dev.aja.aja.auth.RoleEnum;
import dev.aja.aja.auth.entity.UserEntity;
import dev.aja.aja.auth.exception.UserAlreadyExistException;
import dev.aja.aja.auth.repository.UserEntityRepository;
import dev.aja.aja.auth.service.AuthService;
import jakarta.transaction.Transactional;

/**
 * Clase para realizar test de manipulación de usuarios
 */
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public class UserEntityTest {

    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserEntityRepository userEntityRepository;
    private UserEntity userEntity;

    /**
     * Constructor para inyección de dependencias.
     * 
     * @param authService
     * @param passwordEncoder
     * @param authenticationManager
     * @param userEntityRepository
     */
    public UserEntityTest(AuthService authService, BCryptPasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, UserEntityRepository userEntityRepository) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userEntityRepository = userEntityRepository;
    }

    /**
     * Método que se ejecuta cada vez antes de cada test, con esto garantizamos que
     * disponemos de un usuario en el contexto listo para usar
     */
    @BeforeEach
    public void setUp() {

        String password = "1234";

        this.userEntity = UserEntity.builder()
                .username("adminTest")
                .password(passwordEncoder.encode(password))
                .role(RoleEnum.ADMIN.getName())
                .email("adminTest@adminTest.com")
                .build();

        // Sólo se usa este repositorio para este usuario, es necesario para poder
        // tenerlo en el contexto, el resto de métodos para añadir usuarios, usan
        // validaciones
        userEntityRepository.save(userEntity);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(this.userEntity.getUsername(), password));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Comprobamos que el usuario de la sesión dispone de contexto. Es necesario
     * para realizar acciones en authService
     */
    @Test
    @Order(1)
    public void checkUserContext() {
        assertEquals(this.userEntity.getUsername(), SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Añadir usuario para comprobar que el usuario del contexto es role admin
     */
    @Test
    @Order(2)
    public void addUserEntityWithAdminRoleContextTest() {

        UserEntity userEntity = UserEntity.builder()
                .username("userTest")
                .password(passwordEncoder.encode("1234"))
                .role(RoleEnum.USER.getName())
                .email("userTest@userTest.com")
                .build();

        authService.addUser(userEntity);

        Optional<UserEntity> userOption = userEntityRepository.findByUsername(userEntity.getUsername());

        assertFalse(userOption.isEmpty());
        assertEquals(userEntity.getUsername(), userOption.get().getUsername());
    }

    /**
     * Añadir usuario que ya existe su username
     */
    @Test
    @Order(3)
    public void addUserCheckVerifyUserAlreadyExistExceptionWithUsernameTest() {

        addUserEntityWithAdminRoleContextTest();

        assertThrows(UserAlreadyExistException.class, () -> {

            UserEntity userEntity = UserEntity.builder()
                    .username("userTest")
                    .password(passwordEncoder.encode("1234"))
                    .role(RoleEnum.USER.getName())
                    .email("")
                    .build();

            authService.addUser(userEntity);
        });

        assertDoesNotThrow(() -> {

            UserEntity userEntity = UserEntity.builder()
                    .username("userTest2")
                    .password(passwordEncoder.encode("1234"))
                    .role(RoleEnum.USER.getName())
                    .email("")
                    .build();

            authService.addUser(userEntity);
        });
    }

    /**
     * Añadir usuario que ya existe su email
     */
    @Test
    @Order(4)
    public void addUserCheckVerifyUserAlreadyExistExceptionWithEmailTest() {

        addUserEntityWithAdminRoleContextTest();

        assertThrows(UserAlreadyExistException.class, () -> {

            UserEntity userEntity = UserEntity.builder()
                    .username("123")
                    .password(passwordEncoder.encode("1234"))
                    .role(RoleEnum.USER.getName())
                    .email("userTest@userTest.com")
                    .build();

            authService.addUser(userEntity);
        });

        assertDoesNotThrow(() -> {

            UserEntity userEntity = UserEntity.builder()
                    .username("123")
                    .password(passwordEncoder.encode("1234"))
                    .role(RoleEnum.USER.getName())
                    .email("userTest@userTest2.com")
                    .build();

            authService.addUser(userEntity);
        });
    }

    /**
     * Eliminar usuario, se comprueba que se añadió correctamente, que fue borrado y
     * comprobamos eliminándolo nuevamente que nos lanza excepción
     */
    @Test
    @Order(5)
    public void deleteUser() {
        UserEntity userEntity = UserEntity.builder()
                .username("123")
                .password(passwordEncoder.encode("1234"))
                .role(RoleEnum.USER.getName())
                .email("userTest@userTest.com")
                .build();

        authService.addUser(userEntity);

        Optional<UserEntity> userOptional = userEntityRepository.findByUsername(userEntity.getUsername());

        assertFalse(userOptional.isEmpty());

        UserEntity user = userOptional.get();

        authService.delUSer(user.getId());

        Optional<UserEntity> userOption = userEntityRepository.findByUsername(user.getUsername());

        assertTrue(userOption.isEmpty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.delUSer(user.getId());
        });

    }

    /**
     * Actualizar los datos de un usuario existente. Lo añadimos, editamos su email
     * y lo volvemos a guardar. Seguidamente lo obtenemos de la base de datos y
     * comprobamos que existe
     */
    @Test
    @Order(6)
    public void updateUser() {
        UserEntity userEntity = UserEntity.builder()
                .username("123")
                .password(passwordEncoder.encode("1234"))
                .role(RoleEnum.USER.getName())
                .email("userTest@userTest.com")
                .build();

        authService.addUser(userEntity);

        Optional<UserEntity> userOptional = userEntityRepository.findByUsername(userEntity.getUsername());

        assertFalse(userOptional.isEmpty());

        UserEntity user = userOptional.get();

        user.setEmail("");

        authService.editUser(userEntity);

        assertNotNull(authService.getUser(user.getId()));

    }

}

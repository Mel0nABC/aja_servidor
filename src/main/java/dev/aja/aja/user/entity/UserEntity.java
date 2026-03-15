package dev.aja.aja.user.entity;

import dev.aja.aja.user.RoleEnum;
import dev.aja.aja.user.dto.UserEntityDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase donde se implementa toda la información del usuario. Al usar
 * annotations de Lombok, es innnecesario declarar constructores, getters,
 * setters, etcére
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
public class UserEntity {

    /**
     * Constructor creado para ignorar warnings cuando se crea javadoc
     */
    public UserEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private String role = RoleEnum.USER.getName();

    @Builder.Default
    private Boolean isActive = true;

    /***
     * Método para generar UserEntityDTO con la información de la instancia que
     * ejecuta el método
     * 
     * @return devolvemos un UserEntityDTO, que envía toda la información sin la
     *         contraseña del usuario. Con esto hacemos más segura la transferencia
     *         de información del usuario entre cliente y servidor y, ademas, al ser
     *         un DTO conseguimos mejor eficiencia en envío de información
     */
    public UserEntityDTO toDTO() {
        return UserEntityDTO.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .role(this.role)
                .isActive(this.isActive)
                .build();
    }

}

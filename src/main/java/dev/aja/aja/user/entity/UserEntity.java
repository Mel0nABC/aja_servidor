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
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase donde se implementa toda la información del usuario. Al usar
 * annotations de Lombok, es innnecesario declarar constructores, getters,
 * setters, etcére
 */
@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserEntity {

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
    private boolean isActive = true;

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

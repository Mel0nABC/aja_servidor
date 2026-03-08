package dev.aja.aja.user;

/**
 * Se crea este enum para asegurarnos que no va a haber errores en las opciones
 * de role para UserEntity
 */
public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

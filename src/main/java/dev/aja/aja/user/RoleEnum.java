package dev.aja.aja.user;

/**
 * Se crea este enum para asegurarnos que no va a haber errores en las opciones
 * de role para UserEntity
 */
public enum RoleEnum {

    /**
     * Indica que el usuario tiene como rol Admin, tendrá acceso a gestión de
     * usuarios entre otros
     */
    ADMIN("ADMIN"),

    /**
     * Usuario básico de la aplicación
     */
    USER("USER");

    private final String name;

    /**
     * Constructor del enum, sirve para poder usar sus métodos
     * 
     * @param name indicamos a qué enum hace referencia
     */
    RoleEnum(String name) {
        this.name = name;
    }

    /**
     * Para obtener el value del enum en cuestion
     * 
     * @return devolvemos el valor de tipo String
     */
    public String getName() {
        return name;
    }

    /**
     * Sobre escritura de toString() que simplemente devuelve el valor del enum
     * 
     * @return devolvemos el valor de tipo String
     */
    @Override
    public String toString() {
        return name;
    }
}

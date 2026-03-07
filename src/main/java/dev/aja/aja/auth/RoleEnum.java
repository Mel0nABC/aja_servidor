package dev.aja.aja.auth;

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

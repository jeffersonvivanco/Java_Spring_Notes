package app.security.constants;

public enum  RolesAndPrivileges {
    READ_PRIVILEGE("READ_PRIVILEGE"), WRITE_PRIVILEGE("WRITE_PRIVILEGE"),
    ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER"), AUTHORITIES("AUTHORITIES");

    private String value;

    RolesAndPrivileges(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

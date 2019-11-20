package app.security.constants;

public enum  SecurityConstants {
    SECRET("SecretKeyToGenJWTs"), EXPIRATION_TIME(864_000_000), TOKEN_PREFIX("Bearer "),
    HEADER_STRING("Authorization"), SIGN_UP_URL("/api/users/sign-up");

    private String stringValue;
    private long longValue;

    SecurityConstants() {
    }

    SecurityConstants(String val) {
        this.stringValue = val;
    }
    SecurityConstants(long val){
        this.longValue = val;
    }

    public long getLongValue(){
        return longValue;
    }

    public String getValue(){
        return stringValue;
    }

}

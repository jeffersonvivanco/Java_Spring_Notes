package app.models;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ErisUser {
    private String username; // will be used by users to identify themselves
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // will be used to check the user's identity
    private String fullName;
    private Integer age;

    public ErisUser() { }

    public ErisUser(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}

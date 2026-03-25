package com.uep.wap.dto;

import com.uep.wap.model.Role;

import java.util.HashSet;
import java.util.Set;

public class CreateUserDTO {
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Set<Role> roles = new HashSet<>();

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}

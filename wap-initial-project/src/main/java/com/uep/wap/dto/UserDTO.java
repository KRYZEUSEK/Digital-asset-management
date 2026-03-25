package com.uep.wap.dto;

import com.uep.wap.model.Role;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private boolean active;
    private Set<Role> roles = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}

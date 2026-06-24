package backend.dto;

import backend.model.Role;

import java.util.HashSet;
import java.util.Set;

public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Boolean active;
    private Set<Role> roles = new HashSet<>();

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}

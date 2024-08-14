package com.skillstorm.dtos;

import com.skillstorm.constants.Role;
import com.skillstorm.entities.AuthUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AuthUserDto {

    private String username;
    private String password;
    private boolean enabled;
    private List<Role> roles;

    public AuthUserDto() {
        this.roles = new ArrayList<>(1);
    }

    public AuthUserDto(AuthUser user) {
        this();
        this.username = user.getUsername();
        this.enabled = user.isEnabled();
        this.roles = new ArrayList<>(user.getRoles());
    }

    public AuthUser mapToEntity() {
        AuthUser user = new AuthUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(enabled);
        user.setRoles(new ArrayList<>(roles));

        return user;
    }
}

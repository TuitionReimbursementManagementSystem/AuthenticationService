package com.skillstorm.entities;

import com.skillstorm.constants.Role;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Table("auth_users")
public class AuthUser implements UserDetails {

    @PrimaryKey
    private String username;

    private String password;

    private boolean enabled;

    private List<Role> roles;

    public AuthUser() {
        super();
        this.enabled = true;
        this.roles = new ArrayList<>(1);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    public List<String> getUserList() {
        return new ArrayList<>();
    }
}

package ru.kata.spring.boot_security.demo.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import java.util.Collection;
import javax.persistence.*;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private byte age;
    private String password;
    private String role;

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.MERGE)
    @JoinTable(
            name="user_role",
            joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="id")})
    private List<Role> roles;

    public String getStringRoles() {
        String stringRoles = "";
        if (roles != null) {
            for (Role role : roles) {
                stringRoles += " " + role.getRoleName();
            }
            return stringRoles.trim().replace("ROLE_", "");
        } else {
            return null;
        }
    }

    public String getRole() {
        if (role != null) {
            return role.toUpperCase();
        } else {
            return role;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
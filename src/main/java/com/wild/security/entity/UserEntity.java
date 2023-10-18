package com.wild.security.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wild.security.dto.RoleDto;
import com.wild.security.dto.UserDto;

import org.springframework.security.core.userdetails.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name ="user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean active;

   //FetchType.EAGER force Hibernate à charger les rôles en même temps que l'utilisateur
   //CascadeType.MERGE => https://www.baeldung.com/hibernate-detached-entity-passed-to-persist
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(String username, String email, String password, List<Role> roles, boolean active) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UserDetails asUserDetails() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (Role role : this.getRoles()) {
        authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
    }
    return User.withUsername(username)
        .password(password)
        .authorities(authorities)
        .build();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserDto toUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUsername(this.getUsername());
        userDto.setEmail(this.getEmail());
        userDto.setPassword(this.getPassword());

        List<RoleDto> roleDtos = new ArrayList<>();
        for (Role role : this.getRoles()) {
            RoleDto roleDto = new RoleDto(role.getName().toString());
            roleDtos.add(roleDto);
        }

        userDto.setRoles(roleDtos);

        return userDto;
    }
}

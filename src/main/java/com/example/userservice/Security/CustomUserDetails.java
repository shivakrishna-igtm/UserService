package com.example.userservice.Security;

import com.example.userservice.Models.Role;
import com.example.userservice.Models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@JsonDeserialize(as=CustomUserDetails.class)
@NoArgsConstructor

public class CustomUserDetails implements UserDetails {
    private User user;

    public CustomUserDetails(User user){
        this.user=user;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<Role> roles = user.getRoles();

        Collection<CustomGrantedAuthority> customGrantedAuthorities = new ArrayList<>();
        for (Role role : roles) {
            customGrantedAuthorities.add(
                    new CustomGrantedAuthority(role)
            );
        }
        return customGrantedAuthorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}

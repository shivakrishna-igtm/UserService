package com.example.userservice.DTO;

import com.example.userservice.Models.Role;
import com.example.userservice.Models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private String email;
    private Set<Role> roles=new HashSet<>();
    public static UserDto setUser(User user){
        UserDto userDto=new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}

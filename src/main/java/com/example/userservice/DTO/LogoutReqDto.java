package com.example.userservice.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutReqDto {
    private String Token;
    private Long userId;
}

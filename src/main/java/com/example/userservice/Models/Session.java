package com.example.userservice.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Entity
public class Session extends BaseModel{
    private String token;
    @Enumerated(EnumType.ORDINAL)
    private SessionStatus sessionStatus;
    private Date expiryDate;
    @ManyToOne
    private User user;
}

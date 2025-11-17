package com.personal.ecommerceauth.dto.response;


import com.personal.ecommerceauth.util.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String email;
    private UserRole role;
}

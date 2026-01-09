package com.personal.ecommerceauth.dto.response;

import com.personal.ecommerceauth.util.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String email;
    private UserRole role;
    private String jwt;
}

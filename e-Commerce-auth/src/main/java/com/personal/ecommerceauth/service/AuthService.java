package com.personal.ecommerceauth.service;


import com.personal.ecommerceauth.dto.request.LoginRequest;
import com.personal.ecommerceauth.dto.request.RegisterRequest;
import com.personal.ecommerceauth.dto.response.LoginResponse;
import com.personal.ecommerceauth.dto.response.RegisterResponse;
import com.personal.ecommerceauth.entity.User;
import com.personal.ecommerceauth.exception.EmailAlreadyUsedException;
import com.personal.ecommerceauth.exception.InvalidCredentialsException;
import com.personal.ecommerceauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.personal.ecommerceauth.util.UserRole.USER;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("Email already in use");
        }

        User saved = userRepository.save(User.builder()
                                             .email(request.getEmail())
                                             .passwordHash(passwordEncoder.encode(request.getPassword()))
                                             .role(USER)
                                             .build());

        return new RegisterResponse(saved.getId(), saved.getEmail(), saved.getRole());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                                  .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}

package com.authservice.service;

import com.authservice.dto.RegisterRequest;
import com.authservice.entity.User;
import com.authservice.enums.UserRole;
import com.authservice.kafka.UserEventProducer;
import com.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;

    public User createUser(RegisterRequest request){
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        User saved = userRepository.save(user);
        userEventProducer.publishUserCreated(saved.getId());
        return saved;
    }

    public User getUser(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}

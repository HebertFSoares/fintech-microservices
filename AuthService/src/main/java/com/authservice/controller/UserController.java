package com.authservice.controller;

import com.authservice.dto.RegisterRequest;
import com.authservice.dto.RegisterResponse;
import com.authservice.dto.UserResponse;
import com.authservice.entity.User;
import com.authservice.mapper.UserMapper;
import com.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> create(@RequestBody RegisterRequest request){
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toRegisterResponse(user));
    }

    @GetMapping("/{id}/user")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id){
        User getUser = userService.getUser(id);
        return ResponseEntity.ok(userMapper.toUserResponse(getUser));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();

        return ResponseEntity.ok(users);
    }
}

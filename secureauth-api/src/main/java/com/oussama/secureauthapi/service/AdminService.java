package com.oussama.secureauthapi.service;

import com.oussama.secureauthapi.dto.UserResponse;
import com.oussama.secureauthapi.entity.User;
import com.oussama.secureauthapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .enabled(user.isEnabled())
                        .roles(user.getRoles()
                                .stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toSet()))
                        .build())
                .toList();
    }

    public UserResponse updateUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(enabled);
        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .enabled(savedUser.isEnabled())
                .roles(savedUser.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(java.util.stream.Collectors.toSet()))
                .build();
    }
}
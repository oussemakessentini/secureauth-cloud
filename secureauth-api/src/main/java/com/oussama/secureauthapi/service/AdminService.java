package com.oussama.secureauthapi.service;

import com.oussama.secureauthapi.dto.UserResponse;
import com.oussama.secureauthapi.entity.Role;
import com.oussama.secureauthapi.entity.RoleName;
import com.oussama.secureauthapi.entity.User;
import com.oussama.secureauthapi.repository.RoleRepository;
import com.oussama.secureauthapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    public UserResponse updateUserStatus(Long userId, boolean enabled) {
        User user = getUserById(userId);

        user.setEnabled(enabled);

        return mapToUserResponse(userRepository.save(user));
    }

    public UserResponse addRoleToUser(Long userId, RoleName roleName) {
        User user = getUserById(userId);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);

        return mapToUserResponse(userRepository.save(user));
    }

    public UserResponse removeRoleFromUser(Long userId, RoleName roleName) {
        User user = getUserById(userId);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().remove(role);

        return mapToUserResponse(userRepository.save(user));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}
package com.oussama.secureauthapi.controller;

import com.oussama.secureauthapi.dto.UserResponse;
import com.oussama.secureauthapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PatchMapping("/users/{userId}/status")
    public UserResponse updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean enabled
    ) {
        return adminService.updateUserStatus(userId, enabled);
    }
}
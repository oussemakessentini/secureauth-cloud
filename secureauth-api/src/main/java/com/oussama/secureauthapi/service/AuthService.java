package com.oussama.secureauthapi.service;

import com.oussama.secureauthapi.dto.*;
import com.oussama.secureauthapi.entity.*;
import com.oussama.secureauthapi.repository.*;
import com.oussama.secureauthapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    @Value("${app.password-reset.expiration}")
    private long passwordResetExpiration;

    @Value("${app.email-verification.expiration}")
    private long emailVerificationExpiration;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .roles(Set.of(userRole))
                .build();

        User savedUser = userRepository.save(user);

        EmailVerificationToken verificationToken = createEmailVerificationToken(savedUser);
        String verificationLink =
                "http://localhost:5173/verify-email?token="
                        + verificationToken.getToken();
        emailService.sendEmail(
                savedUser.getEmail(),
                "Verify your SecureAuth account",
                "Welcome to SecureAuth Cloud!\n\nPlease verify your email using this token:\n\n"
                        + verificationToken.getToken()
                        + "\n\nVerification link:\n"
                        + verificationLink
        );

        return AuthResponse.builder()
                .message("User registered successfully. Please check your email to verify your account.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled. Please contact administrator.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .message("Login successful")
                .build();
    }

    public AuthResponse changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenService.deleteByUserEmail(email);

        return AuthResponse.builder()
                .message("Password changed successfully. Please login again.")
                .build();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName().name())
                                .collect(java.util.stream.Collectors.toSet())
                )
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        String accessToken = jwtService.generateToken(refreshToken.getUser().getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .message("Token refreshed successfully")
                .build();
    }

    public AuthResponse logout(LogoutRequest request) {
        refreshTokenService.deleteByUserEmail(request.getEmail());

        return AuthResponse.builder()
                .message("Logout successful")
                .build();
    }

    @Transactional
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user)
                .orElse(new PasswordResetToken());

        resetToken.setUser(user);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(Instant.now().plusMillis(passwordResetExpiration));

        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken.getToken();

        emailService.sendEmail(
                user.getEmail(),
                "SecureAuth Password Reset",
                "Use this link to reset your password:\n\n" + resetLink +
                        "\n\nOr use this token:\n\n" + resetToken.getToken()
        );

        return AuthResponse.builder()
                .message("Password reset instructions sent to your email.")
                .build();
    }

    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Password reset token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        refreshTokenService.deleteByUserEmail(user.getEmail());

        return AuthResponse.builder()
                .message("Password reset successfully. Please login again.")
                .build();
    }

    private EmailVerificationToken createEmailVerificationToken(User user) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByUser(user)
                .orElse(new EmailVerificationToken());

        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(emailVerificationExpiration));

        return emailVerificationTokenRepository.save(token);
    }

    @Transactional
    public AuthResponse verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("Verification token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);
        emailVerificationTokenRepository.delete(verificationToken);

        return AuthResponse.builder()
                .message("Email verified successfully. You can now login.")
                .build();
    }

    @Transactional
    public AuthResponse resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        EmailVerificationToken verificationToken = createEmailVerificationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                "SecureAuth Email Verification",
                "Use this token to verify your email:\n\n" + verificationToken.getToken()
        );

        return AuthResponse.builder()
                .message("Verification email sent successfully.")
                .build();
    }
}
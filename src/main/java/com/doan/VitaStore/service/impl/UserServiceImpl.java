package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.admin.UserRequest;
import com.doan.VitaStore.dto.response.admin.UserResponse;
import com.doan.VitaStore.entity.PasswordResetTokenEntity;
import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.enums.Role;
import com.doan.VitaStore.enums.Status;
import com.doan.VitaStore.exception.UserNotFoundException;
import com.doan.VitaStore.repository.PasswordResetTokenRepository;
import com.doan.VitaStore.repository.UserRepository;
import com.doan.VitaStore.service.EmailService;
import com.doan.VitaStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    private final Random random = new Random();

    @Override
    public UserEntity registerUser(String fullName, String email, String phone, String password) {
        String normalizedFullName = normalize(fullName);
        String normalizedEmail = normalize(email);
        String normalizedPhone = normalize(phone);
        String normalizedPassword = normalize(password);

        if (normalizedFullName.isBlank()
                || normalizedEmail.isBlank()
                || normalizedPhone.isBlank()
                || normalizedPassword.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin đăng ký.");
        }
        if (normalizedPassword.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setFullName(normalizedFullName);
        userEntity.setEmail(normalizedEmail);
        userEntity.setPhone(normalizedPhone);
        userEntity.setPasswordHash(passwordEncoder.encode(normalizedPassword));
        userEntity.setRole(Role.USER);
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setDeletedAt(null);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmailOrPhone(email,email).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmailOrPhone(email,email).isPresent();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toAdminUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserByIdResponse(int id) {
        UserEntity user = userRepository.findById((long) id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toAdminUserResponse(user);
    }

    @Override
    public UserResponse createUserByAdmin(UserRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setFullName(request.getFullName());
        userEntity.setEmail(request.getEmail());
        userEntity.setPhone(request.getPhone());
        userEntity.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        userEntity.setRole(request.getRole());
        userEntity.setStatus(request.getStatus());
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setDeletedAt(null);

        UserEntity savedUser = userRepository.save(userEntity);
        return toAdminUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUserByAdmin(int id, UserRequest request) {
        UserEntity user = userRepository.findById((long) id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());

        if (request.getPasswordHash() != null && !request.getPasswordHash().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        }

        UserEntity savedUser = userRepository.save(user);
        return toAdminUserResponse(savedUser);
    }

    @Override
    public void deleteUserById(int id) {
        UserEntity user = userRepository.findById((long) id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(Status.BLOCKED);
        userRepository.save(user);
    }

    private UserResponse toAdminUserResponse(UserEntity user) {
        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getDeletedAt() != null ? user.getDeletedAt().toString() : null
        );
    }

    @Override
    public UserResponse updateProfile(int userId, String fullName, String phone) {
        UserEntity user = userRepository.findById((long) userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (fullName != null && !fullName.isBlank()) user.setFullName(fullName);
        if (phone != null) user.setPhone(phone);
        return toAdminUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse changePassword(int userId, String currentPassword, String newPassword) {
        UserEntity user = userRepository.findById((long) userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return toAdminUserResponse(userRepository.save(user));
    }

    @Override
    public boolean verifyPassword(int userId, String rawPassword) {
        UserEntity user = userRepository.findById((long) userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    @Override
    public UserResponse restoreUserById(int id) {
        UserEntity user = userRepository.findById((long) id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setDeletedAt(null);
        user.setStatus(Status.ACTIVE);
        return toAdminUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trong hệ thống."));

        String otp = String.format("%06d", random.nextInt(999999));
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity(
                email, otp, token, now.plusMinutes(5), now.plusMinutes(15)
        );
        tokenRepository.save(resetToken);

        emailService.sendOtpEmail(email, otp);
    }

    @Override
    @Transactional
    public String verifyOtp(String email, String otp) {
        PasswordResetTokenEntity resetToken = tokenRepository
                .findByEmailAndOtpAndUsedFalse(email, otp)
                .orElseThrow(() -> new IllegalArgumentException("Mã OTP không đúng hoặc đã hết hạn."));

        if (resetToken.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Mã OTP đã hết hạn.");
        }

        return resetToken.getToken();
    }

    @Override
    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        PasswordResetTokenEntity resetToken = tokenRepository
                .findByEmailAndTokenAndUsedFalse(email, token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ."));

        if (resetToken.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token đã hết hạn.");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại."));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}

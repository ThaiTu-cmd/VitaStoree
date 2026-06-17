package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.admin.UserRequest;
import com.doan.VitaStore.dto.response.admin.UserResponse;
import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.enums.Role;
import com.doan.VitaStore.enums.Status;
import com.doan.VitaStore.exception.UserNotFoundException;
import com.doan.VitaStore.repository.UserRepository;
import com.doan.VitaStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity registerUser(String fullName, String email, String phone, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFullName(fullName);
        userEntity.setEmail(email);
        userEntity.setPhone(phone);
        userEntity.setPasswordHash(passwordEncoder.encode(password));
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

}
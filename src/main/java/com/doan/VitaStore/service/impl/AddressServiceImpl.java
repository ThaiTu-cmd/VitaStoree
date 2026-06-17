package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.client.AddressRequest;
import com.doan.VitaStore.dto.response.client.AddressResponse;
import com.doan.VitaStore.entity.UserAddressesEntity;
import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.repository.UserAddressesRepository;
import com.doan.VitaStore.repository.UserRepository;
import com.doan.VitaStore.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressesRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses() {
        return repository.findByDeletedAtIsNull().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(int id) {
        UserAddressesEntity entity = repository.findByAddressIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByUser(int userId) {
        return repository.findByUserUserIdAndDeletedAtIsNull(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void setDefaultById(int id) {
        UserAddressesEntity entity = repository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        clearOtherDefaults(entity.getUser().getUserId());
        entity.setDefault(true);
        repository.save(entity);
    }

    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        UserEntity user = userRepository.findById((long) request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        UserAddressesEntity entity = new UserAddressesEntity();
        entity.setUser(user);
        entity.setReceiverName(request.getReceiverName());
        entity.setReceiverPhone(request.getReceiverPhone());
        entity.setProvince(request.getProvince());
        entity.setDistrict(request.getDistrict());
        entity.setWard(request.getWard());
        entity.setStreetAddress(request.getStreetAddress());
        entity.setDefault(request.isDefault());

        if (request.isDefault()) {
            clearOtherDefaults(user.getUserId());
        }

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(int id, AddressRequest request) {
        UserAddressesEntity entity = repository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        entity.setReceiverName(request.getReceiverName());
        entity.setReceiverPhone(request.getReceiverPhone());
        entity.setProvince(request.getProvince());
        entity.setDistrict(request.getDistrict());
        entity.setWard(request.getWard());
        entity.setStreetAddress(request.getStreetAddress());

        if (request.isDefault() && !entity.isDefault()) {
            clearOtherDefaults(entity.getUser().getUserId());
            entity.setDefault(true);
        } else if (!request.isDefault()) {
            entity.setDefault(false);
        }

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteAddressById(int id) {
        UserAddressesEntity entity = repository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

    @Override
    @Transactional
    public AddressResponse restoreAddressById(int id) {
        UserAddressesEntity entity = repository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        entity.setDeletedAt(null);
        return toResponse(repository.save(entity));
    }

    private void clearOtherDefaults(int userId) {
        repository.findByDeletedAtIsNull().stream()
                .filter(a -> a.getUser().getUserId() == userId && a.isDefault())
                .forEach(a -> {
                    a.setDefault(false);
                    repository.save(a);
                });
    }

    private AddressResponse toResponse(UserAddressesEntity entity) {
        return new AddressResponse(
                entity.getAddressId(),
                entity.getUser() != null ? entity.getUser().getUserId() : 0,
                entity.getUser() != null ? entity.getUser().getEmail() : "—",
                entity.getReceiverName(),
                entity.getReceiverPhone(),
                entity.getProvince(),
                entity.getDistrict(),
                entity.getWard(),
                entity.getStreetAddress(),
                entity.getFullAddress(),
                entity.isDefault(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null,
                entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null
        );
    }
}

package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.request.client.AddressRequest;
import com.doan.VitaStore.dto.response.client.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAllAddresses();
    AddressResponse getAddressById(int id);
    List<AddressResponse> getAddressesByUser(int userId);
    AddressResponse createAddress(AddressRequest request);
    AddressResponse updateAddress(int id, AddressRequest request);
    void deleteAddressById(int id);
    AddressResponse restoreAddressById(int id);
    void setDefaultById(int id);
}

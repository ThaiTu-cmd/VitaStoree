package com.doan.VitaStore.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(int id) {
        super("Không tìm thấy user với ID: " + id);
    }
}
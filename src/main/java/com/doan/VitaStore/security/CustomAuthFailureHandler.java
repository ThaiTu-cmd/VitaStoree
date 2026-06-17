package com.doan.VitaStore.security;

import com.doan.VitaStore.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String email = request.getParameter("email");
        String error;

        if (!userRepository.findByEmailOrPhone(email, email).isPresent()) {
            error = "email_not_found";
        } else {
            error = "wrong_password";
        }

        response.sendRedirect(request.getContextPath() + "/auth/login?error=" + error);
    }
}

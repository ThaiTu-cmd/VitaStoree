package com.doan.VitaStore.security;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SecurityConfig {

    @Autowired
    private CustomAuthFailureHandler customAuthFailureHandler;

    public static final String ADMIN_CTX_KEY = "ADMIN_SPRING_SECURITY_CONTEXT";
    public static final String CLIENT_CTX_KEY = "CLIENT_SPRING_SECURITY_CONTEXT";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        HttpSessionSecurityContextRepository adminCtxRepo = new HttpSessionSecurityContextRepository();
        adminCtxRepo.setSpringSecurityContextKey(ADMIN_CTX_KEY);

        http.securityMatcher("/admin/**", "/api/admin/**", "/api/auth/admin/**")
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect(request.getContextPath() + "/admin/login"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect(request.getContextPath() + "/admin/login")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/admin/**").permitAll()
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/admin/css/**", "/admin/js/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .securityContext(ctx -> ctx.securityContextRepository(adminCtxRepo));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain clientFilterChain(HttpSecurity http) throws Exception {
        HttpSessionSecurityContextRepository clientCtxRepo = new HttpSessionSecurityContextRepository();
        clientCtxRepo.setSpringSecurityContextKey(CLIENT_CTX_KEY);

        http.securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            String path = request.getRequestURI();
                            if (path.contains("/api/")) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                            } else {
                                response.sendRedirect(request.getContextPath() + "/auth/login");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect(request.getContextPath() + "/auth/login")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register",
                                "/auth/forgot-password", "/auth/reset-password",
                                "/auth/verify-otp").permitAll()
                        .requestMatchers("/client/**").permitAll()
                        .requestMatchers("/checkout", "/orders", "/orderdetail",
                                "/userprofile", "/address").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(customAuthFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/")
                        .addLogoutHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.removeAttribute(CLIENT_CTX_KEY);
                            }
                        })
                        .invalidateHttpSession(false)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .securityContext(ctx -> ctx.securityContextRepository(clientCtxRepo));

        return http.build();
    }
}

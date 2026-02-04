package org.bm.service.education.identity.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bm.service.education.common.security.jwt.service.JwtService;
import org.bm.service.education.identity.api.dto.request.LoginRequest;
import org.bm.service.education.identity.api.dto.request.RegisterRequest;
import org.bm.service.education.identity.api.dto.request.RefreshTokenRequest;
import org.bm.service.education.identity.api.dto.response.AuthResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.identity.domain.UserStatus;
import org.bm.service.education.identity.infrastructure.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadCredentialsException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadCredentialsException("Пользователь с таким email уже существует");
        }
        if (userRepository.existsByPersonnelNumber(request.personnelNumber())) {
            throw new BadCredentialsException("Пользователь с таким табельным номером уже существует");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .personnelNumber(request.personnelNumber())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.EMPLOYEE)
                .status(UserStatus.ACTIVE)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (DisabledException e) {
            log.warn("Login attempt for disabled user: {}", request.username());
            throw new DisabledException("User account is disabled");
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for user: {}", request.username());
            throw new BadCredentialsException("Invalid username or password");
        } catch (AuthenticationException e) {
            log.error("Authentication error for user: {}", request.username(), e);
            throw new BadCredentialsException("Authentication failed");
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }

        return buildAuthResponse(user);
    }

    public AuthResponse getCurrentUser(User user) {
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        return AuthResponse.of(accessToken, refreshToken, userInfo);
    }
}

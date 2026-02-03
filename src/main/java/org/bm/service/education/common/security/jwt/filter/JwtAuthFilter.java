package org.bm.service.education.common.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bm.service.education.common.security.jwt.service.JwtService;
import org.bm.service.education.identity.application.CustomUserDetailsService;
import org.bm.service.education.identity.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JsonMapper jsonMapper;
    private final CustomUserDetailsService userDetailsService;

    public static final String ERROR_TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String ERROR_TOKEN_INVALID = "TOKEN_INVALID";
    public static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String ERROR_USER_DISABLED = "USER_DISABLED";
    public static final String ERROR_AUTH_FAILED = "AUTH_FAILED";

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = (User) userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, user)) {
                    if (!user.isEnabled()) {
                        sendUnauthorized(ERROR_USER_DISABLED, "User account is disabled", response);
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired for request: {}", request.getRequestURI());
            sendUnauthorized(ERROR_TOKEN_EXPIRED, "JWT token has expired. Please refresh your token.", response);
            return;
        } catch (SignatureException | MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            sendUnauthorized(ERROR_TOKEN_INVALID, "Invalid JWT token", response);
            return;
        } catch (UsernameNotFoundException e) {
            log.warn("User not found for JWT: {}", e.getMessage());
            sendUnauthorized(ERROR_USER_NOT_FOUND, "User not found", response);
            return;
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            sendUnauthorized(ERROR_AUTH_FAILED, "Authentication failed", response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(String errorCode, String message, HttpServletResponse response) throws IOException {
        Map<String, Object> errorResponse = Map.of(
                "success", false,
                "status", HttpStatus.UNAUTHORIZED.value(),
                "errorCode", errorCode,
                "message", message,
                "timestamp", Instant.now().toString());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        jsonMapper.writeValue(response.getWriter(), errorResponse);
    }
}

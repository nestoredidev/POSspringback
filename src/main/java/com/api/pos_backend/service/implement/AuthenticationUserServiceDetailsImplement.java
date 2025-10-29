package com.api.pos_backend.service.implement;

import com.api.pos_backend.entity.Role;
import com.api.pos_backend.entity.Users;
import com.api.pos_backend.exception.BadRequestException;
import com.api.pos_backend.exception.ResourceNotFoundException;
import com.api.pos_backend.records.AuthCreateUserRequest;
import com.api.pos_backend.records.AuthLoginRequest;
import com.api.pos_backend.records.AuthResponse;
import com.api.pos_backend.repository.RoleRepository;
import com.api.pos_backend.repository.UserRepository;
import com.api.pos_backend.shared.authentication.JwtUtils;
import com.api.pos_backend.shared.authentication.TokenBlacklistService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationUserServiceDetailsImplement implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        List<GrantedAuthority> authorities = new ArrayList<>();
        //Puede fallar por la variabel de la entidad.
        userEntity.getRole().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
        });

        userEntity.getRole().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> {
                    authorities.add(new SimpleGrantedAuthority(permission.getName()));
                });

        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getIsEnabled(),
                userEntity.getIsAccountNonExpired(),
                userEntity.getAccountNonLocked(),
                userEntity.getIsAccountNonExpired(),
                authorities
        );
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadRequestException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse login(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateToken(authentication);

        String userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username))
                .getId().toString();


        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(auth ->
                        auth.startsWith("ROLE_")).collect(Collectors.joining(","));

        AuthResponse authResponse = new AuthResponse(
                userId,
                roles.substring(5),
                username,
                "Login successful",
                token,
                true

        );
        return authResponse;
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest) {
        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        String email = authCreateUserRequest.email();
        List<String> roles = authCreateUserRequest.role().roleListName();
        Set<Role> roleEntitySet = roleRepository.findByNameIn(roles).stream()
                .collect(Collectors.toSet());
        if (roleEntitySet.isEmpty()) {
            throw new BadRequestException("No se encontraron roles validos para el usuario");
        }

        Users newUser = Users.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isCredentialsNonExpired(true)
                .isAccountNonLocked(true)
                .accountNonLocked(true)
                .role(roleEntitySet)
                .build();

        Users savedUser = userRepository.save(newUser);
        return new AuthResponse(
                savedUser.getId().toString(),
                String.join(",", roles),
                savedUser.getUsername(),
                "User created successfully",
                null,
                true
        );
    }

    public AuthResponse verifyToken(String token) {
        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(token);
            String username = jwtUtils.extractUsername(decodedJWT);
            Users userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
            List<String> roles = userEntity.getRole().stream()
                    .map(role -> "ROLE_" + role.getName().name())
                    .collect(Collectors.toList());

            return new AuthResponse(
                    userEntity.getId().toString(),
                    roles.stream().map(r -> r.substring(5)).collect(Collectors.joining(",")),
                    userEntity.getUsername(),
                    "Token is valid",
                    null,
                    true
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Token inválido: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BadRequestException("Error al validar token: " + ex.getMessage());
        }
    }
    public Map<String, Object> logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            tokenBlacklistService.blacklistToken(jwtToken);
            return Map.of(
                    "message", "Logout exitoso",
                    "tokenExtraido", jwtToken,
                    "blacklisted", true
            );
        }
        return Map.of(
                "message", "Token no proporcionado",
                "blacklisted", false
        );
    }

}

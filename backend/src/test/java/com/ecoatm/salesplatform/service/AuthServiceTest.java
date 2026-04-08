package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.LoginRequest;
import com.ecoatm.salesplatform.dto.LoginResponse;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.UserRepository;
import com.ecoatm.salesplatform.security.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private EntityManager em;
    @Mock private Query rolesQuery;
    @Mock private Query userInfoQuery;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        Field emField = AuthService.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(authService, em);
        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("Admin123!");
        loginRequest.setRememberMe(false);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("admin@test.com");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setActive(true);
        testUser.setBlocked(false);
    }

    @Test
    void authenticateLocalUser_validCredentials_returnsSuccessWithToken() {
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin123!", "$2a$10$hashedpassword")).thenReturn(true);
        mockGetUserRoles("Administrator");
        mockBuildUserInfo();
        when(jwtService.generateToken(eq(1L), eq("admin@test.com"), any(), eq(false))).thenReturn("jwt-token");

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void authenticateLocalUser_unknownEmail_returnsFailure() {
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.empty());

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("No account");
    }

    @Test
    void authenticateLocalUser_wrongPassword_returnsFailure() {
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin123!", "$2a$10$hashedpassword")).thenReturn(false);

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Incorrect Password");
    }

    @Test
    void authenticateLocalUser_inactiveUser_returnsFailure() {
        testUser.setActive(false);
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("disabled");
    }

    @Test
    void authenticateLocalUser_blockedUser_returnsFailure() {
        testUser.setBlocked(true);
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("disabled");
    }

    @Test
    void authenticateLocalUser_withRememberMe_passesRememberMeToJwtService() {
        loginRequest.setRememberMe(true);
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin123!", "$2a$10$hashedpassword")).thenReturn(true);
        mockGetUserRoles();
        mockBuildUserInfo();
        when(jwtService.generateToken(eq(1L), eq("admin@test.com"), any(), eq(true))).thenReturn("jwt-remember");

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isTrue();
        verify(jwtService).generateToken(eq(1L), eq("admin@test.com"), any(), eq(true));
    }

    @Test
    void authenticateLocalUser_multipleRoles_allPassedToToken() {
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin123!", "$2a$10$hashedpassword")).thenReturn(true);
        mockGetUserRoles("Administrator", "SalesOps");
        mockBuildUserInfo();
        when(jwtService.generateToken(eq(1L), eq("admin@test.com"),
                eq(List.of("Administrator", "SalesOps")), eq(false))).thenReturn("jwt-multi");

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isTrue();
        verify(jwtService).generateToken(eq(1L), eq("admin@test.com"),
                eq(List.of("Administrator", "SalesOps")), eq(false));
    }

    @Test
    void authenticateLocalUser_userWithNoRoles_returnsSuccessWithEmptyRoles() {
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin123!", "$2a$10$hashedpassword")).thenReturn(true);
        mockGetUserRoles(); // no roles
        mockBuildUserInfo();
        when(jwtService.generateToken(eq(1L), eq("admin@test.com"), eq(List.of()), eq(false))).thenReturn("jwt-no-roles");

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isTrue();
        verify(jwtService).generateToken(eq(1L), eq("admin@test.com"), eq(List.of()), eq(false));
    }

    @Test
    void authenticateLocalUser_singleRole_scalarResultHandled() {
        // Verifies fix for single-column native query returning List<String>, not List<Object[]>
        when(userRepository.findByNameIgnoreCase("admin@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin123!", "$2a$10$hashedpassword")).thenReturn(true);
        mockGetUserRoles("Bidder");
        mockBuildUserInfo();
        when(jwtService.generateToken(eq(1L), eq("admin@test.com"), eq(List.of("Bidder")), eq(false))).thenReturn("jwt-bidder");

        LoginResponse response = authService.authenticateLocalUser(loginRequest);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("jwt-bidder");
        verify(jwtService).generateToken(eq(1L), eq("admin@test.com"), eq(List.of("Bidder")), eq(false));
    }

    @Test
    void getCurrentUser_existingUser_returnsUserInfo() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        mockBuildUserInfo();

        LoginResponse.UserInfo info = authService.getCurrentUser(1L);

        assertThat(info).isNotNull();
    }

    @Test
    void getCurrentUser_unknownUser_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // --- Helpers ---

    @SuppressWarnings("unchecked")
    private void mockGetUserRoles(String... roleNames) {
        when(em.createNativeQuery(contains("user_roles"))).thenReturn(rolesQuery);
        when(rolesQuery.setParameter(anyString(), any())).thenReturn(rolesQuery);
        // Single-column native queries return List<Object> (scalars), not List<Object[]>
        List<Object> rows = new java.util.ArrayList<>(List.of(roleNames));
        when(rolesQuery.getResultList()).thenReturn((List) rows);
    }

    @SuppressWarnings("unchecked")
    private void mockBuildUserInfo() {
        when(em.createNativeQuery(contains("ecoatm_direct_users"))).thenReturn(userInfoQuery);
        when(userInfoQuery.setParameter(anyString(), any())).thenReturn(userInfoQuery);
        Object[] infoRow = new Object[]{"Admin", "User", "Admin User", "admin@test.com"};
        List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(infoRow);
        when(userInfoQuery.getResultList()).thenReturn((List) rows);
    }
}

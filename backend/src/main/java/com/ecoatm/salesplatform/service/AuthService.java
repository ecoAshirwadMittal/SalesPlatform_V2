package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.LoginRequest;
import com.ecoatm.salesplatform.dto.LoginResponse;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.UserRepository;
import com.ecoatm.salesplatform.security.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public LoginResponse authenticateLocalUser(LoginRequest request) {
        // Mendix ACT_Login_ExternalUser logic representation

        Optional<User> userOpt = userRepository.findByNameIgnoreCase(request.getEmail());

        if (userOpt.isEmpty()) {
            return new LoginResponse(false, "No account with this email", null);
        }

        User user = userOpt.get();

        if (!user.isActive() || user.isBlocked()) {
            return new LoginResponse(false, "Account is disabled or locked.", null);
        }

        // Validate BCrypt password stored by Mendix
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            List<String> roles = getUserRoles(user.getId());
            String token = jwtService.generateToken(user.getId(), user.getName(), roles, request.isRememberMe());
            LoginResponse resp = new LoginResponse(true, "Authentication successful", token);
            resp.setUser(buildUserInfo(user.getId(), user.getName()));
            return resp;
        } else {
            return new LoginResponse(false, "Incorrect Password", null);
        }
    }

    /**
     * Mendix ACT_GetCurrentUser: retrieves EcoATMDirectUser by userId.
     * Used by SNP_UserInfoDisplay to show name + initials in the top bar.
     */
    @Transactional(readOnly = true)
    public LoginResponse.UserInfo getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return buildUserInfo(user.getId(), user.getName());
    }

    @SuppressWarnings("unchecked")
    private List<String> getUserRoles(Long userId) {
        // Single-column native query returns List<Object> (scalars), not List<Object[]>
        List<Object> rows = em.createNativeQuery(
                "SELECT ur.name FROM identity.user_roles ur " +
                "JOIN identity.user_role_assignments ura ON ura.role_id = ur.id " +
                "WHERE ura.user_id = :userId")
                .setParameter("userId", userId)
                .getResultList();
        return rows.stream().map(r -> (String) r).toList();
    }

    private LoginResponse.UserInfo buildUserInfo(Long userId, String loginName) {
        // Join ecoatm_direct_users + accounts for full name / first / last
        List<Object[]> rows = em.createNativeQuery("""
            SELECT edu.first_name, edu.last_name, a.full_name, a.email
            FROM user_mgmt.ecoatm_direct_users edu
            JOIN identity.accounts a ON a.user_id = edu.user_id
            WHERE edu.user_id = :userId
            """)
                .setParameter("userId", userId)
                .getResultList();

        String firstName = "";
        String lastName = "";
        String fullName = loginName;
        String email = loginName;

        if (!rows.isEmpty()) {
            Object[] row = rows.get(0);
            firstName = row[0] != null ? (String) row[0] : "";
            lastName = row[1] != null ? (String) row[1] : "";
            fullName = row[2] != null ? (String) row[2] : loginName;
            email = row[3] != null ? (String) row[3] : loginName;
        }

        // Mendix initials logic: if(Name = 'MxAdmin') then 'MX' else FirstName[0]+LastName[0]
        String initials;
        if ("MxAdmin".equalsIgnoreCase(loginName)) {
            initials = "MX";
        } else if (!firstName.isEmpty() && !lastName.isEmpty()) {
            initials = ("" + firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
        } else if (fullName != null && !fullName.isBlank()) {
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length >= 2) {
                initials = ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
            } else {
                initials = fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
            }
        } else {
            initials = "??";
        }

        return new LoginResponse.UserInfo(userId, firstName, lastName, fullName, email, initials);
    }
}

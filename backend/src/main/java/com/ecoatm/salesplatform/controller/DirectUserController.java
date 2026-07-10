package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.DirectUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal user-management surface (roles, buyer associations, PII).
 *
 * <p>Administrator-only: this controller can grant role assignments, so anything
 * less than an Administrator gate would be a privilege-escalation vector (a
 * lower-privilege caller could self-grant {@code Administrator} via
 * {@code roleIds}). See security review 2026-07-10 (CR-1).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('Administrator')")
public class DirectUserController {

    private final DirectUserService directUserService;

    @GetMapping("/direct-users")
    public ResponseEntity<DirectUserPageResponse> listDirectUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String buyer,
            @RequestParam(required = false) String roles,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        DirectUserPageResponse response = directUserService.getDirectUsers(
                name, buyer, roles, email, status, page, pageSize);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/direct-users/{userId}")
    public ResponseEntity<DirectUserDetailResponse> getDirectUser(@PathVariable Long userId) {
        return ResponseEntity.ok(directUserService.getDirectUserDetail(userId));
    }

    @PostMapping("/direct-users")
    public ResponseEntity<DirectUserDetailResponse> createDirectUser(
            @RequestBody DirectUserSaveRequest request) {
        return ResponseEntity.ok(directUserService.createDirectUser(request));
    }

    @PutMapping("/direct-users/{userId}")
    public ResponseEntity<DirectUserDetailResponse> updateDirectUser(
            @PathVariable Long userId,
            @RequestBody DirectUserSaveRequest request) {
        return ResponseEntity.ok(directUserService.updateDirectUser(userId, request));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(directUserService.getAllRoles());
    }

    @GetMapping("/buyers")
    public ResponseEntity<List<BuyerResponse>> getAllBuyers() {
        return ResponseEntity.ok(directUserService.getAllBuyers());
    }
}

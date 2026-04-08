package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.DirectUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
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

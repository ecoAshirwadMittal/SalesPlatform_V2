package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // In Mendix, the 'name' field holds the email Address or login identifier
    Optional<User> findByNameIgnoreCase(String name);
}

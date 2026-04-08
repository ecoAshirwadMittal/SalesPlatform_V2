package com.ecoatm.salesplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "identity")
@Getter
@Setter
public class User {
    
    @Id
    private Long id;
    
    private String name;
    
    // Mendix stores the BCrypt hash here
    private String password;
    
    private String userType;
    
    private boolean active;
    
    private boolean blocked;
}

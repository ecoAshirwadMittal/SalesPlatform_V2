package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales_representatives", schema = "buyer_mgmt")
@Getter
@Setter
public class SalesRepresentative {

    // The table's PK is a plain BIGINT with no sequence/identity/default
    // (V8 DDL), so new rows must supply an explicit id — see
    // SalesRepresentativeRepository#nextId. Hence no @GeneratedValue here.
    @Id
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "active")
    private boolean active;

    // Audit columns present in the V8 table but previously unmapped; stamped
    // from the JWT-derived caller id on write (owner on create, changer on
    // update). Both reference identity.users(id).
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "changed_date")
    private LocalDateTime changedDate;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;
}

package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "buyer_codes", schema = "buyer_mgmt")
@Getter
@Setter
public class BuyerCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "buyer_code_type")
    private String buyerCodeType;

    @Column(name = "status")
    private String status;

    @Column(name = "budget")
    private Integer budget;

    @Column(name = "soft_delete")
    private boolean softDelete;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "changed_date")
    private LocalDateTime changedDate;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;
}

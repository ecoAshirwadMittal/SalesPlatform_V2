package com.ecoatm.salesplatform.model.mdm;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "condition", schema = "mdm")
public class Condition extends BaseLookup { }

package com.ecoatm.salesplatform.model.mdm;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "capacity", schema = "mdm")
public class Capacity extends BaseLookup { }

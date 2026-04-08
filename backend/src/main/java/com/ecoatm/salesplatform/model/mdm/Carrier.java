package com.ecoatm.salesplatform.model.mdm;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "carrier", schema = "mdm")
public class Carrier extends BaseLookup { }

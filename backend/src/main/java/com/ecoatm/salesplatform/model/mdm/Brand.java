package com.ecoatm.salesplatform.model.mdm;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "brand", schema = "mdm")
public class Brand extends BaseLookup { }

package com.ecoatm.salesplatform.model.mdm;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "color", schema = "mdm")
public class Color extends BaseLookup { }

-- ==============================================================================
-- Migration: V13__create_mdm_schema.sql
-- Description: Flattens and normalizes the Legacy ecoatm_pwsmdm module.
-- ==============================================================================

CREATE SCHEMA IF NOT EXISTS mdm;

-- ---------------------------------------------------------
-- Lookup / Dictionary Tables
-- ---------------------------------------------------------
CREATE TABLE mdm.brand (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

CREATE TABLE mdm.category (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

CREATE TABLE mdm.model (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

CREATE TABLE mdm.condition (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

CREATE TABLE mdm.capacity (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

CREATE TABLE mdm.carrier (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

CREATE TABLE mdm.color (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

CREATE TABLE mdm.grade (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    name VARCHAR(200),
    display_name VARCHAR(200),
    is_enabled BOOLEAN DEFAULT true,
    sort_rank INTEGER
);

-- ---------------------------------------------------------
-- Core Device Table (Flattened Constraints)
-- ---------------------------------------------------------
CREATE TABLE mdm.device (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    
    sku VARCHAR(200) UNIQUE,
    device_code VARCHAR(100),
    description VARCHAR(500),
    
    -- Pricing and Inventory
    list_price NUMERIC(10, 2) DEFAULT 0.00,
    min_price NUMERIC(10, 2) DEFAULT 0.00,
    future_list_price NUMERIC(10, 2) DEFAULT 0.00,
    future_min_price NUMERIC(10, 2) DEFAULT 0.00,
    
    available_qty INTEGER DEFAULT 0,
    reserved_qty INTEGER DEFAULT 0,
    atp_qty INTEGER DEFAULT 0,
    weight NUMERIC(10, 4) DEFAULT 0.0000,
    item_type VARCHAR(100),
    
    is_active BOOLEAN DEFAULT true,
    
    -- Flattened Foreign Keys (replaces 8 junction tables)
    brand_id BIGINT REFERENCES mdm.brand(id),
    category_id BIGINT REFERENCES mdm.category(id),
    model_id BIGINT REFERENCES mdm.model(id),
    condition_id BIGINT REFERENCES mdm.condition(id),
    capacity_id BIGINT REFERENCES mdm.capacity(id),
    carrier_id BIGINT REFERENCES mdm.carrier(id),
    color_id BIGINT REFERENCES mdm.color(id),
    grade_id BIGINT REFERENCES mdm.grade(id),
    
    last_sync_time TIMESTAMP,
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------------------
-- Price History Traceability
-- ---------------------------------------------------------
CREATE TABLE mdm.price_history (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    device_id BIGINT REFERENCES mdm.device(id) ON DELETE CASCADE,
    
    list_price NUMERIC(10, 2),
    min_price NUMERIC(10, 2),
    expiration_date TIMESTAMP,
    
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_mdm_device_sku ON mdm.device(sku);
CREATE INDEX idx_mdm_price_history_device ON mdm.price_history(device_id);

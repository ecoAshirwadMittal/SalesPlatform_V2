-- ==============================================================================
-- Migration: V14__create_integration_schema.sql
-- Description: Centralizes Oracle/Deposco integration state, logging, and error maps
-- within an isolated schema domain.
-- ==============================================================================

CREATE SCHEMA IF NOT EXISTS integration;

-- ---------------------------------------------------------
-- Oracle Configuration
-- ---------------------------------------------------------
CREATE TABLE integration.oracle_config (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    username VARCHAR(500),
    password_hash VARCHAR(500),         -- Handled explicitly by SecretManager in production
    auth_path TEXT,
    create_order_path TEXT,
    create_rma_path TEXT,
    timeout_ms INTEGER DEFAULT 5000,
    is_active BOOLEAN DEFAULT true,
    updated_date TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------------------
-- Deposco Configuration
-- ---------------------------------------------------------
CREATE TABLE integration.deposco_config (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    base_url TEXT,
    username VARCHAR(500),
    password_hash VARCHAR(500),
    last_sync_time TIMESTAMP,
    timeout_ms INTEGER DEFAULT 5000,
    is_active BOOLEAN DEFAULT true,
    updated_date TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------------------
-- Tokens
-- ---------------------------------------------------------
CREATE TABLE integration.api_token (
    id BIGSERIAL PRIMARY KEY,
    system_target VARCHAR(100),         -- E.g., 'ORACLE_AUTH'
    access_token TEXT,
    expiration_date TIMESTAMP,
    created_date TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------------------
-- Error Mappings (Translating Oracle to Human UX)
-- ---------------------------------------------------------
CREATE TABLE integration.error_mapping (
    id BIGSERIAL PRIMARY KEY,
    source_system VARCHAR(100),
    source_error_code VARCHAR(100),
    source_error_type VARCHAR(100),
    user_error_code VARCHAR(100),
    user_error_message TEXT,
    bypass_for_user BOOLEAN DEFAULT false
);

-- ---------------------------------------------------------
-- API Logs Cache
-- Note: A Scheduled Spring Task will prune data > 30 days
-- ---------------------------------------------------------
CREATE TABLE integration.api_log (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    
    system_target VARCHAR(100),         -- E.g., 'ORACLE', 'DEPOSCO'
    method VARCHAR(20),
    url TEXT,
    
    request_payload TEXT,
    response_payload TEXT,
    
    http_status VARCHAR(50),
    is_successful BOOLEAN,
    error_message TEXT,
    stack_trace TEXT,
    
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms INTEGER,
    
    created_date TIMESTAMP DEFAULT NOW()
);

-- Performance tuning
CREATE INDEX idx_integration_api_log_date ON integration.api_log(created_date);
CREATE INDEX idx_integration_api_log_system ON integration.api_log(system_target);

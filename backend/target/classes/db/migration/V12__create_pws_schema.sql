-- ==============================================================================
-- Migration: V12__create_pws_schema.sql
-- Description: Unifies the fragmented Mendix ecoatm_pws$ modules into a robust 
-- PostgreSQL relational architecture under the `pws` schema.
-- Integrates: buyer_offer, offer, order, shipment_detail into clean flows.
-- ==============================================================================

CREATE SCHEMA IF NOT EXISTS pws;

-- ---------------------------------------------------------
-- 1. Unified Offer Table
-- Replaces: ecoatm_pws$offer, ecoatm_pws$buyeroffer
-- ---------------------------------------------------------
CREATE TABLE pws.offer (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,           -- Retained Mendix BigInt for smooth data import referencing
    offer_type VARCHAR(50) NOT NULL,   -- 'SYSTEM' or 'BUYER'
    status VARCHAR(50),                -- E.g., 'Pending_Order', 'Sales_Review', 'Declined'
    
    total_qty INTEGER DEFAULT 0,
    total_price NUMERIC(14, 2) DEFAULT 0.00,
    
    buyer_code_id BIGINT,              -- FK conceptually linked to buyer management module
    sales_rep_id BIGINT,               -- FK conceptually linked to administration/reps
    
    submission_date TIMESTAMP,
    sales_review_completed_on TIMESTAMP,
    canceled_on TIMESTAMP,
    
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------------------
-- 2. Unified Offer Item Table
-- Replaces: ecoatm_pws$offeritem, ecoatm_pws$buyerofferitem
-- ---------------------------------------------------------
CREATE TABLE pws.offer_item (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    offer_id BIGINT NOT NULL REFERENCES pws.offer(id) ON DELETE CASCADE,
    
    sku VARCHAR(200),
    device_id BIGINT,                  -- FK conceptually linked to MDM devices
    
    quantity INTEGER DEFAULT 0,
    price NUMERIC(14, 2) DEFAULT 0.00,
    total_price NUMERIC(14, 2) DEFAULT 0.00,
    
    -- Counter tracking for negotiations
    counter_qty INTEGER DEFAULT 0,
    counter_price NUMERIC(14, 2) DEFAULT 0.00,
    counter_total NUMERIC(14, 2) DEFAULT 0.00,
    
    item_status VARCHAR(50),           -- E.g., 'Counter', 'Finalize', 'Decline'
    
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------------------
-- 3. Unified Order Table
-- Replaces: ecoatm_pws$order
-- ---------------------------------------------------------
CREATE TABLE pws.order (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    offer_id BIGINT REFERENCES pws.offer(id),
    
    order_number VARCHAR(100) UNIQUE,
    order_line VARCHAR(100),
    
    order_status VARCHAR(500),          -- Consolidated local status tracking
    oracle_status VARCHAR(500),        -- Tracking raw Oracle downstream strings
    ship_method VARCHAR(200),
    
    shipped_total_qty INTEGER DEFAULT 0,
    shipped_total_price NUMERIC(14, 2) DEFAULT 0.00,
    
    order_date TIMESTAMP,
    ship_date TIMESTAMP,
    
    oracle_http_code INTEGER,          -- Integration tracking
    
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------------------
-- 4. Unified Shipment Detail Table
-- Replaces: ecoatm_pws$shipmentdetail
-- ---------------------------------------------------------
CREATE TABLE pws.shipment_detail (
    id BIGSERIAL PRIMARY KEY,
    legacy_id BIGINT UNIQUE,
    order_id BIGINT REFERENCES pws.order(id) ON DELETE CASCADE,
    
    tracking_number VARCHAR(1000),
    tracking_url VARCHAR(2000),
    
    sku_count INTEGER DEFAULT 0,
    quantity INTEGER DEFAULT 0,
    
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_pws_offer_buyer_code ON pws.offer(buyer_code_id);
CREATE INDEX idx_pws_offer_item_offer_id ON pws.offer_item(offer_id);
CREATE INDEX idx_pws_order_offer_id ON pws.order(offer_id);
CREATE INDEX idx_pws_shipment_detail_order_id ON pws.shipment_detail(order_id);

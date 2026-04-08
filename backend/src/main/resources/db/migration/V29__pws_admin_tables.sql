-- ==============================================================================
-- Migration: V29__pws_admin_tables.sql
-- Description: Admin config tables for PWS Control Center sub-pages.
--   Tables: feature_flag, pws_constants, order_status_config,
--           maintenance_mode, rma_status, rma_template, rma_reason,
--           navigation_menu
-- ==============================================================================

-- ---------------------------------------------------------
-- Feature Flags (Mendix: eco_core$pwsfeatureflag)
-- ---------------------------------------------------------
CREATE TABLE pws.feature_flag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    active BOOLEAN DEFAULT false,
    description TEXT,
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.feature_flag (name, active, description) VALUES
('PWS_COUNTER_OFFER', true, 'Enable counter offer flow for sales review'),
('PWS_RMA_CREATE', true, 'Enable RMA creation from orders'),
('PWS_BULK_UPLOAD', true, 'Enable CSV bulk offer upload'),
('PWS_DEPOSCO_SYNC', true, 'Enable Deposco inventory sync'),
('PWS_SNOWFLAKE_SYNC', false, 'Enable Snowflake data warehouse sync'),
('PWS_EMAIL_NOTIFICATIONS', true, 'Enable email notifications for offers/orders'),
('PWS_ATP_CHECK', true, 'Enable Available-To-Promise quantity validation'),
('PWS_SLA_TAGS', true, 'Enable SLA tagging for overdue offers'),
('PWS_MAINTENANCE_MODE', false, 'Enable maintenance mode banner'),
('PWS_GRADING_MODULE', true, 'Enable grading module for buyers'),
('PWS_CASE_LOT', false, 'Enable case lot purchasing'),
('PWS_PRICE_HISTORY', true, 'Show price history on inventory'),
('PWS_BUYER_COUNTER', true, 'Enable buyer counter response flow'),
('PWS_ORDER_TRACKING', true, 'Enable order shipment tracking');

-- ---------------------------------------------------------
-- PWS Constants (Mendix: ecoatm_pws$pwsconstants)
-- ---------------------------------------------------------
CREATE TABLE pws.pws_constants (
    id BIGSERIAL PRIMARY KEY,
    sla_days INTEGER DEFAULT 2,
    sales_email VARCHAR(500),
    send_first_reminder BOOLEAN DEFAULT true,
    send_second_reminder BOOLEAN DEFAULT true,
    hours_first_counter_reminder INTEGER DEFAULT 24,
    hours_second_counter_reminder INTEGER DEFAULT 48,
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.pws_constants (sla_days, sales_email, send_first_reminder, send_second_reminder, hours_first_counter_reminder, hours_second_counter_reminder) VALUES
(2, 'pws-sales@ecoatm.com', true, true, 24, 48);

-- ---------------------------------------------------------
-- Order Status Config (Mendix: ecoatm_pws$orderstatus)
-- ---------------------------------------------------------
CREATE TABLE pws.order_status_config (
    id BIGSERIAL PRIMARY KEY,
    system_status VARCHAR(200) NOT NULL,
    internal_status_text VARCHAR(500),
    external_status_text VARCHAR(500),
    internal_hex_code VARCHAR(100),
    external_hex_code VARCHAR(100),
    description TEXT,
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.order_status_config (system_status, internal_status_text, external_status_text, internal_hex_code, external_hex_code, description) VALUES
('DRAFT', 'Draft', 'Draft', 'pws_bgcolor_grey', 'pws_bgcolor_grey', 'Initial offer state in cart'),
('SUBMITTED', 'Submitted', 'Submitted', 'pws_bgcolor_blue', 'pws_bgcolor_blue', 'Offer submitted, awaiting review'),
('SALES_REVIEW', 'Sales Review', 'Under Review', 'pws_bgcolor_orange', 'pws_bgcolor_orange', 'Offer needs sales team review'),
('BUYER_ACCEPTANCE', 'Buyer Acceptance', 'Counter Offer', 'pws_bgcolor_peach', 'pws_bgcolor_peach', 'Counter offer sent to buyer'),
('PENDING_ORDER', 'Pending Order', 'Processing', 'pws_bgcolor_yellow', 'pws_bgcolor_yellow', 'Order sent to Oracle, awaiting confirmation'),
('ORDERED', 'Ordered', 'Confirmed', 'pws_bgcolor_green', 'pws_bgcolor_green', 'Order confirmed by Oracle'),
('SHIPPED', 'Shipped', 'Shipped', 'pws_bgcolor_teal', 'pws_bgcolor_teal', 'Order has shipped'),
('DELIVERED', 'Delivered', 'Delivered', 'pws_bgcolor_green', 'pws_bgcolor_green', 'Order delivered'),
('DECLINED', 'Declined', 'Declined', 'pws_bgcolor_red', 'pws_bgcolor_red', 'Offer declined by sales'),
('CANCELLED', 'Cancelled', 'Cancelled', 'pws_bgcolor_red', 'pws_bgcolor_red', 'Order cancelled'),
('PENDING_REVIEW', 'Pending Review', 'Under Review', 'pws_bgcolor_orange', 'pws_bgcolor_orange', 'Pending internal review');

-- ---------------------------------------------------------
-- Maintenance Mode (Mendix: ecoatm_pws$maintenancemode)
-- ---------------------------------------------------------
CREATE TABLE pws.maintenance_mode (
    id BIGSERIAL PRIMARY KEY,
    is_enabled BOOLEAN DEFAULT false,
    banner_start_time TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    banner_title VARCHAR(500),
    banner_message TEXT,
    page_title VARCHAR(500),
    page_header VARCHAR(500),
    page_message TEXT,
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.maintenance_mode (is_enabled, banner_title, banner_message, page_title, page_header, page_message) VALUES
(false, 'Scheduled Maintenance', 'The system will be undergoing maintenance shortly.', 'Maintenance Mode', 'We''ll be right back', 'The system is currently undergoing scheduled maintenance. Please try again later.');

-- ---------------------------------------------------------
-- RMA Status (Mendix: ecoatm_rma$rmastatus)
-- ---------------------------------------------------------
CREATE TABLE pws.rma_status (
    id BIGSERIAL PRIMARY KEY,
    sort_order INTEGER,
    system_status VARCHAR(200) NOT NULL,
    internal_status_text VARCHAR(500),
    external_status_text VARCHAR(500),
    status_group VARCHAR(200),
    bidder_message TEXT,
    description TEXT,
    is_default BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.rma_status (sort_order, system_status, internal_status_text, external_status_text, status_group, description, is_default) VALUES
(1, 'DRAFT', 'Draft', 'Draft', 'Open', 'RMA created but not submitted', true),
(2, 'SUBMITTED', 'Submitted', 'Submitted', 'Open', 'RMA submitted for review', false),
(3, 'APPROVED', 'Approved', 'Approved', 'Open', 'RMA approved by sales', false),
(4, 'PENDING_SHIPMENT', 'Pending Shipment', 'Awaiting Shipment', 'Open', 'Waiting for buyer to ship', false),
(5, 'SHIPPED', 'Shipped', 'Shipped', 'In Progress', 'Buyer has shipped items', false),
(6, 'RECEIVED', 'Received', 'Received', 'In Progress', 'Items received at warehouse', false),
(7, 'PROCESSING', 'Processing', 'Processing', 'In Progress', 'RMA being processed', false),
(8, 'COMPLETED', 'Completed', 'Completed', 'Closed', 'RMA completed, credit issued', false),
(9, 'DECLINED', 'Declined', 'Declined', 'Closed', 'RMA request declined', false);

-- ---------------------------------------------------------
-- RMA Template (Mendix: ecoatm_rma$rmatemplate)
-- ---------------------------------------------------------
CREATE TABLE pws.rma_template (
    id BIGSERIAL PRIMARY KEY,
    template_name VARCHAR(500) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    file_name VARCHAR(500),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.rma_template (template_name, is_active, file_name) VALUES
('Default RMA Template', true, 'rma_default_template.xlsx');

-- ---------------------------------------------------------
-- RMA Reasons (Mendix: ecoatm_rma$rmareasons)
-- ---------------------------------------------------------
CREATE TABLE pws.rma_reason (
    id BIGSERIAL PRIMARY KEY,
    valid_reason VARCHAR(500) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.rma_reason (valid_reason, is_active) VALUES
('Defective Battery/Lower 69%', true),
('Gaps/Parts Fit/Screen Lift', true),
('Defective Charger/Data Port', true),
('Defective Buttons/Switches', true),
('Defective Speaker/Microphone', true),
('Defective Camera', true),
('Cracked/Broken Screen', true),
('Water Damage', true),
('Wrong Item Received', true),
('Missing Accessories', true),
('Cosmetic Damage Beyond Grade', true),
('Device Not Powering On', true),
('Software/Firmware Issue', true),
('Network Lock Issue', true),
('iCloud/FRP Lock', true),
('Other', true);

-- ---------------------------------------------------------
-- Navigation Menu (Mendix: ecoatm_pws$navigationmenu)
-- ---------------------------------------------------------
CREATE TABLE pws.navigation_menu (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    sort_order INTEGER,
    loading_message VARCHAR(500),
    microflow_name VARCHAR(500),
    icon_css_class VARCHAR(200),
    user_group VARCHAR(200),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

INSERT INTO pws.navigation_menu (name, is_active, sort_order, loading_message, icon_css_class, user_group) VALUES
('Shop', true, 1, 'Loading inventory...', 'mx-icon-shop', 'Buyer'),
('Counters', true, 2, 'Loading counters...', 'mx-icon-counters', 'Buyer'),
('Orders', true, 3, 'Loading orders...', 'mx-icon-orders', 'Buyer'),
('RMAs', true, 4, 'Loading RMAs...', 'mx-icon-rma', 'Buyer'),
('FAQ''s', true, 5, 'Loading FAQs...', 'mx-icon-faq', 'Buyer'),
('Grading', true, 6, 'Loading grading...', 'mx-icon-grading', 'Buyer'),
('Inventory', true, 1, 'Loading inventory...', 'mx-icon-inventory', 'Sales'),
('Offer Review', true, 2, 'Loading offers...', 'mx-icon-offer-review', 'Sales'),
('RMA Review', true, 3, 'Loading RMA review...', 'mx-icon-rma-review', 'Sales'),
('Pricing', true, 4, 'Loading pricing...', 'mx-icon-pricing', 'Sales');

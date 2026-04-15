-- =============================================================================
-- V49: Add missing indexes for query performance
--
-- Findings 8, 9, 15 from database remediation plan.
-- =============================================================================

-- Finding 8: offer status filtering (used by OfferReviewService, SLA queries)
CREATE INDEX IF NOT EXISTS idx_offer_status ON pws.offer(status);

-- Finding 15: offer listing ORDER BY updated_date DESC
CREATE INDEX IF NOT EXISTS idx_offer_updated_date ON pws.offer(updated_date DESC);

-- Composite for common query pattern: filter by status + sort by updated_date
CREATE INDEX IF NOT EXISTS idx_offer_status_updated ON pws.offer(status, updated_date DESC);

-- Finding 9: mdm.device FK columns for pricing filter JOINs
-- These 8 FK columns are used in every pricing page query via multi-table JOINs
CREATE INDEX IF NOT EXISTS idx_device_brand_id ON mdm.device(brand_id);
CREATE INDEX IF NOT EXISTS idx_device_category_id ON mdm.device(category_id);
CREATE INDEX IF NOT EXISTS idx_device_model_id ON mdm.device(model_id);
CREATE INDEX IF NOT EXISTS idx_device_condition_id ON mdm.device(condition_id);
CREATE INDEX IF NOT EXISTS idx_device_capacity_id ON mdm.device(capacity_id);
CREATE INDEX IF NOT EXISTS idx_device_carrier_id ON mdm.device(carrier_id);
CREATE INDEX IF NOT EXISTS idx_device_color_id ON mdm.device(color_id);
CREATE INDEX IF NOT EXISTS idx_device_grade_id ON mdm.device(grade_id);

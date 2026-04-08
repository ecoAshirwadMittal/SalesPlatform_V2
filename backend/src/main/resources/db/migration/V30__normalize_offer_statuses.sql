-- Normalize offer statuses to Mendix ENUM convention (Mixed_Case)
-- Legacy migrated data already uses this convention; only new code wrote UPPER_CASE variants.

-- Offer status normalization
UPDATE pws.offer SET status = 'Draft'            WHERE status = 'DRAFT';
UPDATE pws.offer SET status = 'Sales_Review'     WHERE status = 'SALES_REVIEW';
UPDATE pws.offer SET status = 'Submitted'        WHERE status = 'SUBMITTED';
UPDATE pws.offer SET status = 'Pending_Review'   WHERE status = 'PENDING_REVIEW';

-- Offer item status normalization (DRAFT → Draft to match Accept/Counter/Decline/Finalize)
UPDATE pws.offer_item SET item_status = 'Draft'  WHERE item_status = 'DRAFT';

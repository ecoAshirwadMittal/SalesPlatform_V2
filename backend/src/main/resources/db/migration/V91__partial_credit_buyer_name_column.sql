-- ---------------------------------------------------------------------
-- V91 — Partial Credit, buyer contact name column.
--
-- Figma parity fix campaign (2026-05-12) flagged that the admin landing
-- + admin review HeaderStrip need a separate "Buyer" (contact name)
-- field distinct from the existing "Company" (party_name) field. Today
-- the DTO conflates them by rendering party_name in both cells.
--
-- Single column, nullable. Buyer flow populates it from the resolved
-- contact when the rep / buyer enters the wizard; legacy rows stay
-- NULL and the UI falls back to "—" until the row is re-saved.
-- ---------------------------------------------------------------------

ALTER TABLE partial_credit.credit_requests
    ADD COLUMN buyer_name VARCHAR(200);

COMMENT ON COLUMN partial_credit.credit_requests.buyer_name IS
    'Buyer contact name (e.g. "Jonathan Wildermeyer") — distinct from party_name (company). Nullable; legacy rows show "—" in the admin UI.';

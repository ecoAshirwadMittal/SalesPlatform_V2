-- Phase 12: Buyer User Guide admin upload
-- Stores uploaded PDF files for the bidder-facing "Buyer User Guide" sidebar link.
-- Only one row may have is_active = TRUE at a time (partial unique index).

CREATE SCHEMA IF NOT EXISTS admin;

CREATE TABLE admin.buyer_user_guide (
    id           BIGSERIAL PRIMARY KEY,
    file_name    VARCHAR(255)  NOT NULL,
    file_path    VARCHAR(512)  NOT NULL,
    content_type VARCHAR(100)  NOT NULL DEFAULT 'application/pdf',
    byte_size    BIGINT        NOT NULL,
    uploaded_by  BIGINT        NOT NULL REFERENCES identity.users(id),
    uploaded_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    is_active    BOOLEAN       NOT NULL DEFAULT FALSE,
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE
);

-- Enforce at most one active guide at a time.
CREATE UNIQUE INDEX uq_buyer_user_guide_active
    ON admin.buyer_user_guide (is_active)
    WHERE is_active = TRUE;

-- =============================================================================
-- V62: Auctions module — reporting, document blobs, and submit logs
-- Sources:
--   auctionui$allbiddownload (17,776) + allbiddownload_2 (4,335,383)
--     → merged into auctions.all_bid_downloads with format_version (Q2)
--   auctionui$allbidsdoc (1,982), auctionui$biddatadoc (18,858)
--     → auctions.all_bids_documents, auctions.bid_data_documents
--       (bytea blob mirroring Mendix FileDocument blobstore — Q4)
--   auctionui$roundthreebuyersdatareport (1,958, + collapsed _auction junction)
--   auctionui$bidsubmitlog (24,402, + collapsed _bidround junction)
--
-- Design notes:
--   • allbiddownload merge uses format_version = 1 (legacy) or 2 (v2). The
--     unified column set is the superset; v1 rows leave v2-only columns NULL
--     and vice versa.
--   • Blob documents follow Mendix FileDocument convention: bytea blob +
--     file_name / file_size / mime_type / has_contents metadata. Parent FK
--     collapsed to a column (not junction) since every doc belongs to
--     exactly one parent in Mendix.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- auctions.all_bid_downloads (merged v1 + v2 format)
-- Source: auctionui$allbiddownload + auctionui$allbiddownload_2
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.all_bid_downloads (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    format_version          SMALLINT        NOT NULL,

    -- Device identity (superset of both formats)
    ecoid                   VARCHAR(100),
    device_id               VARCHAR(100),        -- Mendix string UUID
    name                    VARCHAR(512),
    brand                   VARCHAR(200),
    model                   VARCHAR(200),
    carrier                 VARCHAR(200),
    category                VARCHAR(100),
    merged_grade            VARCHAR(30),

    -- Quantities + payout
    total_quantity          INTEGER,
    bid_quantity            INTEGER,
    bid_amount              NUMERIC(14, 2),
    target_price            NUMERIC(14, 4),
    payout                  NUMERIC(14, 2),

    -- Buyer + round context
    buyer_code              VARCHAR(100),
    company_name            VARCHAR(500),
    bid_round               INTEGER,
    week_id                 INTEGER,
    bid_rank                INTEGER,
    highest_bid             BOOLEAN,

    -- v2-only fields (nullable for v1 rows)
    round2_bid_rank         INTEGER,
    round3_bid_rank         INTEGER,
    r2_po_max_bid           NUMERIC(14, 2),
    r3_po_max_bid           NUMERIC(14, 2),
    submitted_datetime      TIMESTAMPTZ,

    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_abd_format_version CHECK (format_version IN (1, 2))
);

COMMENT ON TABLE  auctions.all_bid_downloads IS 'Unified bid download report rows — merges legacy auctionui$allbiddownload + allbiddownload_2 via format_version discriminator';
COMMENT ON COLUMN auctions.all_bid_downloads.format_version IS '1 = auctionui$allbiddownload (legacy); 2 = auctionui$allbiddownload_2 (current)';

CREATE INDEX idx_abd_format        ON auctions.all_bid_downloads(format_version);
CREATE INDEX idx_abd_ecoid_round   ON auctions.all_bid_downloads(ecoid, bid_round);
CREATE INDEX idx_abd_company       ON auctions.all_bid_downloads(company_name);

-- ---------------------------------------------------------------------------
-- auctions.all_bids_documents (bytea blob)
-- Source: auctionui$allbidsdoc (1,982 rows)
-- Parent FK: all_bid_downloads.id (collapsed from junction)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.all_bids_documents (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    all_bid_download_id     BIGINT          REFERENCES auctions.all_bid_downloads(id) ON DELETE CASCADE,
    file_name               VARCHAR(500),
    file_size               BIGINT,
    mime_type               VARCHAR(200),
    has_contents            BOOLEAN         NOT NULL DEFAULT false,
    blob                    BYTEA,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  auctions.all_bids_documents IS 'Per-auction bid export document blob (auctionui$allbidsdoc). Mirrors Mendix FileDocument blobstore convention.';
COMMENT ON COLUMN auctions.all_bids_documents.blob IS 'Document bytes — matches Mendix internal FileDocument blobstore';
COMMENT ON COLUMN auctions.all_bids_documents.has_contents IS 'True when blob is populated — matches Mendix FileDocument.HasContents';

CREATE INDEX idx_abdoc_parent ON auctions.all_bids_documents(all_bid_download_id);

-- ---------------------------------------------------------------------------
-- auctions.bid_data_documents (bytea blob)
-- Source: auctionui$biddatadoc (18,858 rows)
-- Parent FK: bid_rounds.id (documents are attached to bid rounds in practice)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.bid_data_documents (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    bid_round_id            BIGINT          REFERENCES auctions.bid_rounds(id) ON DELETE CASCADE,
    file_name               VARCHAR(500),
    file_size               BIGINT,
    mime_type               VARCHAR(200),
    has_contents            BOOLEAN         NOT NULL DEFAULT false,
    blob                    BYTEA,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE auctions.bid_data_documents IS 'Per-bid-round bid data export document blob (auctionui$biddatadoc). Mirrors Mendix FileDocument blobstore.';

CREATE INDEX idx_bddoc_parent ON auctions.bid_data_documents(bid_round_id);

-- ---------------------------------------------------------------------------
-- auctions.round3_buyer_data_reports (Round 3 submission audit)
-- Source: auctionui$roundthreebuyersdatareport (1,958 rows)
-- Collapsed: auctionui$roundthreebuyersdatareport_auction (740/1,958) → auction_id
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.round3_buyer_data_reports (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    auction_id              BIGINT          REFERENCES auctions.auctions(id) ON DELETE SET NULL,
    buyer_code              VARCHAR(100),
    company_name            VARCHAR(500),
    total_quantity          INTEGER,
    total_payout            NUMERIC(16, 2),
    report_json             TEXT,
    submitted_datetime      TIMESTAMPTZ,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  auctions.round3_buyer_data_reports IS 'Round 3 per-buyer submission audit (auctionui$roundthreebuyersdatareport)';
COMMENT ON COLUMN auctions.round3_buyer_data_reports.auction_id IS 'Parent auction — collapsed from auctionui$roundthreebuyersdatareport_auction junction (sparse 740/1958 in prod)';

CREATE INDEX idx_r3rep_auction ON auctions.round3_buyer_data_reports(auction_id);

-- ---------------------------------------------------------------------------
-- auctions.bid_submit_log (bid submit retry log)
-- Source: auctionui$bidsubmitlog (24,402 rows)
-- Collapsed: auctionui$bidsubmitlog_bidround (3,850/24,402) → bid_round_id
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.bid_submit_log (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    bid_round_id            BIGINT          REFERENCES auctions.bid_rounds(id) ON DELETE SET NULL,
    submit_action           VARCHAR(30)     NOT NULL,
    status                  VARCHAR(20)     NOT NULL,
    message                 TEXT,
    attempt_number          INTEGER         NOT NULL DEFAULT 1,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_bsl_action CHECK (submit_action IN ('User_Submit','Push_To_Sharepoint')),
    CONSTRAINT chk_bsl_status CHECK (status IN ('Success','Error'))
);

COMMENT ON TABLE  auctions.bid_submit_log IS 'Bid submit / sharepoint push retry log (auctionui$bidsubmitlog)';
COMMENT ON COLUMN auctions.bid_submit_log.bid_round_id IS 'Referenced bid round — collapsed from auctionui$bidsubmitlog_bidround (sparse 3850/24402)';

CREATE INDEX idx_bsl_bid_round ON auctions.bid_submit_log(bid_round_id);
CREATE INDEX idx_bsl_created   ON auctions.bid_submit_log(created_date DESC);
CREATE INDEX idx_bsl_errors    ON auctions.bid_submit_log(status, created_date DESC) WHERE status = 'Error';

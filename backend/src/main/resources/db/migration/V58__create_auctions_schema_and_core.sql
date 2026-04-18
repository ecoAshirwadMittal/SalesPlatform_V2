-- =============================================================================
-- V58: Auctions module — schema + core (auctions, scheduling_auctions)
-- Source: auctionui$auction, auctionui$schedulingauction (+ collapsed junctions
--         auctionui$auction_week, auctionui$schedulingauction_auction)
-- Pre-req: mdm.week (ported from ecoatm_mdm$week — not created by V13)
--
-- Design notes:
--   • N:1 Mendix junctions collapsed to direct FK columns (see
--     docs/tasks/auctions-schema-migration-plan.md §3.1).
--   • BIGSERIAL + legacy_id pattern (V13) — IDs are remapped, not preserved.
--   • TIMESTAMPTZ throughout (aligned with V50).
--   • Enum values stored as VARCHAR with CHECK constraints (verified against
--     qa-0407 on 2026-04-15; see plan §6).
-- =============================================================================

-- ---------------------------------------------------------------------------
-- Pre-req: mdm.week (port of ecoatm_mdm$week, 157 rows)
-- Referenced by auctions.auctions, auctions.bid_rounds,
-- auctions.aggregated_inventory, auctions.aggregated_inventory_totals.
-- ---------------------------------------------------------------------------
CREATE TABLE mdm.week (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    week_id                 BIGINT,              -- Mendix weekid (business key, not the PK)
    year                    INTEGER,
    week_number             INTEGER,
    week_start_datetime     TIMESTAMPTZ,
    week_end_datetime       TIMESTAMPTZ,
    week_display            VARCHAR(200),        -- "2024 / Wk01"
    week_display_short      VARCHAR(200),        -- "Wk01"
    week_number_string      VARCHAR(200),
    auction_data_purged     BOOLEAN         NOT NULL DEFAULT false,
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE  mdm.week IS 'Weekly calendar reference used by the auctions module (ecoatm_mdm$week)';
COMMENT ON COLUMN mdm.week.week_id IS 'Mendix weekid — business-key week identifier, distinct from surrogate PK';
COMMENT ON COLUMN mdm.week.auction_data_purged IS 'True once old auction data for this week has been purged to Snowflake';

CREATE INDEX idx_mdm_week_year_number    ON mdm.week(year, week_number);
CREATE INDEX idx_mdm_week_start_datetime ON mdm.week(week_start_datetime);

-- ---------------------------------------------------------------------------
-- Schema: auctions
-- ---------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS auctions;
COMMENT ON SCHEMA auctions IS 'Weekly auction event, bid rounds, aggregated inventory, bid data facts (auctionui module)';

-- ---------------------------------------------------------------------------
-- auctions.auctions (one row per weekly auction event)
-- Source: auctionui$auction (5 rows in prod)
-- Collapsed junction: auctionui$auction_week → week_id FK column (1:1 in prod)
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.auctions (
    id                      BIGSERIAL       PRIMARY KEY,
    legacy_id               BIGINT          UNIQUE,
    auction_title           VARCHAR(200),
    auction_status          VARCHAR(20)     NOT NULL DEFAULT 'Unscheduled',
    week_id                 BIGINT          REFERENCES mdm.week(id),
    created_by              VARCHAR(200),        -- Mendix username (text; not a user FK)
    updated_by              VARCHAR(200),
    created_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id),
    CONSTRAINT chk_auctions_status CHECK (auction_status IN ('Unscheduled','Scheduled','Started','Closed'))
);

COMMENT ON TABLE  auctions.auctions IS 'Weekly auction event — parent of 3 scheduling_auctions rounds (auctionui$auction)';
COMMENT ON COLUMN auctions.auctions.auction_status IS 'Enum_AuctionStatus: Unscheduled | Scheduled | Started | Closed';
COMMENT ON COLUMN auctions.auctions.week_id IS 'Target week — collapsed from auctionui$auction_week junction (always 1:1 in prod)';

CREATE INDEX idx_auctions_status   ON auctions.auctions(auction_status);
CREATE INDEX idx_auctions_week     ON auctions.auctions(week_id);

-- ---------------------------------------------------------------------------
-- auctions.scheduling_auctions (round-level timing + notification state)
-- Source: auctionui$schedulingauction (15 rows = 3 per auction)
-- Collapsed junction: auctionui$schedulingauction_auction → auction_id FK
-- ---------------------------------------------------------------------------
CREATE TABLE auctions.scheduling_auctions (
    id                              BIGSERIAL       PRIMARY KEY,
    legacy_id                       BIGINT          UNIQUE,
    auction_id                      BIGINT          NOT NULL REFERENCES auctions.auctions(id) ON DELETE CASCADE,
    name                            VARCHAR(200),
    round                           INTEGER         NOT NULL,
    auction_week_year               VARCHAR(50),         -- denormalized year/week label used in UI
    start_datetime                  TIMESTAMPTZ,
    end_datetime                    TIMESTAMPTZ,
    round_status                    VARCHAR(20)     NOT NULL DEFAULT 'Unscheduled',
    round3_init_status              VARCHAR(20)     NOT NULL DEFAULT 'Pending',
    email_reminders                 VARCHAR(20)     NOT NULL DEFAULT 'NoneSent',
    has_round                       BOOLEAN         NOT NULL DEFAULT true,
    is_start_notification_sent      BOOLEAN         NOT NULL DEFAULT false,
    is_end_notification_sent        BOOLEAN         NOT NULL DEFAULT false,
    is_reminder_notification_sent   BOOLEAN         NOT NULL DEFAULT false,
    notifications_enabled           BOOLEAN         NOT NULL DEFAULT true,
    snowflake_json                  TEXT,                -- audit payload pushed to Snowflake
    created_by                      VARCHAR(200),
    updated_by                      VARCHAR(200),
    created_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date                    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                        BIGINT          REFERENCES identity.users(id),
    changed_by_id                   BIGINT          REFERENCES identity.users(id),
    CONSTRAINT chk_sa_round_status   CHECK (round_status      IN ('Unscheduled','Scheduled','Started','Closed')),
    CONSTRAINT chk_sa_round3_init    CHECK (round3_init_status IN ('Pending','Complete')),
    CONSTRAINT chk_sa_reminders      CHECK (email_reminders    IN ('NoneSent','OneHourSent','FourHourSent','AllSent')),
    CONSTRAINT chk_sa_round_positive CHECK (round BETWEEN 1 AND 3)
);

COMMENT ON TABLE  auctions.scheduling_auctions IS 'Round-level auction schedule with notification state (auctionui$schedulingauction)';
COMMENT ON COLUMN auctions.scheduling_auctions.auction_id IS 'Parent auction — collapsed from auctionui$schedulingauction_auction junction';
COMMENT ON COLUMN auctions.scheduling_auctions.round IS 'Round number within the parent auction (1, 2, or 3)';
COMMENT ON COLUMN auctions.scheduling_auctions.round_status IS 'Enum_SchedulingAuctionStatus: Unscheduled | Scheduled | Started | Closed';
COMMENT ON COLUMN auctions.scheduling_auctions.round3_init_status IS 'Enum_ScheduleAuctionInitStatus: Pending | Complete';
COMMENT ON COLUMN auctions.scheduling_auctions.email_reminders IS 'ENUM_ReminderEmails: NoneSent | OneHourSent | FourHourSent | AllSent';
COMMENT ON COLUMN auctions.scheduling_auctions.snowflake_json IS 'JSON payload pushed to Snowflake on round close — audit trail';

CREATE INDEX idx_sa_auction          ON auctions.scheduling_auctions(auction_id);
CREATE INDEX idx_sa_status_start     ON auctions.scheduling_auctions(round_status, start_datetime);
CREATE INDEX idx_sa_auction_round    ON auctions.scheduling_auctions(auction_id, round);

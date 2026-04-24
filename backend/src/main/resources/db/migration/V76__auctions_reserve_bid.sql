-- =============================================================================
-- V74: auctions — reserve_bid (ExchangeBid floor prices)
-- Source: ecoatm_eb$reservebid (15,875 rows), ecoatm_eb$reservedbidaudit (4),
--         ecoatm_eb$reservebidsync (1).
-- Drops: ecoatm_eb$reservebidfile (0 rows; modern streams download),
--        both junctions (empty or collapsed to FK).
-- Design: docs/tasks/auction-eb-module-design.md
-- =============================================================================

CREATE TABLE auctions.reserve_bid (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    product_id               VARCHAR(100)    NOT NULL,
    grade                    VARCHAR(200)    NOT NULL,
    brand                    VARCHAR(200),
    model                    VARCHAR(200),
    bid                      NUMERIC(14, 4)  NOT NULL DEFAULT 0,
    last_update_datetime     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    last_awarded_min_price   NUMERIC(14, 4),
    last_awarded_week        VARCHAR(20),
    bid_valid_week_date      VARCHAR(20),
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id),
    CONSTRAINT uq_reserve_bid_product_grade UNIQUE (product_id, grade)
);

COMMENT ON TABLE  auctions.reserve_bid IS 'ExchangeBid reserve floor prices per (product_id, grade) (ecoatm_eb$reservebid)';
COMMENT ON COLUMN auctions.reserve_bid.product_id IS 'VARCHAR(100) matches auctions.bid_data.ecoid join key';
COMMENT ON COLUMN auctions.reserve_bid.bid IS 'Reserve floor — joined into target-price CTE GREATEST() by sub-project 4C';

CREATE INDEX idx_rb_product_grade  ON auctions.reserve_bid(product_id, grade);
CREATE INDEX idx_rb_last_update    ON auctions.reserve_bid(last_update_datetime DESC);

CREATE TABLE auctions.reserve_bid_audit (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    reserve_bid_id           BIGINT          NOT NULL
                                            REFERENCES auctions.reserve_bid(id)
                                            ON DELETE CASCADE,
    old_price                NUMERIC(14, 4)  NOT NULL,
    new_price                NUMERIC(14, 4)  NOT NULL,
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE auctions.reserve_bid_audit IS 'Price-change audit trail (ecoatm_eb$reservedbidaudit)';

CREATE INDEX idx_rba_reserve_bid ON auctions.reserve_bid_audit(reserve_bid_id);
CREATE INDEX idx_rba_created     ON auctions.reserve_bid_audit(created_date DESC);

CREATE TABLE auctions.reserve_bid_sync (
    id                       BIGSERIAL       PRIMARY KEY,
    legacy_id                BIGINT          UNIQUE,
    last_sync_datetime       TIMESTAMPTZ,
    created_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    changed_date             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    owner_id                 BIGINT          REFERENCES identity.users(id),
    changed_by_id            BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE auctions.reserve_bid_sync IS 'Singleton — last successful Snowflake→local pull watermark (ecoatm_eb$reservebidsync)';

INSERT INTO auctions.reserve_bid_sync (last_sync_datetime) VALUES (NULL);

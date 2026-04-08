-- Case Lot table — mirrors Mendix EcoATM_PWSMDM.CaseLot entity
CREATE TABLE pws.case_lot (
    id                  BIGSERIAL PRIMARY KEY,
    device_id           BIGINT NOT NULL REFERENCES mdm.device(id),
    case_lot_id         VARCHAR(200) NOT NULL UNIQUE,
    case_lot_size       INT NOT NULL,
    case_lot_price      NUMERIC(14,2) NOT NULL DEFAULT 0,
    case_lot_avl_qty    INT NOT NULL DEFAULT 0,
    case_lot_reserved_qty INT NOT NULL DEFAULT 0,
    case_lot_atp_qty    INT NOT NULL DEFAULT 0,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_by          VARCHAR(200),
    updated_by          VARCHAR(200),
    created_date        TIMESTAMP DEFAULT now(),
    updated_date        TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_case_lot_device_id ON pws.case_lot(device_id);
CREATE INDEX idx_case_lot_active    ON pws.case_lot(is_active);

-- Add case_lot_id FK column to offer_item so cart items can reference a specific case lot
ALTER TABLE pws.offer_item ADD COLUMN case_lot_id BIGINT REFERENCES pws.case_lot(id);

-- Seed case lots from existing SPB devices
-- Each SPB device gets one case lot with a realistic case size
INSERT INTO pws.case_lot (device_id, case_lot_id, case_lot_size, case_lot_price, case_lot_avl_qty, case_lot_reserved_qty, case_lot_atp_qty, is_active, created_by)
SELECT
    d.id,
    d.sku || '--' || ((d.id % 5 + 1) * 5),                          -- e.g. "PWS123--15"
    (d.id % 5 + 1) * 5,                                              -- size: 5, 10, 15, 20, or 25
    COALESCE(d.list_price, 0) * ((d.id % 5 + 1) * 5),               -- price = unit price × size
    GREATEST(1, COALESCE(d.available_qty, 0) / NULLIF((d.id % 5 + 1) * 5, 0)),  -- avl cases
    0,
    GREATEST(1, COALESCE(d.available_qty, 0) / NULLIF((d.id % 5 + 1) * 5, 0)),  -- atp = avl (no reservations yet)
    TRUE,
    'migration'
FROM mdm.device d
WHERE d.item_type = 'SPB'
  AND d.is_active = TRUE;

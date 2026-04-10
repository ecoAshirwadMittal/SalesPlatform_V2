-- Future Price Configuration (singleton row)
-- Mirrors Mendix MDMFuturePriceHelper entity
CREATE TABLE IF NOT EXISTS pws.future_price_config (
    id              BIGSERIAL PRIMARY KEY,
    future_price_date TIMESTAMP WITH TIME ZONE,
    created_date    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_date    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Seed the singleton row
INSERT INTO pws.future_price_config (future_price_date) VALUES (NULL);

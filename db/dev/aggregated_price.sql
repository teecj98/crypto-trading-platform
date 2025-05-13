CREATE TABLE aggregated_prices
(
    symbol     VARCHAR(10) PRIMARY KEY CHECK (symbol IN ('ETHUSDT', 'BTCUSDT')),
    bid        NUMERIC(19, 2) NOT NULL DEFAULT 0,
    ask        NUMERIC(19, 2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NULL
);
-- wallet
CREATE TABLE wallets
(
    uuid       UUID PRIMARY KEY,
    user_id    BIGINT                   NOT NULL,
    currency   VARCHAR(10)              NOT NULL CHECK (currency IN ('USDT', 'ETH', 'BTC')), --  currency unit of the wallet
    balance    NUMERIC(24, 6) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version    BIGINT         DEFAULT 0,

    CONSTRAINT unique_wallets__user_id_crypto UNIQUE (user_id, currency),                    -- as index as well
    CONSTRAINT fk_wallets_user_id_users_id FOREIGN KEY (user_id) REFERENCES users (id)
);

-- inheritance scenario: table per type ( transaction can have subtypes e.g. trade,deposit, dividend etc)
CREATE TABLE transactions
(
    uuid       UUID PRIMARY KEY,
    type       VARCHAR(10)              NOT NULL CHECK (type in ('TRADE', 'DEPOSIT')),      -- could have more types depending on the scope
    status     VARCHAR(10)              NOT NULL CHECK (status in ('FAILED', 'FULFILLED')), -- could have more statuses depending on logic
    user_id    BIGINT                   NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_transactions_user_id_users_id FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX idx_user_ide ON transactions (user_id);
CREATE INDEX idx_created_at ON transactions (created_at);

-- data about trading
CREATE TABLE trades
(
    uuid             UUID PRIMARY KEY,
    type             VARCHAR(10)    NOT NULL CHECK (type in ('BUY', 'SELL')),
    symbol           VARCHAR(10)    NOT NULL CHECK (symbol IN ('ETHUSDT', 'BTCUSDT')), -- buy or sell symbol
    amount           NUMERIC(19, 2) NOT NULL,                                          -- amount to buy or sell
    transaction_uuid UUID           NOT NULL,

    CONSTRAINT fk_trades_transaction_uuid_transactions_uuid FOREIGN KEY (transaction_uuid) REFERENCES transactions (uuid)
);

-- wallet in/out statements
CREATE TABLE wallet_statements
(
    uuid             UUID PRIMARY KEY,
    wallet_uuid      UUID                     NOT NULL,
    amount           NUMERIC(19, 2)           NOT NULL,
    type             VARCHAR(10)              NOT NULL CHECK (type in ('IN', 'OUT')),
    transaction_uuid UUID                     NOT NULL, -- each must be associated with a transaction
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_wallet_statements_wallet_uuid_wallets_uuid FOREIGN KEY (wallet_uuid) REFERENCES wallets (uuid),
    CONSTRAINT fk_wallet_statements_transaction_uuid_transactions_uuid FOREIGN KEY (transaction_uuid) REFERENCES transactions (uuid)
);

CREATE INDEX idx_wallet_statements_wallet_uuid_created_at ON wallet_statements (wallet_uuid, created_at);






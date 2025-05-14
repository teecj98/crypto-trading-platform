SET
@walletID = UUID();
SET
@transactionID = UUID();

-- user 1001 initially has 50 000 USDT deposited

INSERT INTO wallets(uuid, user_id, currency, balance, created_at, updated_at)
VALUES (@walletID, 1001, 'USDT', 50000, current_timestamp(), current_timestamp()),
       (UUID(), 1001, 'ETH', 0, current_timestamp(), current_timestamp()),
       (UUID(), 1001, 'BTC', 0, current_timestamp(), current_timestamp());

INSERT INTO transactions (uuid, type, status, user_id, created_at)
VALUES (@transactionID, 'DEPOSIT', 'FULFILLED', 1001, current_timestamp());

INSERT INTO wallet_statements(uuid, wallet_uuid, amount, type, transaction_uuid, created_at)
VALUES (UUID(), @walletID, 50000, 'IN', @transactionID, current_timestamp());
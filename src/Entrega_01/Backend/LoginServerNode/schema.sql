-- ====================== USUÁRIOS ======================
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    coupon_count INTEGER DEFAULT 0,        -- Contador de cupons
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- ====================== ADMIN ======================
CREATE TABLE IF NOT EXISTS admin (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Inserir admin padrão
INSERT INTO admin (username, email, password) VALUES (
    'admin',
    'admin@email.com',
    '$2b$10$7a9tR/6LzV1Wz1qQeIXvFeJqPZp8eDpEMqZCqv8P2qTk4x1k3Yd3a' -- senha: 123456
);

-- ====================== PRODUTOS ======================
CREATE TABLE IF NOT EXISTS product (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    description TEXT,
    image_url TEXT
);

-- ====================== PEDIDOS ======================
CREATE TABLE IF NOT EXISTS orders (
    id TEXT PRIMARY KEY,                   -- Pode ser UUID
    user_id INTEGER NOT NULL,
    total REAL NOT NULL,
    status TEXT DEFAULT 'pending',         -- pending ou completed
    paid INTEGER DEFAULT 0,                -- 0 = não pago, 1 = pago
    pickup_code TEXT,                      -- Código para retirada
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- ====================== ITENS DO PEDIDO ======================
CREATE TABLE IF NOT EXISTS order_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id TEXT NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    subtotal REAL NOT NULL,                -- price * quantity
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- ====================== RECOMPENSAS ======================
CREATE TABLE IF NOT EXISTS rewards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL,                    -- e.g., 'voucher'
    code TEXT NOT NULL,                    -- Código do voucher
    redeemed INTEGER DEFAULT 0,            -- 0 = não usado, 1 = usado
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

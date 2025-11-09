// server.js (ou index.js)
const express = require('express');
const Database = require('better-sqlite3');
const bcrypt = require('bcrypt');
const cors = require('cors');
const { v4: uuidv4 } = require('uuid');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const jwt = require('jsonwebtoken');

const app = express();
const port = process.env.PORT || 3000;
const JWT_SECRET = "sua_chave_secreta_aqui"; // troque para algo seguro

// Conecta ao banco SQLite
const db = new Database('./user.db');
db.pragma('foreign_keys = ON');

// Middleware
app.use(express.json());
app.use(cors());
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// ====================== UPLOAD DE IMAGEM ======================
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, './uploads'),
  filename: (req, file, cb) => cb(null, `${Date.now()}-${file.originalname}`)
});
const upload = multer({ storage });

// ====================== CRIAÇÃO DE TABELAS ======================
db.prepare(`
  CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    coupon_count INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  )
`).run();

db.prepare(`
  CREATE TABLE IF NOT EXISTS admin (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  )
`).run();

db.prepare(`
  CREATE TABLE IF NOT EXISTS product (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    description TEXT,
    image_url TEXT,
    deleted INTEGER DEFAULT 0
  )
`).run();

// 🔧 Migração: garante a coluna "deleted" se a tabela product já existia sem ela
(function ensureProductDeletedColumn() {
  const cols = db.prepare(`PRAGMA table_info(product)`).all();
  const hasDeleted = cols.some(c => c.name === 'deleted');
  if (!hasDeleted) {
    db.prepare(`ALTER TABLE product ADD COLUMN deleted INTEGER DEFAULT 0`).run();
  }
})();

db.prepare(`
  CREATE TABLE IF NOT EXISTS orders (
    id TEXT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total REAL NOT NULL,
    status TEXT DEFAULT 'pending',
    paid INTEGER DEFAULT 0,
    pickup_code TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
  )
`).run();

db.prepare(`
  CREATE TABLE IF NOT EXISTS order_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id TEXT NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    subtotal REAL NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
  )
`).run();

db.prepare(`
  CREATE TABLE IF NOT EXISTS rewards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    code TEXT NOT NULL,
    redeemed INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
  )
`).run();

// ====================== ADMIN PADRÃO ======================
(async () => {
  const adminEmail = 'admin@admin.com';
  const adminPassword = 'admin123';
  const exists = db.prepare('SELECT * FROM admin WHERE email = ?').get(adminEmail);
  if (!exists) {
    const hashedPassword = await bcrypt.hash(adminPassword, 10);
    db.prepare('INSERT INTO admin (username, email, password) VALUES (?, ?, ?)').run('Admin', adminEmail, hashedPassword);
    console.log('✅ Admin padrão criado: admin@admin.com / admin123');
  }
})();

// ====================== FUNÇÃO DE AUTENTICAÇÃO JWT ======================
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).send("Access denied: No token provided");

  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) return res.status(403).send("Invalid token");
    req.user = user; // { id, is_admin }
    next();
  });
}

// ====================== HELPERS ======================
function unlinkIfLocal(image_url) {
  try {
    if (!image_url) return;
    const rel = image_url.startsWith('/') ? image_url.slice(1) : image_url;
    const full = path.join(__dirname, rel);
    if (fs.existsSync(full)) fs.unlinkSync(full);
  } catch (e) {
    console.warn('⚠️ Falha ao remover imagem:', e.message);
  }
}

// ====================== ROTAS USUÁRIO ======================

// Cadastro de usuário
app.post('/user', async (req, res) => {
  const { username, email, password } = req.body;
  try {
    if (!username || !email || !password) return res.status(400).send("All fields are required");
    if (password.length < 6) return res.status(400).send("Password must be at least 6 characters");

    const hashedPassword = await bcrypt.hash(password, 10);
    db.prepare('INSERT INTO user (username, email, password) VALUES (?, ?, ?)').run(username, email, hashedPassword);
    res.status(201).send("User created successfully");
  } catch (err) {
    console.error(err);
    if (err.code === 'SQLITE_CONSTRAINT') return res.status(400).send("Email already exists");
    res.status(500).send("Internal server error");
  }
});

// Login usuário/admin
app.post('/login', async (req, res) => {
  const { email, password } = req.body;
  try {
    let user = db.prepare('SELECT * FROM user WHERE email = ?').get(email);
    let isAdmin = false;

    if (!user) {
      user = db.prepare('SELECT * FROM admin WHERE email = ?').get(email);
      isAdmin = !!user;
    }

    if (!user) return res.status(401).send("Invalid credentials");

    const valid = await bcrypt.compare(password, user.password);
    if (!valid) return res.status(401).send("Invalid credentials");

    const token = jwt.sign({ id: user.id, is_admin: isAdmin }, JWT_SECRET, { expiresIn: '12h' });

    res.status(200).json({
      message: "Login successful",
      token,
      user_id: user.id,
      is_admin: isAdmin
    });
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// ====================== ROTAS PRODUTOS ======================

// Listar produtos (somente ativos)
app.get('/products', (req, res) => {
  try {
    const products = db.prepare('SELECT * FROM product WHERE deleted = 0 ORDER BY id DESC').all();
    res.json(products);
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// (Opcional) Lista completa para admin (inclui arquivados)
app.get('/admin/products', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");
  try {
    const products = db.prepare('SELECT * FROM product ORDER BY id DESC').all();
    res.json(products);
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// ====================== ROTAS ADMIN PROTEGIDAS ======================

// Criar produto com imagem
app.post('/admin/product', authenticateToken, upload.single('image'), (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const { name, price, description } = req.body;
  const image_url = req.file ? `/uploads/${req.file.filename}` : null;

  try {
    db.prepare(`
      INSERT INTO product (name, price, description, image_url, deleted)
      VALUES (?, ?, ?, ?, 0)
    `).run(name, price, description, image_url);

    res.status(201).send({ message: "Product created successfully", image_url });
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// Atualizar produto
app.put('/admin/product/:id', authenticateToken, upload.single('image'), (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const { id } = req.params;
  const { name, price, description } = req.body;
  const image_url = req.file ? `/uploads/${req.file.filename}` : null;

  try {
    const product = db.prepare('SELECT * FROM product WHERE id = ?').get(id);
    if (!product) return res.status(404).send("Product not found");

    db.prepare(`
      UPDATE product
      SET name = ?, price = ?, description = ?, image_url = COALESCE(?, image_url)
      WHERE id = ?
    `).run(
      name || product.name,
      price || product.price,
      description || product.description,
      image_url,
      id
    );

    res.send("✅ Product updated successfully");
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// Restaurar produto arquivado (opcional)
app.put('/admin/product/:id/restore', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");
  const { id } = req.params;
  try {
    const p = db.prepare('SELECT * FROM product WHERE id = ?').get(id);
    if (!p) return res.status(404).send("Product not found");
    db.prepare('UPDATE product SET deleted = 0 WHERE id = ?').run(id);
    res.send("✅ Product restored");
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// Deletar (hard) ou arquivar (soft) produto
app.delete('/admin/product/:id', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const { id } = req.params;
  try {
    const product = db.prepare('SELECT * FROM product WHERE id = ?').get(id);
    if (!product) return res.status(404).send("Product not found");

    const usage = db.prepare('SELECT COUNT(*) AS cnt FROM order_items WHERE product_id = ?').get(id);

    if (usage.cnt > 0) {
      // ✅ Soft delete: some da vitrine mas preserva histórico dos pedidos
      db.prepare('UPDATE product SET deleted = 1 WHERE id = ?').run(id);
      return res.status(200).json({
        archived: true,
        message: "Product archived (it has past orders)."
      });
    }

    // Hard delete (nunca usado): remove imagem e apaga o registro
    unlinkIfLocal(product.image_url);
    db.prepare('DELETE FROM product WHERE id = ?').run(id);
    res.send("🗑️ Product deleted successfully");
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// ====================== CHECKOUT / PEDIDOS ======================
app.post('/checkout', authenticateToken, (req, res) => {
  const { user_id, items } = req.body;
  if (req.user.id !== user_id) return res.status(403).send("Access denied");

  try {
    if (!Array.isArray(items) || items.length === 0)
      return res.status(400).send("Invalid request: items are required");

    let total = 0;
    for (const item of items) {
      const product = db.prepare('SELECT price FROM product WHERE id = ?').get(item.product_id);
      if (!product) return res.status(400).send(`Product id ${item.product_id} not found`);
      total += product.price * item.quantity;
    }

    const orderId = uuidv4();
    const pickupCode = Math.floor(1000 + Math.random() * 9000).toString();

    // PIX simulado = paid = 1
    db.prepare('INSERT INTO orders (id, user_id, total, pickup_code, paid) VALUES (?, ?, ?, ?, 1)')
      .run(orderId, user_id, total, pickupCode);

    const itemStmt = db.prepare('INSERT INTO order_items (order_id, product_id, quantity, subtotal) VALUES (?, ?, ?, ?)');
    const insertItems = db.transaction((items) => {
      for (const item of items) {
        const price = db.prepare('SELECT price FROM product WHERE id = ?').get(item.product_id).price;
        const subtotal = price * item.quantity;
        itemStmt.run(orderId, item.product_id, item.quantity, subtotal);
      }
    });
    insertItems(items);

    // Atualiza cupons/rewards
    const user = db.prepare('SELECT coupon_count FROM user WHERE id = ?').get(user_id);
    let newCoupons = (user?.coupon_count ?? 0) + 1;
    db.prepare('UPDATE user SET coupon_count = ? WHERE id = ?').run(newCoupons, user_id);

    if (newCoupons >= 10) {
      const rewardCode = `VOUCHER-${uuidv4().split('-')[0].toUpperCase()}`;
      db.prepare('INSERT INTO rewards (user_id, type, code) VALUES (?, ?, ?)').run(user_id, 'voucher', rewardCode);
      db.prepare('UPDATE user SET coupon_count = 0 WHERE id = ?').run(user_id);
    }

    res.status(201).send({ order_id: orderId, total, pickup_code: pickupCode });
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// Histórico de pedidos (usuário)
app.get('/orders', authenticateToken, (req, res) => {
  try {
    const orders = db.prepare('SELECT * FROM orders WHERE user_id = ?').all(req.user.id);
    for (const order of orders) {
      order.items = db.prepare(`
        SELECT product.name, product.price, order_items.quantity
        FROM order_items
        JOIN product ON order_items.product_id = product.id
        WHERE order_items.order_id = ?
      `).all(order.id);

      order.rewards = db.prepare('SELECT * FROM rewards WHERE user_id = ?').all(req.user.id);
    }
    res.json(orders);
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// ====================== ADMIN - PEDIDOS ======================
app.get('/admin/orders', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  try {
    const orders = db.prepare("SELECT * FROM orders WHERE status = 'pending'").all();
    for (const order of orders) {
      order.items = db.prepare(`
        SELECT product.name, product.price, order_items.quantity
        FROM order_items
        JOIN product ON order_items.product_id = product.id
        WHERE order_items.order_id = ?
      `).all(order.id);
    }
    res.json(orders);
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

app.put('/admin/orders/:order_id', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const { order_id } = req.params;
  const { status } = req.body;

  try {
    if (!status) return res.status(400).send("Status is required");
    db.prepare('UPDATE orders SET status = ? WHERE id = ?').run(status, order_id);
    res.send("Order updated successfully");
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// Relatório de vendas
app.get('/admin/report', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  try {
    const report = db.prepare(`
      SELECT product.name, 
             SUM(order_items.quantity) AS total_sold, 
             SUM(order_items.quantity * product.price) AS revenue
      FROM order_items
      JOIN product ON order_items.product_id = product.id
      JOIN orders ON order_items.order_id = orders.id
      WHERE orders.status = 'completed'
      GROUP BY product.id
    `).all();
    res.json(report);
  } catch (err) {
    console.error(err);
    res.status(500).send("Internal server error");
  }
});

// ====================== PERFIL DO USUÁRIO LOGADO ======================
app.get('/me', authenticateToken, (req, res) => {
  try {
    const u = db.prepare(`
      SELECT id, username, email, coupon_count, created_at
      FROM user
      WHERE id = ?
    `).get(req.user.id);

    if (!u) return res.status(404).send("User not found");
    res.json(u);
  } catch (e) {
    console.error(e);
    res.status(500).send("Internal server error");
  }
});

app.put('/me', authenticateToken, async (req, res) => {
  try {
    const { username, email, password } = req.body;

    const current = db.prepare('SELECT * FROM user WHERE id = ?').get(req.user.id);
    if (!current) return res.status(404).send("User not found");

    let newUsername = (username ?? current.username)?.trim();
    let newEmail    = (email ?? current.email)?.trim();
    let newPwdHash  = current.password;

    if (password && String(password).trim().length > 0) {
      if (password.length < 6) return res.status(400).send("Password must be at least 6 characters");
      newPwdHash = await bcrypt.hash(password, 10);
    }

    if (newEmail !== current.email) {
      const exists = db.prepare('SELECT id FROM user WHERE email = ?').get(newEmail);
      if (exists && exists.id !== current.id) {
        return res.status(400).send("Email already in use");
      }
    }

    db.prepare(`
      UPDATE user
      SET username = ?, email = ?, password = ?
      WHERE id = ?
    `).run(newUsername, newEmail, newPwdHash, req.user.id);

    const updated = db.prepare(`
      SELECT id, username, email, coupon_count, created_at
      FROM user
      WHERE id = ?
    `).get(req.user.id);

    res.json(updated);
  } catch (e) {
    console.error(e);
    res.status(500).send("Internal server error");
  }
});

// ====================== TESTE ======================
app.get('/ping', (req, res) => res.send('pong'));

// ====================== INICIALIZAÇÃO ======================
app.listen(port, () => console.log(`✅ Server started on port ${port}`));

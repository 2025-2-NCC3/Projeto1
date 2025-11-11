// server.js
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
const JWT_SECRET = "sua_chave_secreta_aqui";

// ====================== CONFIGURAÇÕES ======================
const db = new Database('./user.db');
db.pragma('foreign_keys = ON');
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
    category TEXT,
    quantity INTEGER DEFAULT 0,
    description TEXT,
    image_url TEXT,
    deleted INTEGER DEFAULT 0
  )
`).run();

(function ensureProductColumns() {
  const cols = db.prepare(`PRAGMA table_info(product)`).all();
  if (!cols.some(c => c.name === 'category'))
    db.prepare(`ALTER TABLE product ADD COLUMN category TEXT`).run();
  if (!cols.some(c => c.name === 'quantity'))
    db.prepare(`ALTER TABLE product ADD COLUMN quantity INTEGER DEFAULT 0`).run();
  if (!cols.some(c => c.name === 'deleted'))
    db.prepare(`ALTER TABLE product ADD COLUMN deleted INTEGER DEFAULT 0`).run();
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

// ====================== ADMIN PADRÃO ======================
(async () => {
  const adminEmail = 'admin@admin.com';
  const adminPassword = 'admin123';
  const exists = db.prepare('SELECT * FROM admin WHERE email = ?').get(adminEmail);
  if (!exists) {
    const hashed = await bcrypt.hash(adminPassword, 10);
    db.prepare('INSERT INTO admin (username, email, password) VALUES (?, ?, ?)').run('Admin', adminEmail, hashed);
    console.log('✅ Admin padrão criado: admin@admin.com / admin123');
  }
})();

// ====================== AUTH MIDDLEWARE ======================
function authenticateToken(req, res, next) {
  const token = (req.headers['authorization'] || '').split(' ')[1];
  if (!token) return res.status(401).send("Access denied: No token provided");

  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) return res.status(403).send("Invalid token");
    req.user = user;
    next();
  });
}

// ====================== ROTAS ======================

// 👤 Cadastro e login
app.post('/user', async (req, res) => {
  const { username, email, password } = req.body;
  if (!username || !email || !password)
    return res.status(400).send("All fields are required");
  if (password.length < 6)
    return res.status(400).send("Password must be at least 6 characters");

  try {
    const hashed = await bcrypt.hash(password, 10);
    db.prepare('INSERT INTO user (username, email, password) VALUES (?, ?, ?)').run(username, email, hashed);
    res.status(201).send("User created successfully");
  } catch (err) {
    if (err.code === 'SQLITE_CONSTRAINT') return res.status(400).send("Email already exists");
    res.status(500).send("Internal server error");
  }
});

app.post('/login', async (req, res) => {
  const { email, password } = req.body;
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
  res.json({ message: "Login successful", token, user_id: user.id, is_admin: isAdmin });
});

// 📦 Listar produtos
app.get('/products', (req, res) => {
  try {
    const rows = db.prepare(`
      SELECT id, name, price, description, category, quantity, image_url
      FROM product
      WHERE deleted = 0
      ORDER BY id DESC
    `).all();
    res.json(rows);
  } catch (err) {
    console.error("Erro /products:", err);
    res.status(500).send("Internal server error");
  }
});

// 📦 Listar produtos (admin)
app.get('/admin/products', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");
  try {
    const products = db.prepare(`
      SELECT id, name, price, description, category, quantity, image_url, deleted
      FROM product
      ORDER BY id DESC
    `).all();
    res.json(products);
  } catch (err) {
    res.status(500).send("Internal server error");
  }
});

// ➕ Criar produto
app.post('/admin/product', authenticateToken, upload.single('image'), (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const { name, price, description, category, quantity } = req.body;
  const baseUrl = `http://10.0.2.2:3000`;
  const image_url = req.file ? `${baseUrl}/uploads/${req.file.filename}` : null;

  db.prepare(`
    INSERT INTO product (name, price, description, category, quantity, image_url, deleted)
    VALUES (?, ?, ?, ?, ?, ?, 0)
  `).run(name, price, description, category, quantity ?? 0, image_url);

  res.status(201).json({ message: "Product created successfully", image_url });
});

// ✏️ Atualizar produto
app.put('/admin/product/:id', authenticateToken, upload.single('image'), (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const id = req.params.id;
  const { name, price, description, category, quantity } = req.body;
  const baseUrl = `http://10.0.2.2:3000`;

  const existing = db.prepare('SELECT * FROM product WHERE id = ?').get(id);
  if (!existing) return res.status(404).send("Product not found");

  let image_url = existing.image_url;
  if (req.file) {
    if (image_url && image_url.startsWith(baseUrl)) {
      const rel = image_url.replace(baseUrl + '/', '');
      const abs = path.join(__dirname, rel);
      if (fs.existsSync(abs)) fs.unlinkSync(abs);
    }
    image_url = `${baseUrl}/uploads/${req.file.filename}`;
  }

  db.prepare(`
    UPDATE product 
    SET name=?, price=?, description=?, category=?, quantity=?, image_url=?
    WHERE id=?
  `).run(name, price, description, category, quantity, image_url, id);

  res.json({ message: "✅ Produto atualizado com sucesso", image_url });
});

// ❌ Excluir produto
app.delete('/admin/product/:id', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const id = req.params.id;
  const product = db.prepare('SELECT * FROM product WHERE id = ?').get(id);
  if (!product) return res.status(404).send("Product not found");

  const used = db.prepare('SELECT COUNT(*) AS qtd FROM order_items WHERE product_id = ?').get(id);
  if (used.qtd > 0)
    return res.status(409).send("Produto já utilizado em pedidos.");

  if (product.image_url) {
    const rel = product.image_url.replace('http://10.0.2.2:3000/', '');
    const abs = path.join(__dirname, rel);
    if (fs.existsSync(abs)) fs.unlinkSync(abs);
  }

  db.prepare('DELETE FROM product WHERE id = ?').run(id);
  res.json({ message: "✅ Produto excluído com sucesso" });
});

// ✅ Checkout
app.post('/checkout', authenticateToken, (req, res) => {
  const { user_id, items } = req.body;
  if (req.user.id !== user_id) return res.status(403).send("Access denied");

  if (!Array.isArray(items) || items.length === 0)
    return res.status(400).send("Invalid request: items are required");

  let total = 0;
  for (const item of items) {
    const p = db.prepare('SELECT name, price, quantity FROM product WHERE id = ?').get(item.product_id);
    if (!p) return res.status(400).send(`Produto ID ${item.product_id} não encontrado.`);
    if (p.quantity < item.quantity)
      return res.status(400).send(`❌ O produto "${p.name}" possui apenas ${p.quantity} unidade(s).`);
    total += p.price * item.quantity;
  }

  const orderId = uuidv4();
  const pickupCode = Math.floor(1000 + Math.random() * 9000).toString();
  db.prepare('INSERT INTO orders (id, user_id, total, pickup_code, paid) VALUES (?, ?, ?, ?, 1)')
    .run(orderId, user_id, total, pickupCode);

  const insertItem = db.prepare('INSERT INTO order_items (order_id, product_id, quantity, subtotal) VALUES (?, ?, ?, ?)');
  const updateStock = db.prepare('UPDATE product SET quantity = quantity - ? WHERE id = ?');

  const tx = db.transaction(items => {
    for (const i of items) {
      const p = db.prepare('SELECT price FROM product WHERE id = ?').get(i.product_id);
      insertItem.run(orderId, i.product_id, i.quantity, p.price * i.quantity);
      updateStock.run(i.quantity, i.product_id);
    }
  });
  tx(items);

  res.status(201).json({ order_id: orderId, total, pickup_code: pickupCode });
});

// 🧾 Pedidos do usuário
app.get('/myorders', authenticateToken, (req, res) => {
  const userId = req.user.id;
  const orders = db.prepare(`
    SELECT id, total, status, pickup_code, created_at, paid, user_id
    FROM orders WHERE user_id = ?
    ORDER BY created_at DESC
  `).all(userId);

  const itemStmt = db.prepare(`
    SELECT p.name, p.price, oi.quantity
    FROM order_items oi JOIN product p ON p.id = oi.product_id
    WHERE oi.order_id = ?
  `);

  for (const order of orders) {
    order.items = itemStmt.all(order.id);
  }

  res.json(orders);
});

// ====================== ROTAS ADMIN DE PEDIDOS ======================

// 📋 Listar todos os pedidos pendentes (admin)
app.get('/admin/orders', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  try {
    const orders = db.prepare(`
      SELECT id, user_id, total, status, paid, pickup_code, created_at
      FROM orders
      WHERE status = 'pending'
      ORDER BY created_at DESC
    `).all();

    const itemStmt = db.prepare(`
      SELECT oi.product_id, p.name, p.price, oi.quantity
      FROM order_items oi
      JOIN product p ON p.id = oi.product_id
      WHERE oi.order_id = ?
    `);

    for (const order of orders) {
      order.items = itemStmt.all(order.id);
    }

    res.json(orders);
  } catch (err) {
    console.error("Erro /admin/orders:", err);
    res.status(500).send("Internal server error");
  }
});

// 🟢 Atualizar status do pedido (por ex: marcar como concluído)
app.put('/admin/orders/:order_id', authenticateToken, (req, res) => {
  if (!req.user.is_admin) return res.status(403).send("Access denied");

  const { order_id } = req.params;
  const { status } = req.body;

  const order = db.prepare('SELECT * FROM orders WHERE id = ?').get(order_id);
  if (!order) return res.status(404).send("Order not found");

  const newStatus = status || 'completed';
  db.prepare('UPDATE orders SET status = ? WHERE id = ?').run(newStatus, order_id);

  res.json({ message: `✅ Pedido ${order_id} atualizado para "${newStatus}"` });
});

// ====================== PERFIL DO USUÁRIO ======================

// 📋 Obter dados do perfil logado
app.get('/me', authenticateToken, (req, res) => {
  try {
    const u = db.prepare(`
      SELECT id, username, email, coupon_count, created_at
      FROM user
      WHERE id = ?
    `).get(req.user.id);

    if (!u) return res.status(404).send("User not found");
    res.json(u);
  } catch (err) {
    console.error("Erro /me:", err);
    res.status(500).send("Internal server error");
  }
});

// ✏️ Atualizar perfil do usuário logado
app.put('/me', authenticateToken, async (req, res) => {
  try {
    const { username, email, password } = req.body;

    if (!username || !email) {
      return res.status(400).send("Name and email are required");
    }

    const existing = db.prepare('SELECT * FROM user WHERE id = ?').get(req.user.id);
    if (!existing) return res.status(404).send("User not found");

    let hashedPassword = existing.password;
    if (password && password.length >= 6) {
      hashedPassword = await bcrypt.hash(password, 10);
    }

    db.prepare(`
      UPDATE user
      SET username = ?, email = ?, password = ?
      WHERE id = ?
    `).run(username, email, hashedPassword, req.user.id);

    const updated = db.prepare(`
      SELECT id, username, email, coupon_count, created_at
      FROM user WHERE id = ?
    `).get(req.user.id);

    res.json(updated);
  } catch (err) {
    console.error("Erro PUT /me:", err);
    res.status(500).send("Internal server error");
  }
});


// ====================== INICIALIZA ======================
app.listen(port, () => console.log(`✅ Server started on port ${port}`));

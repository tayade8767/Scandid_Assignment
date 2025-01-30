const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Database Connection Configuration
const dbConfig = {
    host: 'localhost',
    user: 'root',
    password: 'akash@123',
    database: 'Scandid_Assignment'  
};

const pool = mysql.createPool(dbConfig);

// ✅ Categories Endpoint
app.get('/api/categories', async (req, res) => {
    try {
        const [categories] = await pool.query('SELECT * FROM categories');  // ✅ Fix table name
        res.json(categories);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// ✅ Products Endpoint
app.get('/api/products', async (req, res) => {
    try {
        const { categoryId } = req.query;
        let query = 'SELECT * FROM products';  // ✅ Fix table name
        let params = [];

        if (categoryId) {
            query += ' WHERE category_id = ?';
            params.push(categoryId);
        }

        const [products] = await pool.query(query, params);
        res.json(products);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// ✅ Transactions Endpoint (Fixing JOIN Issues)
app.get('/api/transactions', async (req, res) => {
    try {
        const { categoryId, startDate, endDate } = req.query;
        
        let query = `
            SELECT t.*, p.title AS product_name, c.category_name AS category_name 
            FROM transactions t
            JOIN products p ON t.product_id = p.product_id
            JOIN categories c ON p.category_id = c.category_id
            WHERE 1=1
        `;
        
        const params = [];

        if (categoryId) {
            query += ' AND c.category_id = ?';
            params.push(categoryId);
        }

        if (startDate) {
            query += ' AND t.order_date >= ?';
            params.push(startDate);
        }

        if (endDate) {
            query += ' AND t.order_date <= ?';
            params.push(endDate);
        }

        const [transactions] = await pool.query(query, params);
        res.json(transactions);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    productid VARCHAR(50) UNIQUE NOT NULL,
    title TEXT NOT NULL,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    txid VARCHAR(50) UNIQUE NOT NULL,
    store VARCHAR(50),
    product_id INT,
    sales DECIMAL(10,2),
    price DECIMAL(10,2),
    commission DECIMAL(10,2),
    order_date DATETIME,
    pid VARCHAR(50),
    affid VARCHAR(50),
    status VARCHAR(50),
    added_at DATETIME,
    last_updated DATETIME,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

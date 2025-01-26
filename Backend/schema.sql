-- Create Database named as 
CREATE DATABASE Scandid_Assignment;
USE Scandid_Assignment;


-- below is only for the category 


CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);



-- below is only for the product    schema

CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES category(id)
);


-- below is only  for the Transactions table

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    quantity INT,
    total_amount DECIMAL(10,2),
    transaction_date DATETIME,
    FOREIGN KEY (product_id) REFERENCES product(id)
);




-- Insert sample categories
INSERT INTO category (name) VALUES ('Electronics'), ('Clothing'), ('Groceries');

-- Insert sample products
INSERT INTO product (name, category_id) VALUES 
('Laptop', 1),
('Shirt', 2),
('Rice', 3);

-- Insert sample transactions
INSERT INTO transactions (product_id, quantity, total_amount, transaction_date) VALUES
(1, 2, 1500.00, '2025-01-25 10:00:00'),
(2, 5, 250.00, '2025-01-26 11:00:00'),
(3, 10, 200.00, '2025-01-26 12:00:00');

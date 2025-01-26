/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from 'react';
import axios from 'axios';

function TransactionDashboard() {
    const [categories, setCategories] = useState([]);
    const [products, setProducts] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [filters, setFilters] = useState({
        categoryId: '',
        startDate: '',
        endDate: ''
    });

    useEffect(() => {
        // Fetch initial data
        fetchCategories();
        fetchTransactions();
    }, []);

    useEffect(() => {
        // Fetch products when category changes
        if (filters.categoryId) {
            fetchProducts(filters.categoryId);
        } else {
            fetchProducts();
        }
    }, [filters.categoryId]);

    const fetchCategories = async () => {
        try {
            const response = await axios.get('http://localhost:5000/api/categories');
            setCategories(response.data);
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    };

    const fetchProducts = async (categoryId = null) => {
        try {
            const url = categoryId 
                ? `http://localhost:5000/api/products?categoryId=${categoryId}`
                : 'http://localhost:5000/api/products';
            
            const response = await axios.get(url);
            setProducts(response.data);
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    };

    const fetchTransactions = async () => {
        try {
            const response = await axios.get('http://localhost:5000/api/transactions', {
                params: {
                    categoryId: filters.categoryId,
                    startDate: filters.startDate,
                    endDate: filters.endDate
                }
            });
            setTransactions(response.data);
        } catch (error) {
            console.error('Error fetching transactions:', error);
        }
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({
            ...prev,
            [name]: value
        }));
    };

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Transaction Management Dashboard</h1>

            {/* Filters */}
            <div className="mb-4 flex space-x-4">
                <select 
                    name="categoryId"
                    value={filters.categoryId}
                    onChange={handleFilterChange}
                    className="border p-2 rounded"
                >
                    <option value="">All Categories</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.id}>
                            {category.name}
                        </option>
                    ))}
                </select>

                <input 
                    type="date" 
                    name="startDate"
                    value={filters.startDate}
                    onChange={handleFilterChange}
                    className="border p-2 rounded"
                />

                <input 
                    type="date" 
                    name="endDate"
                    value={filters.endDate}
                    onChange={handleFilterChange}
                    className="border p-2 rounded"
                />

                <button 
                    onClick={fetchTransactions}
                    className="bg-blue-500 text-white p-2 rounded"
                >
                    Apply Filters
                </button>
            </div>

            {/* Products Table */}
            <div className="mb-6">
                <h2 className="text-xl font-bold mb-2">Products</h2>
                <table className="w-full border">
                    <thead>
                        <tr className="bg-gray-200">
                            <th className="p-2 border">ID</th>
                            <th className="p-2 border">Name</th>
                            <th className="p-2 border">Category</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map(product => (
                            <tr key={product.id}>
                                <td className="p-2 border">{product.id}</td>
                                <td className="p-2 border">{product.name}</td>
                                <td className="p-2 border">{product.category_id}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Transactions Table */}
            <div>
                <h2 className="text-xl font-bold mb-2">Transactions</h2>
                <table className="w-full border">
                    <thead>
                        <tr className="bg-gray-200">
                            <th className="p-2 border">ID</th>
                            <th className="p-2 border">Product</th>
                            <th className="p-2 border">Quantity</th>
                            <th className="p-2 border">Total Amount</th>
                            <th className="p-2 border">Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        {transactions.map(transaction => (
                            <tr key={transaction.id}>
                                <td className="p-2 border">{transaction.id}</td>
                                <td className="p-2 border">{transaction.product_name}</td>
                                <td className="p-2 border">{transaction.quantity}</td>
                                <td className="p-2 border">{transaction.total_amount}</td>
                                <td className="p-2 border">{transaction.transaction_date}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default TransactionDashboard;

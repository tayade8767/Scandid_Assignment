import java.sql.*;
import java.util.*;

public class DataImporter {

    //   database credentials are fake just use for Dummy purpose otherwise i used to shore it in the another file that file should be private
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Scandid_Assignment";
    private static final String USER = "root";
    private static final String PASSWORD = "akash@123";
    private static final String SCHEMA_FILE_PATH = "schema.sql"; // Specify the correct path to schema.sql

    public static void main(String[] args) {
        List<Map<String, String>> flatData = Arrays.asList(
            Map.of(
                "productName", "Laptop", 
                "categoryName", "Electronics", 
                "quantity", "5", 
                "totalAmount", "50000.00", 
                "transactionDate", "2024-01-15 10:30:00"
            ),
            Map.of(
                "productName", "Smartphone", 
                "categoryName", "Electronics", 
                "quantity", "10", 
                "totalAmount", "30000.00", 
                "transactionDate", "2024-01-16 14:45:00"
            )
        );

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // Disable auto-commit for transaction control
            connection.setAutoCommit(false);

            for (Map<String, String> record : flatData) {
                // Process each record
                processRecord(connection, record);
            }

            // Commit transactions
            connection.commit();
            System.out.println("Data imported successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void processRecord(Connection connection, Map<String, String> record) throws SQLException {
        String categoryName = record.get("categoryName");
        String productName = record.get("productName");
        int quantity = Integer.parseInt(record.get("quantity"));
        double totalAmount = Double.parseDouble(record.get("totalAmount"));
        String transactionDate = record.get("transactionDate");

        // Insert or get category
        int categoryId = insertCategory(connection, categoryName);

        // Insert or get product
        int productId = insertProduct(connection, productName, categoryId);

        // Insert transaction
        insertTransaction(connection, productId, quantity, totalAmount, transactionDate);
    }

    private static int insertCategory(Connection connection, String categoryName) throws SQLException {
        String selectQuery = "SELECT id FROM category WHERE name = ?";
        String insertQuery = "INSERT INTO category (name) VALUES (?)";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Check if category exists
            selectStmt.setString(1, categoryName);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

            // Insert new category
            insertStmt.setString(1, categoryName);
            insertStmt.executeUpdate();

            rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Failed to insert category");
    }

    private static int insertProduct(Connection connection, String productName, int categoryId) throws SQLException {
        String selectQuery = "SELECT id FROM product WHERE name = ? AND category_id = ?";
        String insertQuery = "INSERT INTO product (name, category_id) VALUES (?, ?)";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Check if product exists
            selectStmt.setString(1, productName);
            selectStmt.setInt(2, categoryId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

            // Insert new product
            insertStmt.setString(1, productName);
            insertStmt.setInt(2, categoryId);
            insertStmt.executeUpdate();

            rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Failed to insert product");
    }

    private static void insertTransaction(Connection connection, int productId, int quantity, double totalAmount, String transactionDate) throws SQLException {
        String insertQuery = "INSERT INTO transactions (product_id, quantity, total_amount, transaction_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, productId);
            insertStmt.setInt(2, quantity);
            insertStmt.setDouble(3, totalAmount);
            insertStmt.setString(4, transactionDate);
            insertStmt.executeUpdate();
        }
    }
}
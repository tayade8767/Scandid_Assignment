import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.Arrays;

public class CSVtoMySQL {
    public static void main(String[] args) {
        String csvFile = "data.csv";  // Path to your CSV file
        String jdbcURL = "jdbc:mysql://localhost:3306/Scandid_Assignment";
        String dbUser = "root";
        String dbPassword = "akash@123";

        String insertCategorySQL = "INSERT INTO categories (name) VALUES (?) ON DUPLICATE KEY UPDATE id=id";
        String insertProductSQL = "INSERT INTO products (productid, title, category_id) VALUES (?, ?, ?)";
        String insertTransactionSQL = "INSERT INTO transactions (txid, store, product_id, sales, price, commission, order_date, pid, affid1, status, added_at, last_updated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            conn.setAutoCommit(true); // Ensure auto-commit is enabled

            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {  // Skip header row
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);  // Handle commas inside quotes

                // Debug: Print Read Data
                System.out.println("Reading Line: " + Arrays.toString(data));

                String txid = data[0];
                String store = data[1];
                String productid = data[2];
                String title = data[3];
                String categoryName = data[5];
                double sales = Double.parseDouble(data[6]);
                double price = Double.parseDouble(data[7]);
                double commission = Double.parseDouble(data[8]);

                Timestamp orderDate = parseTimestamp(data[9]);
                String pid = data[10];
                String affid1 = data[11];
                String status = data[12];
                Timestamp addedAt = parseTimestamp(data[13]);
                Timestamp lastUpdated = parseTimestamp(data[14]);

                // Debug: Print Values
                System.out.println("Category: " + categoryName + ", Product: " + title + ", TxID: " + txid);

                // Insert category
                int categoryId = getCategoryId(conn, insertCategorySQL, categoryName);

                // Insert product
                int productId = getProductId(conn, insertProductSQL, productid, title, categoryId);

                // Insert transaction
                try (PreparedStatement stmt = conn.prepareStatement(insertTransactionSQL)) {
                    stmt.setString(1, txid);
                    stmt.setString(2, store);
                    stmt.setInt(3, productId);
                    stmt.setDouble(4, sales);
                    stmt.setDouble(5, price);
                    stmt.setDouble(6, commission);
                    stmt.setTimestamp(7, orderDate);
                    stmt.setString(8, pid);
                    stmt.setString(9, affid1);
                    stmt.setString(10, status);
                    stmt.setTimestamp(11, addedAt);
                    stmt.setTimestamp(12, lastUpdated);
                    stmt.executeUpdate();

                    System.out.println("Inserted transaction: " + txid);
                }
            }

            System.out.println("CSV Data Successfully Imported!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper function to handle category insertion
    private static int getCategoryId(Connection conn, String insertCategorySQL, String categoryName) throws SQLException {
        int categoryId = -1;
        try (PreparedStatement stmt = conn.prepareStatement(insertCategorySQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoryName);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                categoryId = rs.getInt(1);
            } else {
                try (PreparedStatement getCatStmt = conn.prepareStatement("SELECT id FROM categories WHERE name = ?")) {
                    getCatStmt.setString(1, categoryName);
                    ResultSet catRs = getCatStmt.executeQuery();
                    if (catRs.next()) {
                        categoryId = catRs.getInt("id");
                    }
                }
            }
        }
        System.out.println("Category ID: " + categoryId);
        return categoryId;
    }

    // Helper function to handle product insertion
    private static int getProductId(Connection conn, String insertProductSQL, String productid, String title, int categoryId) throws SQLException {
        int productId = -1;
        try (PreparedStatement stmt = conn.prepareStatement(insertProductSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, productid);
            stmt.setString(2, title);
            stmt.setInt(3, categoryId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                productId = rs.getInt(1);
            } else {
                try (PreparedStatement getProdStmt = conn.prepareStatement("SELECT id FROM products WHERE productid = ?")) {
                    getProdStmt.setString(1, productid);
                    ResultSet prodRs = getProdStmt.executeQuery();
                    if (prodRs.next()) {
                        productId = prodRs.getInt("id");
                    }
                }
            }
        }
        System.out.println("Product ID: " + productId);
        return productId;
    }

    // Helper function to handle timestamp parsing
    private static Timestamp parseTimestamp(String timestampStr) {
        try {
            return Timestamp.valueOf(timestampStr.replace(", ", " ") + ":00");
        } catch (Exception e) {
            System.err.println("Error parsing timestamp: " + timestampStr);
            return null;
        }
    }
}

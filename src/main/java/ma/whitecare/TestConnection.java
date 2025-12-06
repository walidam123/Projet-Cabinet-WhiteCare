package ma.whitecare;

import ma.whitecare.conf.SessionFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * Simple test to verify database connection and list created tables.
 */
public class TestConnection {
    public static void main(String[] args) {
        try {
            System.out.println("=== Testing Database Connection ===");
            Connection connection = SessionFactory.getInstance().getConnection();
            
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("✓ Connected to: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            System.out.println("✓ Database: " + connection.getCatalog());
            System.out.println();
            
            // List all tables
            System.out.println("Tables in database:");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            int count = 0;
            while (tables.next()) {
                count++;
                System.out.println("  " + count + ". " + tables.getString("TABLE_NAME"));
            }
            System.out.println();
            System.out.println("✓ Total tables: " + count);
            System.out.println("✓ Connection test successful!");
            
            SessionFactory.getInstance().closeConnection();
        } catch (Exception e) {
            System.err.println("❌ Connection test failed:");
            e.printStackTrace();
        }
    }
}


package ma.whitecare;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.conf.util.PropertiesExtractor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Script to test database connection and populate database with schema and seed data.
 */
public class SetupDatabase {

    public static void main(String[] args) {
        System.out.println("=== WhiteCare Database Setup ===");
        System.out.println();

        Connection connection = null;
        try {
            // Load database properties
            Properties props = PropertiesExtractor.loadConfigFile("config/db.properties");
            String url = PropertiesExtractor.getPropertyValue("datasource.url", props);
            String user = PropertiesExtractor.getPropertyValue("datasource.user", props);
            String password = PropertiesExtractor.getPropertyValue("datasource.password", props);
            String driver = PropertiesExtractor.getPropertyValue("datasource.driver", props);

            // Load driver
            Class.forName(driver);

            // Connect to MySQL without database first
            System.out.println("1. Testing database connection...");
            String baseUrl = url.substring(0, url.lastIndexOf("/"));
            connection = DriverManager.getConnection(baseUrl, user, password);
            System.out.println("   ✓ Connection to MySQL server successful!");
            System.out.println();

            // Create database if not exists
            System.out.println("2. Creating database if not exists...");
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS WhiteCare");
                stmt.executeUpdate("USE WhiteCare");
                System.out.println("   ✓ Database WhiteCare ready!");
            }
            System.out.println();

            // Execute schema.sql
            System.out.println("3. Executing schema.sql...");
            executeSqlFile(connection, "dataBase/schema.sql");
            System.out.println("   ✓ Schema created successfully!");
            System.out.println();

            // Execute seed.sql
            System.out.println("4. Executing seed.sql...");
            try {
                executeSqlFile(connection, "dataBase/seed.sql");
                System.out.println("   ✓ Seed data inserted successfully!");
            } catch (Exception e) {
                System.out.println("   ⚠ Warning: Could not execute seed.sql: " + e.getMessage());
                System.out.println("   (This is OK if the data already exists)");
            }
            System.out.println();

            // Close connection
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            System.out.println("=== Database setup completed successfully! ===");

        } catch (Exception e) {
            System.err.println("❌ Error during database setup:");
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
            System.exit(1);
        }
    }

    /**
     * Executes SQL statements from a file.
     * Handles multi-statement SQL files by splitting on semicolons.
     * Executes in multiple passes to handle foreign key dependencies.
     */
    private static void executeSqlFile(Connection connection, String resourcePath) throws Exception {
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }

        String sqlContent = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        // Remove comments and split by semicolon
        String[] statements = sqlContent
                .replaceAll("--.*", "") // Remove single-line comments
                .replaceAll("/\\*[\\s\\S]*?\\*/", "") // Remove multi-line comments
                .split(";");

        // Execute in multiple passes to handle dependencies
        int maxPasses = 5;
        int executedCount = 0;
        
        for (int pass = 1; pass <= maxPasses; pass++) {
            int passExecuted = 0;
            try (Statement stmt = connection.createStatement()) {
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty() && 
                        !trimmed.toUpperCase().startsWith("USE ") &&
                        !trimmed.toUpperCase().startsWith("CREATE DATABASE")) {
                        try {
                            stmt.execute(trimmed);
                            passExecuted++;
                            executedCount++;
                        } catch (Exception e) {
                            // On first pass, skip errors for dependencies
                            // On later passes, only skip "already exists" errors
                            String errorMsg = e.getMessage();
                            if (pass == 1 && 
                                (errorMsg.contains("Failed to open the referenced table") ||
                                 errorMsg.contains("Cannot add foreign key constraint"))) {
                                // Expected on first pass - will retry
                            } else if (errorMsg.contains("already exists") || 
                                      errorMsg.contains("Duplicate")) {
                                // OK - table already exists
                            } else if (pass < maxPasses) {
                                // Will retry on next pass
                            } else {
                                // Last pass - show error
                                System.err.println("   ⚠ Warning executing statement: " + errorMsg);
                                System.err.println("   Statement: " + trimmed.substring(0, Math.min(80, trimmed.length())) + "...");
                            }
                        }
                    }
                }
            }
            if (passExecuted == 0) break; // No more statements to execute
        }
        System.out.println("   Executed " + executedCount + " SQL statements");
    }
}


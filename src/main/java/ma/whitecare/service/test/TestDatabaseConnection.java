package ma.whitecare.service.test;

import ma.whitecare.conf.SessionFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONNEXION À LA BASE DE DONNÉES ===\n");
        
        try {
            SessionFactory sessionFactory = SessionFactory.getInstance();
            Connection connection = sessionFactory.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Connexion établie avec succès !");
                
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("✓ Base de données: " + metaData.getDatabaseProductName());
                System.out.println("✓ Version: " + metaData.getDatabaseProductVersion());
                System.out.println("✓ URL: " + metaData.getURL());
                System.out.println("✓ Utilisateur: " + metaData.getUserName());
                
                // Test simple query
                try (var stmt = connection.createStatement();
                     var rs = stmt.executeQuery("SELECT 1")) {
                    if (rs.next()) {
                        System.out.println("✓ Test de requête SQL réussi");
                    }
                }
                
                System.out.println("\n=== CONNEXION VALIDÉE ===");
            } else {
                System.out.println("✗ Échec de la connexion");
            }
            
        } catch (SQLException e) {
            System.err.println("\n✗ ERREUR de connexion: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n✗ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


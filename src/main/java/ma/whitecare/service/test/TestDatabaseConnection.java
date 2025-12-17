package ma.whitecare.service.test;

import ma.whitecare.conf.SessionFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONNEXION À LA BASE DE DONNÉES ===\n");
        
        try {
            SessionFactory sessionFactory = SessionFactory.getInstance();
            Connection connection = sessionFactory.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Connexion établie avec succès !\n");
                
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("📊 Informations de la base de données:");
                System.out.println("   • Base de données: " + metaData.getDatabaseProductName());
                System.out.println("   • Version: " + metaData.getDatabaseProductVersion());
                System.out.println("   • URL: " + metaData.getURL());
                System.out.println("   • Utilisateur: " + metaData.getUserName());
                System.out.println("   • Nom de la base: WhiteCare\n");
                
                // Test simple query
                try (var stmt = connection.createStatement();
                     var rs = stmt.executeQuery("SELECT 1 as test")) {
                    if (rs.next()) {
                        System.out.println("✓ Test de requête SQL réussi");
                    }
                }
                
                // Vérifier que les tables existent
                System.out.println("\n📋 Vérification des tables:");
                List<String> expectedTables = List.of(
                    "utilisateur", "cabinet_medicale", "staff", "medecin", "secretaire",
                    "role", "utilisateur_role", "notification", "utilisateur_notification",
                    "revenues", "charges", "statistiques", "patient", "antecedents",
                    "patient_antecedents", "acte", "medicament", "dossierMedicale",
                    "consultation", "ordonnance", "prescription", "certificat", "rdv",
                    "intervention_medecin", "situation_financiere", "facture",
                    "agenda_mensuel", "jour_agenda", "creneau_horaire"
                );
                
                List<String> existingTables = getExistingTables(connection, "WhiteCare");
                int foundCount = 0;
                
                for (String table : expectedTables) {
                    if (existingTables.contains(table)) {
                        System.out.println("   ✓ " + table);
                        foundCount++;
                    } else {
                        System.out.println("   ✗ " + table + " (MANQUANTE)");
                    }
                }
                
                System.out.println("\n📈 Résumé:");
                System.out.println("   • Tables attendues: " + expectedTables.size());
                System.out.println("   • Tables trouvées: " + foundCount);
                
                if (foundCount == expectedTables.size()) {
                    System.out.println("\n✅ TOUTES LES TABLES SONT PRÉSENTES !");
                } else {
                    System.out.println("\n⚠️  Certaines tables sont manquantes. Vérifiez l'import du schema.sql");
                }
                
                // Test de lecture depuis une table
                System.out.println("\n🔍 Test de lecture des données:");
                try (var stmt = connection.createStatement();
                     var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM role")) {
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        System.out.println("   • Nombre de rôles dans la table 'role': " + count);
                        if (count > 0) {
                            System.out.println("   ✓ Données trouvées dans la base");
                        } else {
                            System.out.println("   ⚠️  Table vide - pensez à importer seed.sql");
                        }
                    }
                }
                
                System.out.println("\n=== CONNEXION VALIDÉE ===");
            } else {
                System.out.println("✗ Échec de la connexion");
            }
            
        } catch (SQLException e) {
            System.err.println("\n✗ ERREUR de connexion: " + e.getMessage());
            System.err.println("   Code d'erreur SQL: " + e.getErrorCode());
            System.err.println("   État SQL: " + e.getSQLState());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n✗ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static List<String> getExistingTables(Connection connection, String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        
        try (ResultSet rs = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName);
            }
        }
        
        return tables;
    }
}








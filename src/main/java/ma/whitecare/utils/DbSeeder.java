package ma.whitecare.utils;

import ma.whitecare.conf.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;

public class DbSeeder {
    public static void main(String[] args) {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            System.out.println("Connexion à la base de données réussie.");

            // 1. S'assurer que le rôle ADMIN existe
            long adminRoleId = ensureRoleExists(conn, "ADMIN");
            System.out.println("Rôle ADMIN vérifié (ID: " + adminRoleId + ")");

            // 2. Créer/Mettre à jour l'utilisateur admin
            String login = "admin";
            String password = "admin";
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

            long userId = ensureAdminUserExists(conn, login, passwordHash);
            System.out.println("Utilisateur admin vérifié (ID: " + userId + ")");

            // 3. Assigner le rôle à l'utilisateur
            ensureUserHasRole(conn, userId, adminRoleId);
            System.out.println("Rôle ADMIN assigné à l'utilisateur admin.");

            System.out.println("\nSuccès ! Vous pouvez maintenant vous connecter avec admin / admin.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long ensureRoleExists(Connection conn, String libelle) throws SQLException {
        String checkSql = "SELECT id FROM role WHERE libelle = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong(1);
            }
        }

        String insertSql = "INSERT INTO role (libelle, creation_date, last_modification_date, created_by, updated_by) VALUES (?, NOW(), NOW(), 'system', 'system')";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libelle);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getLong(1);
            }
        }
        throw new RuntimeException("Erreur lors de la création du rôle.");
    }

    private static long ensureAdminUserExists(Connection conn, String login, String passwordHash) throws SQLException {
        String checkSql = "SELECT id FROM utilisateur WHERE login = ?";
        Long existingId = null;
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    existingId = rs.getLong(1);
            }
        }

        if (existingId != null) {
            String updateSql = "UPDATE utilisateur SET password_hash = ?, actif = 1 WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, passwordHash);
                ps.setLong(2, existingId);
                ps.executeUpdate();
            }
            return existingId;
        } else {
            String insertSql = "INSERT INTO utilisateur (nom, prenom, email, login, password_hash, cin, tel, sexe, actif, creation_date, last_modification_date, created_by, updated_by) "
                    +
                    "VALUES ('ADMIN', 'System', 'admin@whitecare.ma', ?, ?, 'ADMIN01', '0600000000', 'HOMME', 1, NOW(), NOW(), 'system', 'system')";
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, login);
                ps.setString(2, passwordHash);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        return rs.getLong(1);
                }
            }
        }
        throw new RuntimeException("Erreur lors de la création de l'utilisateur.");
    }

    private static void ensureUserHasRole(Connection conn, long userId, long roleId) throws SQLException {
        String checkSql = "SELECT 1 FROM utilisateur_role WHERE utilisateur_id = ? AND role_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return; // Déjà assigné
            }
        }

        String insertSql = "INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        }
    }
}

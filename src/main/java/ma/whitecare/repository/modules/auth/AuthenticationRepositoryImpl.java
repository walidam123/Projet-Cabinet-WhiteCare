package ma.whitecare.repository.modules.auth;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.repository.common.RowMappers;
import org.mindrot.jbcrypt.BCrypt;


import java.sql.*;

import java.util.Optional;

public class AuthenticationRepositoryImpl implements AuthentificationRepository {

    private static final int BCRYPT_LOG_ROUNDS = 7;
    @Override
    public Optional<Utilisateur> authenticate(String login, String password) {
        String sql = """
            SELECT u.* 
            FROM utilisateur u
            WHERE u.login = ? AND u.actif = true
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = RowMappers.mapUtilisateur(rs);

                    // Vérifier le mot de passe
                    String hashedPassword = rs.getString("password_hash");
                    if (verifyPassword(password, hashedPassword)) {
                        // Mettre à jour la dernière connexion
                        updateLastLogin(user.getIdUser());
                        return Optional.of(user);
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'authentification pour l'email: " + login, e);
        }

        return Optional.empty();
    }


    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        try {
            if (rawPassword == null || rawPassword.trim().isEmpty()) {
                return false;
            }

            if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
                return false;
            }

            // Vérifier que le hash a le format BCrypt
            if (!hashedPassword.startsWith("$2a$") && !hashedPassword.startsWith("$2b$") &&
                    !hashedPassword.startsWith("$2y$")) {
                throw new IllegalArgumentException("Le hash fourni n'est pas au format BCrypt");
            }

            // Vérifier le mot de passe avec BCrypt
            return BCrypt.checkpw(rawPassword, hashedPassword);

        } catch (IllegalArgumentException e) {
            System.err.println("Format de hash invalide: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du mot de passe: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String hashPassword(String password) {
        try {
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
            }

            // Générer un sel et hasher le mot de passe avec BCrypt
            String salt = BCrypt.gensalt(BCRYPT_LOG_ROUNDS);
            return BCrypt.hashpw(password, salt);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erreur lors du hashage du mot de passe: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur technique lors du hashage du mot de passe", e);
        }
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        String sql = """
            UPDATE utilisateur 
            SET password_hash = ?, 
                last_modification_date = CURRENT_TIMESTAMP
                
            WHERE id = ?
            """;

        String hashedPassword = hashPassword(newPassword);

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hashedPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur changement mot de passe utilisateur ID: " + userId, e);
        }
    }





    @Override
    public void updateLastLogin(Long userId) {
        String sql = "UPDATE utilisateur SET last_login_date = CURRENT_TIMESTAMP WHERE id = ?";


        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour dernière connexion", e);
        }
    }

    @Override
    public boolean validateUserSession(Long userId, String token) {
        return false;
    }

    @Override
    public void invalidateUserSessions(Long userId) {

    }
}

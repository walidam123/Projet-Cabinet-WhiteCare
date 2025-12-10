package ma.whitecare.repository.modules.auth;

import ma.whitecare.entities.user.Utilisateur;

import java.util.Optional;

public interface AuthentificationRepository {


    // ========== MÉTHODES D'AUTHENTIFICATION ==========
    Optional<Utilisateur> authenticate(String login, String password);
    boolean verifyPassword(String rawPassword, String hashedPassword);
    String hashPassword(String password);


    void changePassword(Long userId, String newPassword);


    void updateLastLogin(Long userId);

}

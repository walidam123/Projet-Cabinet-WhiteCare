package ma.whitecare.repository.test;

import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.repository.modules.UserManager.impl.UtilisateurRepositoryImpl;
import ma.whitecare.repository.modules.auth.AuthenticationRepositoryImpl;
import ma.whitecare.repository.modules.auth.AuthentificationRepository;

import java.util.Optional;

public class AuthentificationRepositoryTest {

    public static void main(String[] args) {
        System.out.println("=== TEST AUTH SIMPLE ===\n");

        AuthentificationRepository authRepo = new AuthenticationRepositoryImpl();
        UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();

        try {
            // 1. Test hash/verify
            System.out.println("1. Test hash/verify");
            String password = "MyPassword123";
            String hash = authRepo.hashPassword(password);
            System.out.println("Hash: " + hash.substring(0, 20) + "...");

            boolean valid = authRepo.verifyPassword(password, hash);
            System.out.println("Verify correct: " + valid);

            boolean invalid = authRepo.verifyPassword("wrong", hash);
            System.out.println("Verify wrong: " + invalid);
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            // 2. Créer un utilisateur
            System.out.println("\n2. Création utilisateur");
            Utilisateur user = new Utilisateur();
            user.setNom("Doe");
            user.setPrenom("John");
            user.setEmail("john.doe@test.com");
            user.setLogin("jdoe");
            user.setActif(true);
            user.setMotDePass("Pass123");
            user.setCreePar("test");
            user.setModifiePar("test");

            userRepo.create(user);
            Long userId = user.getIdUser();
            System.out.println("User ID: " + userId);
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            // 3. Test authenticate
            System.out.println("\n3. Test authenticate");
            Optional<Utilisateur> authResult = authRepo.authenticate("jdoe", "Pass123");
            System.out.println("Auth success: " + authResult.isPresent());
            System.out.println(authResult.toString());
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            // 4. Test change password
            System.out.println("\n4. Test change password");
            authRepo.changePassword(userId, "NewPass456");
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            // Re-tester avec nouveau mot de passe
            Optional<Utilisateur> authNew = authRepo.authenticate("jdoe", "NewPass456");
            System.out.println("Auth with new password: " + authNew.isPresent());
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            // 5. Test last login
            System.out.println("\n5. Test update last login");
            authRepo.updateLastLogin(userId);
            System.out.println("Last login updated");

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            // 8. Nettoyage
            System.out.println("\n8. Nettoyage");
            userRepo.deleteById(userId);
            System.out.println("User deleted");

            System.out.println("\n✅ Test terminé avec succès!");

        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

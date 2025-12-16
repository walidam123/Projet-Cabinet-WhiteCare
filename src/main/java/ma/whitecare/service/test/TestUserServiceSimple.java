package ma.whitecare.service.test;




import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.mvc.dto.UserDto.CreateUserDTO;
import ma.whitecare.mvc.dto.UserDto.UpdateUserDTO;
import ma.whitecare.repository.modules.UserManager.api.RoleRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.repository.modules.UserManager.impl.RoleRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.UtilisateurRepositoryImpl;
import ma.whitecare.service.modules.UserManager.impl.UserServiceImpl;


import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;

public class TestUserServiceSimple {

    public static void main(String[] args) {
        System.out.println("=== TEST SIMPLE DU USER SERVICE ===\n");

        try {
            // 1. Initialisation avec des mocks simples
            UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();
            RoleRepository roleRepo = new RoleRepositoryImpl();
            UserServiceImpl userService = new UserServiceImpl(userRepo, roleRepo);

            // 2. TEST 1: Création d'utilisateur
            System.out.println("Test 1: Création d'un utilisateur");
            CreateUserDTO createDTO = new CreateUserDTO();
            createDTO.setNom("Dupont");
            createDTO.setPrenom("Jean");
            createDTO.setLogin("jdupont");
            createDTO.setCin("AB123456");
            createDTO.setEmail("jeai.dupont@email.com");
            createDTO.setTelephone("0612345678");
            createDTO.setAdresse("123 Rue de Paris");
            createDTO.setDateNaissance(LocalDate.of(1985, 5, 15));
            createDTO.setPassword("Mot2Passe@123");
            createDTO.setSexe(Sexe.HOMME);
            createDTO.setActif(true);
            createDTO.setRoles(Arrays.asList(LibelleRole.MEDECIN));

            Utilisateur user = userService.createUser(createDTO);
            System.out.println("✓ Utilisateur créé avec ID: " + user.getIdUser());
            System.out.println("  Nom: " + user.getNom());
            System.out.println("  Login: " + user.getLogin());

            // 3. TEST 2: Récupération par ID
            System.out.println("\nTest 2: Récupération par ID");
            Utilisateur foundUser = userService.getUserById(user.getIdUser());
            System.out.println("✓ Utilisateur trouvé: " + foundUser.getNom() + " " + foundUser.getPrenom());

            // 4. TEST 3: Liste de tous les utilisateurs
            System.out.println("\nTest 3: Liste des utilisateurs");
            List<Utilisateur> allUsers = userService.getAllUsers();
            System.out.println("✓ Nombre total d'utilisateurs: " + allUsers.size());

            // 5. TEST 4: Recherche par login
            System.out.println("\nTest 4: Recherche par login");
            boolean loginExists = userService.findByLogin("jdupont").isPresent();
            System.out.println("✓ Login 'jdupont' existe: " + loginExists);

            // 6. TEST 5: Recherche par CIN
            System.out.println("\nTest 5: Recherche par CIN");
            boolean cinExists = userService.findByCin("AB123456").isPresent();
            System.out.println("✓ CIN 'AB123456' existe: " + cinExists);

            // 7. TEST 6: Vérification disponibilité
            System.out.println("\nTest 6: Vérification disponibilité");
            boolean loginAvailable = userService.isLoginAvailable("nouveau");
            boolean cinAvailable = userService.isCinAvailable("CD789012");
            System.out.println("✓ Login 'nouveau' disponible: " + loginAvailable);
            System.out.println("✓ CIN 'CD789012' disponible: " + cinAvailable);
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 8. TEST 7: Mise à jour d'utilisateur
            System.out.println("\nTest 7: Mise à jour d'utilisateur");
            UpdateUserDTO updateDTO = new UpdateUserDTO();
            updateDTO.setNom("Dupont-Modifié");
            updateDTO.setPrenom("Jean-Michel");
            updateDTO.setEmail("jeanmichel.dupont@email.com");
            updateDTO.setTelephone("0698765432");
            updateDTO.setAdresse("456 Avenue des Champs");
            updateDTO.setDateNaissance(LocalDate.of(1985, 5, 15));
            updateDTO.setSexe(Sexe.HOMME);
            updateDTO.setActif(true);
            updateDTO.setRoles(Arrays.asList(LibelleRole.MEDECIN, LibelleRole.ADMIN));

            Utilisateur updated = userService.updateUser(user.getIdUser(), updateDTO);
            System.out.println("✓ Utilisateur mis à jour");
            System.out.println("  Nouveau nom: " + updated.getNom());
            System.out.println("  Nouveau prénom: " + updated.getPrenom());

            // 9. TEST 8: Recherche par sexe
            System.out.println("\nTest 8: Recherche par sexe");
            List<Utilisateur> hommes = userService.findBySexe(Sexe.HOMME);
            System.out.println("✓ Nombre d'hommes: " + hommes.size());

            // 10. TEST 9: Recherche actifs/inactifs
            System.out.println("\nTest 9: Recherche par statut");
            List<Utilisateur> actifs = userService.findActifs();
            List<Utilisateur> inactifs = userService.findInactifs();
            System.out.println("✓ Actifs: " + actifs.size());
            System.out.println("✓ Inactifs: " + inactifs.size());
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 11. TEST 10: Activation/désactivation
            System.out.println("\nTest 10: Activation/Désactivation");
            userService.deactivateUser(user.getIdUser());
            System.out.println("✓ Utilisateur désactivé");

            userService.activateUser(user.getIdUser());
            System.out.println("✓ Utilisateur réactivé");
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 12. TEST 11: Assignation de rôle
            System.out.println("\nTest 11: Gestion des rôles");
            userService.assignRoleToUser(user.getIdUser(), LibelleRole.SECRETAIRE);
            System.out.println("✓ Rôle assigné");

            List<LibelleRole> roles = userService.getUserRoles(user.getIdUser());
            System.out.println("✓ Nombre de rôles: " + roles.size());

            // 13. TEST 12: Comptage
            System.out.println("\nTest 12: Statistiques");
            long total = userService.countAllUsers();
            System.out.println("✓ Total utilisateurs: " + total);
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 14. TEST 13: Suppression
            System.out.println("\nTest 13: Suppression d'utilisateur");
            userService.deleteUser(user.getIdUser());
            System.out.println("✓ Utilisateur supprimé");

            // Vérifier qu'il n'existe plus
            try {
                userService.getUserById(user.getIdUser());
                System.out.println("✗ ERREUR: L'utilisateur devrait être supprimé");
            } catch (Exception e) {
                System.out.println("✓ L'utilisateur a bien été supprimé (exception attendue)");
            }

            System.out.println("\n=== TESTS TERMINÉS AVEC SUCCÈS ===");

        } catch (Exception e) {
            System.err.println("\n✗ ERREUR pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
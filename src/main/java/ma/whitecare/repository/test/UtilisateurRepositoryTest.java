package ma.whitecare.repository.test;



import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Role;

import ma.whitecare.repository.modules.UserManager.api.RoleRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.repository.modules.UserManager.impl.RoleRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.UtilisateurRepositoryImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepositoryTest {

    private static UtilisateurRepository utilisateurRepo;
    private static RoleRepository roleRepo;
    private static Utilisateur utilisateurTest1;
    private static Utilisateur utilisateurTest2;
    private static Utilisateur utilisateurTest3;
    private static Role roleMedecin;
    private static Role roleSecretaire;
    private static Role roleAdmin;

    public static void main(String[] args) {
        System.out.println("🚀 TEST - UtilisateurRepository\n");

        utilisateurRepo = new UtilisateurRepositoryImpl();
        roleRepo = new RoleRepositoryImpl();

        try {
            setupTestData();
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));


            testCRUDOperations();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testFindByAttributes();


            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testFindByEnum();
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));


            testFindByStatus();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testFindByRole();
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testStatistics();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));
            testPagination();
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testUpdateOperations();
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testBulkOperations();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            cleanupTestData();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));
            System.out.println("\n🎉 TOUS LES TESTS TERMINÉS AVEC SUCCÈS!");

        } catch (Exception e) {
            System.out.println("❌ Erreur pendant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupTestData() {
        System.out.println("🔧 Configuration des données de test...\n");

        // Créer les rôles nécessaires
        createRoles();

        // Créer les utilisateurs de test
        createTestUsers();

        // Assigner les rôles aux utilisateurs
        assignRolesToUsers();
    }

    private static void createRoles() {
        System.out.println("1. Création des rôles...");

        // Rôle Médecin
        roleMedecin = Role.builder()
                .libelle(LibelleRole.MEDECIN)
                .creePar("test_system")
                .modifiePar("test_system")
                .build();
        roleRepo.create(roleMedecin);
        System.out.println("✅ Rôle MEDECIN créé");

        // Rôle Secrétaire
        roleSecretaire = Role.builder()
                .libelle(LibelleRole.SECRETAIRE)
                .creePar("test_system")
                .modifiePar("test_system")
                .build();
        roleRepo.create(roleSecretaire);
        System.out.println("✅ Rôle SECRETAIRE créé");

        // Rôle Admin
        roleAdmin = Role.builder()
                .libelle(LibelleRole.ADMIN)
                .creePar("test_system")
                .modifiePar("test_system")
                .build();
        roleRepo.create(roleAdmin);
        System.out.println("✅ Rôle ADMIN créé");
    }

    private static void createTestUsers() {
        System.out.println("\n2. Création des utilisateurs de test...");

        // Utilisateur 1 (Homme, Actif, Médecin)
        utilisateurTest1 = Utilisateur.builder()
                .nom("Alami")
                .prenom("Ahmed")
                .email("ahmed.alami@test.com")
                .cin("AB123456")
                .tel("0612345678")
                .sexe(Sexe.HOMME)
                .login("ahmedalami")
                .motDePass("hash123")
                .dateNaissance(LocalDate.of(1985, 5, 15))
                .actif(true)
                .creePar("test_system")
                .modifiePar("test_system")
                .build();
        utilisateurRepo.create(utilisateurTest1);
        System.out.println("✅ Utilisateur 1 créé (Ahmed Alami)");

        // Utilisateur 2 (Femme, Actif, Secrétaire)
        utilisateurTest2 = Utilisateur.builder()
                .nom("Benani")
                .prenom("Fatima")
                .email("fatima.benani@test.com")
                .cin("CD789012")
                .tel("0623456789")
                .sexe(Sexe.FEMME)
                .login("fatimab")
                .motDePass("hash456")
                .dateNaissance(LocalDate.of(1990, 8, 22))
                .actif(true)
                .creePar("test_system")
                .modifiePar("test_system")
                .build();
        utilisateurRepo.create(utilisateurTest2);
        System.out.println("✅ Utilisateur 2 créé (Fatima Benani)");

        // Utilisateur 3 (Homme, Inactif, Admin)
        utilisateurTest3 = Utilisateur.builder()
                .nom("Alami") // Même nom que utilisateurTest1 pour tester la recherche
                .prenom("Karim")
                .email("karim.alami@test.com")
                .cin("EF345678")
                .tel("0634567890")
                .sexe(Sexe.HOMME)
                .login("karima")
                .motDePass("hash789")
                .dateNaissance(LocalDate.of(1978, 3, 10))
                .actif(false) // Inactif
                .creePar("test_system")
                .modifiePar("test_system")
                .build();
        utilisateurRepo.create(utilisateurTest3);
        System.out.println("✅ Utilisateur 3 créé (Karim Alami - inactif)");
    }

    private static void assignRolesToUsers() {
        System.out.println("\n3. Attribution des rôles...");

        // Ahmed = Médecin
        roleRepo.assignRoleToUser(utilisateurTest1.getIdUser(), roleMedecin.getIdRole());
        System.out.println("✅ Rôle MEDECIN attribué à Ahmed");

        // Fatima = Secrétaire
        roleRepo.assignRoleToUser(utilisateurTest2.getIdUser(), roleSecretaire.getIdRole());
        System.out.println("✅ Rôle SECRETAIRE attribué à Fatima");

        // Karim = Admin
        roleRepo.assignRoleToUser(utilisateurTest3.getIdUser(), roleAdmin.getIdRole());
        System.out.println("✅ Rôle ADMIN attribué à Karim");
    }

    private static void testCRUDOperations() {
        System.out.println("\n📋 === TEST OPERATIONS CRUD ===\n");

        // 1. Test FIND ALL
        System.out.println("1. 📋 Test findAll...");
        List<Utilisateur> allUsers = utilisateurRepo.findAll();
        assertTrue(!allUsers.isEmpty(), "La liste des utilisateurs ne doit pas être vide");
        System.out.println("✅ " + allUsers.size() + " utilisateurs trouvés");
        allUsers.forEach(u ->  System.out.println(u.toString()));
        // 2. Test FIND BY ID
        System.out.println("\n2. 🔍 Test findById...");
        Utilisateur foundUser = utilisateurRepo.findById(utilisateurTest1.getIdUser());
        assertNotNull(foundUser, "L'utilisateur doit être trouvé par ID");
        assertEquals("Ahmed", foundUser.getPrenom());
        System.out.println("✅ Utilisateur trouvé: " + foundUser.getNom() + " " + foundUser.getPrenom());

        // 3. Test UPDATE
        System.out.println("\n3. ✏️ Test update...");
        utilisateurTest1.setNom("Alami Modifié");
        utilisateurRepo.update(utilisateurTest1);

        Utilisateur updatedUser = utilisateurRepo.findById(utilisateurTest1.getIdUser());
        assertEquals("Alami Modifié", updatedUser.getNom());
        System.out.println("✅ Utilisateur modifié: " + updatedUser.getNom());

        // Remettre le nom original pour les autres tests
        utilisateurTest1.setNom("Alami");
        utilisateurRepo.update(utilisateurTest1);

        // 4. Test CREATE (un nouvel utilisateur)
        System.out.println("\n4. 📝 Test create (nouvel utilisateur)...");
        Utilisateur newUser = Utilisateur.builder()
                .nom("Test")
                .prenom("Nouveau")
                .email("nouveau@test.com")
                .login("nouveau")
                .motDePass("hash")
                .actif(true)
                .creePar("test_system")
                .modifiePar("test_system")
                .build();
        utilisateurRepo.create(newUser);
        System.out.println("✅ Nouvel utilisateur créé avec ID: " + newUser.getIdUser());


        testFindByEnum();
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-".repeat(50));
        // 5. Test DELETE
        System.out.println("\n5. 🗑️ Test delete...");
        utilisateurRepo.deleteById(newUser.getIdUser());
        Utilisateur deletedUser = utilisateurRepo.findById(newUser.getIdUser());
        assertNull(deletedUser, "L'utilisateur doit être supprimé");
        System.out.println("✅ Utilisateur supprimé");
    }

    private static void testFindByAttributes() {
        System.out.println("\n\n🔍 === TEST FIND BY ATTRIBUTES ===\n");

        // 1. Test FIND BY LOGIN
        System.out.println("1. 🔑 Test findByLogin...");
        Optional<Utilisateur> userByLogin = utilisateurRepo.findByLogin("ahmedalami");
        assertTrue(userByLogin.isPresent(), "Doit trouver l'utilisateur par login");
        assertEquals("Ahmed", userByLogin.get().getPrenom());
        System.out.println("✅ Utilisateur trouvé par login");

        // 2. Test FIND BY CIN
        System.out.println("\n2. 🆔 Test findByCin...");
        Optional<Utilisateur> userByCin = utilisateurRepo.findByCin("CD789012");
        assertTrue(userByCin.isPresent(), "Doit trouver l'utilisateur par CIN");
        assertEquals("Fatima", userByCin.get().getPrenom());
        System.out.println("✅ Utilisateur trouvé par CIN");

        // 3. Test FIND BY NOM AND PRENOM
        System.out.println("\n3. 👤 Test findByNomAndPrenom...");
        List<Utilisateur> usersByName = utilisateurRepo.findByNomAndPrenom("Alami", "Ahmed");

        assertEquals("ahmed.alami@test.com", usersByName.get(0).getEmail());
        System.out.println("✅ Utilisateur trouvé par nom et prénom");


    }

    private static void testFindByEnum() {
        System.out.println("\n\n🏷️ === TEST FIND BY ENUM ===\n");

        // 1. Test FIND BY SEXE
        System.out.println("1. 👨 Test findBySexe (HOMME)...");
        List<Utilisateur> hommes = utilisateurRepo.findBySexe(Sexe.HOMME);
        assertTrue(!hommes.isEmpty(), "Doit trouver des hommes");
        System.out.println("✅ " + hommes.size() + " hommes trouvés");
        hommes.forEach(Utilisateur -> System.out.println(Utilisateur.getNom()+" "+Utilisateur.getPrenom()));
        // 2. Test FIND HOMMES
        System.out.println("\n2. 👨 Test findHommes...");
        List<Utilisateur> hommes2 = utilisateurRepo.findHommes();
        System.out.println("✅ " + hommes2.size() + " hommes trouvés");

        // 3. Test FIND FEMMES
        System.out.println("\n3. 👩 Test findFemmes...");
        List<Utilisateur> femmes = utilisateurRepo.findFemmes();
        assertTrue(!femmes.isEmpty(), "Doit trouver des femmes");
        System.out.println("✅ " + femmes.size() + " femmes trouvés");

        // 4. Test FIND BY SEXE (FEMME)
        System.out.println("\n4. 👩 Test findBySexe (FEMME)...");
        List<Utilisateur> femmes2 = utilisateurRepo.findBySexe(Sexe.FEMME);
        System.out.println("✅ " + femmes2.size() + " femmes trouvés");
    }

    private static void testFindByStatus() {
        System.out.println("\n\n📊 === TEST FIND BY STATUS ===\n");

        // 1. Test FIND BY ACTIF
        System.out.println("1. ✅ Test findByActif (true)...");
        List<Utilisateur> actifs = utilisateurRepo.findByActif(true);
        assertTrue(!actifs.isEmpty(), "Doit trouver des utilisateurs actifs");
        System.out.println("✅ " + actifs.size() + " utilisateurs actifs trouvés");

        // 2. Test FIND ACTIFS
        System.out.println("\n2. ✅ Test findActifs...");
        List<Utilisateur> actifs2 = utilisateurRepo.findActifs();
        System.out.println("✅ " + actifs2.size() + " utilisateurs actifs trouvés");

        // 3. Test FIND BY ACTIF (false)
        System.out.println("\n3. ❌ Test findByActif (false)...");
        List<Utilisateur> inactifs = utilisateurRepo.findByActif(false);
        assertTrue(!inactifs.isEmpty(), "Doit trouver des utilisateurs inactifs");
        System.out.println("✅ " + inactifs.size() + " utilisateurs inactifs trouvés");

        // 4. Test FIND INACTIFS
        System.out.println("\n4. ❌ Test findInactifs...");
        List<Utilisateur> inactifs2 = utilisateurRepo.findInactifs();

        System.out.println("✅ " + inactifs2.size() + " utilisateurs inactifs trouvés");
    }

    private static void testFindByRole() {
        System.out.println("\n\n👥 === TEST FIND BY ROLE ===\n");

        // 1. Test FIND MEDECINS
        System.out.println("1. 🩺 Test findMedecins...");
        List<Utilisateur> medecins = utilisateurRepo.findMedecins();
        assertTrue(!medecins.isEmpty(), "Doit trouver des médecins");
        assertEquals("Ahmed", medecins.get(0).getPrenom());
        System.out.println("✅ " + medecins.size() + " médecin(s) trouvé(s)");

        medecins.forEach(u ->  System.out.println(u.toString()));

        // 2. Test FIND SECRETAIRES
        System.out.println("\n2. 📝 Test findSecretaires...");
        List<Utilisateur> secretaires = utilisateurRepo.findSecretaires();
        assertTrue(!secretaires.isEmpty(), "Doit trouver des secrétaires");
        assertEquals("Fatima", secretaires.get(0).getPrenom());
        System.out.println("✅ " + secretaires.size() + " secrétaire(s) trouvé(s)");
        secretaires.forEach(u ->  System.out.println(u.toString()));

        // 3. Test FIND ADMINS
        System.out.println("\n3. 👨‍💼 Test findAdmins...");
        List<Utilisateur> admins = utilisateurRepo.findAdmins();
        assertTrue(!admins.isEmpty(), "Doit trouver des administrateurs");
        // Note: Karim est inactif, donc peut ne pas apparaître selon l'implémentation
        System.out.println("✅ " + admins.size() + " admin(s) trouvé(s)");
        admins.forEach(u ->  System.out.println(u.toString()));

    }

    private static void testStatistics() {
        System.out.println("\n\n📈 === TEST STATISTIQUES ===\n");

        // 1. Test COUNT ALL
        System.out.println("1. 📊 Test countAll...");
        long totalUsers = utilisateurRepo.countAll();
        assertTrue(totalUsers > 0, "Doit compter des utilisateurs");
        System.out.println("✅ " + totalUsers + " utilisateurs au total");
    }

    private static void testPagination() {
        System.out.println("\n\n📄 === TEST PAGINATION ===\n");

        // 1. Test FIND WITH PAGINATION
        System.out.println("1. 📄 Test findWithPagination...");
        List<Utilisateur> page1 = utilisateurRepo.findWithPagination(0, 2);

        System.out.println("✅ Page 1: " + page1.size() + " utilisateurs");

        List<Utilisateur> page2 = utilisateurRepo.findWithPagination(2, 2);
        System.out.println("✅ Page 2: " + page2.size() + " utilisateurs");
    }

    private static void testUpdateOperations() {
        System.out.println("\n\n✏️ === TEST UPDATE OPERATIONS ===\n");

        // 1. Test UPDATE PASSWORD
        System.out.println("1. 🔐 Test updatePassword...");
        String newPasswordHash = "new_hashed_password_123";
        utilisateurRepo.updatePassword(utilisateurTest1.getIdUser(), newPasswordHash);
        System.out.println("✅ Mot de passe mis à jour");

        // Note: On ne peut pas vérifier directement le hash dans la base
        // mais on peut vérifier que l'utilisateur existe toujours
        Utilisateur userAfterUpdate = utilisateurRepo.findById(utilisateurTest1.getIdUser());
        assertNotNull(userAfterUpdate, "L'utilisateur doit toujours exister après mise à jour du mot de passe");
    }

    private static void testBulkOperations() {
        System.out.println("\n\n⚡ === TEST BULK OPERATIONS ===\n");

        // Créer quelques utilisateurs supplémentaires pour les tests bulk
        List<Utilisateur> extraUsers = createExtraUsersForBulkTest();

        // 1. Test ACTIVATE USERS
        System.out.println("1. ✅ Test activateUsers...");
        List<Long> userIdsToActivate = Arrays.asList(
                extraUsers.get(0).getIdUser(),
                extraUsers.get(1).getIdUser()
        );
        utilisateurRepo.activateUsers(userIdsToActivate);

        // Vérifier qu'ils sont actifs
        Utilisateur user1 = utilisateurRepo.findById(extraUsers.get(0).getIdUser());
        Utilisateur user2 = utilisateurRepo.findById(extraUsers.get(1).getIdUser());
        assertTrue(user1.getActif() && user2.getActif(), "Les utilisateurs doivent être actifs");
        System.out.println("✅ " + userIdsToActivate.size() + " utilisateurs activés");

        // 2. Test DEACTIVATE USERS
        System.out.println("\n2. ❌ Test deactivateUsers...");
        utilisateurRepo.deactivateUsers(userIdsToActivate);

        // Vérifier qu'ils sont inactifs
        user1 = utilisateurRepo.findById(extraUsers.get(0).getIdUser());
        user2 = utilisateurRepo.findById(extraUsers.get(1).getIdUser());
        assertTrue(!user1.getActif() && !user2.getActif(), "Les utilisateurs doivent être inactifs");
        System.out.println("✅ " + userIdsToActivate.size() + " utilisateurs désactivés");



        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("/".repeat(50));
        // Nettoyer les utilisateurs supplémentaires
        for (Utilisateur user : extraUsers) {
            utilisateurRepo.deleteById(user.getIdUser());
        }
        System.out.println("✅ Utilisateurs supplémentaires nettoyés");
    }

    private static List<Utilisateur> createExtraUsersForBulkTest() {
        List<Utilisateur> extraUsers = Arrays.asList(
                Utilisateur.builder()
                        .nom("Bulk1")
                        .prenom("Test")
                        .email("bulk1@test.com")
                        .login("bulk1")
                        .motDePass("hash")
                        .sexe(Sexe.HOMME)
                        .actif(false) // Inactif au départ
                        .creePar("test_system")
                        .modifiePar("test_system")
                        .build(),
                Utilisateur.builder()
                        .nom("Bulk2")
                        .prenom("Test")
                        .email("bulk2@test.com")
                        .login("bulk2")
                        .motDePass("hash")
                        .sexe(Sexe.FEMME)
                        .actif(false) // Inactif au départ
                        .creePar("test_system")
                .modifiePar("test_system")
                        .build()
        );

        for (Utilisateur user : extraUsers) {
            utilisateurRepo.create(user);
        }

        return extraUsers;
    }

    private static void cleanupTestData() {
        System.out.println("\n\n🧹 === NETTOYAGE DES DONNÉES DE TEST ===\n");

        // 1. Retirer les rôles
        System.out.println("1. Retrait des rôles...");
        roleRepo.removeAllRolesFromUser(utilisateurTest1.getIdUser());
        roleRepo.removeAllRolesFromUser(utilisateurTest2.getIdUser());
        roleRepo.removeAllRolesFromUser(utilisateurTest3.getIdUser());
        System.out.println("✅ Rôles retirés");

        // 2. Supprimer les utilisateurs
        System.out.println("\n2. Suppression des utilisateurs...");
        utilisateurRepo.deleteById(utilisateurTest1.getIdUser());
        utilisateurRepo.deleteById(utilisateurTest2.getIdUser());
        utilisateurRepo.deleteById(utilisateurTest3.getIdUser());
        System.out.println("✅ Utilisateurs supprimés");

        // 3. Supprimer les rôles
        System.out.println("\n3. Suppression des rôles...");
        roleRepo.deleteById(roleMedecin.getIdRole());
        roleRepo.deleteById(roleSecretaire.getIdRole());
        roleRepo.deleteById(roleAdmin.getIdRole());
        System.out.println("✅ Rôles supprimés");
    }

    // Méthodes d'assertion
    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format("❌ Expected: %s, Actual: %s", expected, actual));
        }
    }
}

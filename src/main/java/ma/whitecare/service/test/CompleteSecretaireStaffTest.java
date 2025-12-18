package ma.whitecare.service.test;

import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.entities.user.Staff;
import ma.whitecare.mvc.dto.UserDto.*;
import ma.whitecare.repository.modules.UserManager.api.RoleRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.repository.modules.UserManager.impl.*;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.whitecare.service.modules.UserManager.impl.SecretaireServiceImpl;
import ma.whitecare.service.modules.UserManager.impl.StaffServiceImpl;
import ma.whitecare.service.modules.UserManager.impl.UserServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CompleteSecretaireStaffTest {

    private static final Random random = new Random();
    private static Secretaire secretaire1;
    private static Secretaire secretaire2;
    private static StaffServiceImpl staffService;
    private static SecretaireServiceImpl secretaireService;
    private static CabinetMedicaleRepository cabinetRepo;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  TEST COMPLET SECRÉTAIRE & STAFF SERVICES    ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        try {
            // 1. INITIALISATION
            System.out.println("🔧 PHASE 1: INITIALISATION DES SERVICES");
            initializeServices();

            System.out.println("✅ Services initialisés avec succès\n");

            // 2. CRÉATION DES SECRÉTAIRES
            System.out.println("👩‍💼 PHASE 2: CRÉATION DES SECRÉTAIRES DE TEST");
            secretaire1 = createAndTestSecretaire("test", "Fatima", 1);
            secretaire2 = createAndTestSecretaire("testsecr", "Khadija", 2);

            System.out.println("✅ Secrétaires créées avec succès\n");

            // 3. TEST DU SERVICE STAFF
            System.out.println("📋 PHASE 3: TEST COMPLET DU SERVICE STAFF");
            testAllStaffMethods();

            // 4. TEST DU SERVICE SECRÉTAIRE
            System.out.println("\n👩‍💼 PHASE 4: TEST COMPLET DU SERVICE SECRÉTAIRE");
            testAllSecretaireMethods();

            // 5. TESTS D'INTÉGRATION
            System.out.println("\n🔗 PHASE 5: TESTS D'INTÉGRATION");
            testIntegration();

            // 6. NETTOYAGE
            System.out.println("\n🧹 PHASE 6: NETTOYAGE DES DONNÉES DE TEST");
            cleanupTestData();

            System.out.println("\n🎉 ===========================================");
            System.out.println("🎉  TEST COMPLET TERMINÉ AVEC SUCCÈS !");
            System.out.println("🎉 ===========================================");

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR FATALE : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeServices() {
        System.out.println("   Création des repositories...");

        // Créer tous les repositories nécessaires
        StaffRepositoryImpl staffRepo = new StaffRepositoryImpl();
        SecretaireRepositoryImpl secretaireRepo = new SecretaireRepositoryImpl();
        UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();
        RoleRepository roleRepo = new RoleRepositoryImpl();
        cabinetRepo = new CabinetMedicaleRepositoryImpl();

        System.out.println("   Création du UserService...");
        UserServiceImpl userService = new UserServiceImpl(userRepo, roleRepo);

        System.out.println("   Création du StaffService...");
        staffService = new StaffServiceImpl(staffRepo, userService, cabinetRepo);

        System.out.println("   Création du SecretaireService...");
        secretaireService = new SecretaireServiceImpl(secretaireRepo, userService, cabinetRepo);
    }

    private static Secretaire createAndTestSecretaire(String nom, String prenom, int numero) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(9);
            String uniqueId = timestamp + numero;

            CreateSecretaireDTO dto = CreateSecretaireDTO.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .login(prenom.toLowerCase() + "1" + nom.toLowerCase() + uniqueId)
                    .cin("EE" + uniqueId + "000")
                    .email(prenom.toLowerCase() + "." + nom.toLowerCase() + uniqueId + "@whitecare.com")
                    .telephone("06" + (10000000 + random.nextInt(90000000)))
                    .adresse(numero + " Rue " + nom + ", Casablanca")
                    .dateNaissance(LocalDate.of(1990 + random.nextInt(10), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                    .sexe(Sexe.FEMME)
                    .actif(true)
                    .password("Password123!")
                    .salaire(5000.0 + random.nextInt(5000))
                    .prime(500.0 + random.nextInt(500))
                    .dateRecrutement(LocalDate.now().minusMonths(random.nextInt(24)))
                    .soldeConge(20 + random.nextInt(15))
                    .numCNSS("J" + uniqueId + "000000")
                    .commission(300.0 + random.nextInt(700))
                    .cabinetMedicaleId(1L)
                    .roles(Arrays.asList(LibelleRole.SECRETAIRE))
                    .build();

            System.out.println("   Création de " + nom + " " + prenom + "...");
            Secretaire secretaire = secretaireService.createSecretaire(dto);
            System.out.println("   ✅ Créée avec ID: " + secretaire.getIdUser());
            System.out.println("      Login: " + secretaire.getLogin());
            System.out.println("      Email: " + secretaire.getEmail());
            System.out.println("      CNSS: " + secretaire.getNumCNSS());
            System.out.println("      Salaire: " + secretaire.getSalaire() + " MAD");

            return secretaire;

        } catch (Exception e) {
            System.out.println("   ❌ Erreur création " + nom + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void testAllStaffMethods() {
        System.out.println("\n📌 1. getStaffById()");
        try {
            Staff staff = staffService.getStaffById(secretaire1.getIdUser());
            System.out.println("   ✅ Staff trouvé: " + staff.getNom() + " " + staff.getPrenom());
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 2. getAllStaff()");
        try {
            List<Staff> allStaff = staffService.getAllStaff();
            System.out.println("   ✅ " + allStaff.size() + " staff trouvés");
            for (Staff s : allStaff) {
                String type = s instanceof Secretaire ? "Secrétaire" : "Autre";
                System.out.println("      - " + s.getNom() + " " + s.getPrenom() + " (" + type + ")");
            }
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 3. getStaffByCabinet()");
        try {
            List<Staff> cabinetStaff = staffService.getStaffByCabinet(1L);
            System.out.println("   ✅ " + cabinetStaff.size() + " staff dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 4. getActiveStaffByCabinet()");
        try {
            List<Staff> activeStaff = staffService.getActiveStaffByCabinet(1L);
            System.out.println("   ✅ " + activeStaff.size() + " staff actifs dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 5. assignStaffToCabinet()");
        try {
            staffService.assignStaffToCabinet(secretaire1.getIdUser(), 1L);
            System.out.println("   ✅ Secrétaire assignée au cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 6. countStaffByCabinet()");
        try {
            Long count = staffService.countStaffByCabinet(1L);
            System.out.println("   ✅ " + count + " staff dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 7. updateSalaire()");
        try {
            Double ancienSalaire = secretaire1.getSalaire();
            Double nouveauSalaire = ancienSalaire + 1000.0;
            staffService.updateSalaire(secretaire1.getIdUser(), nouveauSalaire);
            System.out.println("   ✅ Salaire mis à jour:");
            System.out.println("      Ancien: " + ancienSalaire + " MAD");
            System.out.println("      Nouveau: " + nouveauSalaire + " MAD");
        } catch (Exception e) {
            System.out.println("   ❌ " +e.getMessage());
        }

        System.out.println("\n📌 8. updatePrime()");
        try {
            Double anciennePrime = secretaire1.getPrime();
            Double nouvellePrime = (anciennePrime != null ? anciennePrime : 0.0) + 200.0;
            staffService.updatePrime(secretaire1.getIdUser(), nouvellePrime);
            System.out.println("   ✅ Prime mise à jour: " + nouvellePrime + " MAD");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 9. updateSoldeConge() et getSoldeConge()");
        try {
            Integer ancienSolde = staffService.getSoldeConge(secretaire1.getIdUser());
            Integer nouveauSolde = (ancienSolde != null ? ancienSolde : 0) + 5;
            staffService.updateSoldeConge(secretaire1.getIdUser(), nouveauSolde);
            Integer verifSolde = staffService.getSoldeConge(secretaire1.getIdUser());
            System.out.println("   ✅ Solde congé mis à jour:");
            System.out.println("      Ancien: " + ancienSolde + " jours");
            System.out.println("      Nouveau: " + verifSolde + " jours");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 10. updateStaff()");
        try {
            UpdateSecretaireDTO updateDTO = UpdateSecretaireDTO.builder()
                    .nom(secretaire1.getNom())
                    .prenom(secretaire1.getPrenom())
                    .adresse("Nouvelle adresse mise à jour, Casablanca")
                    .telephone("06" + (20000000 + random.nextInt(80000000)))
                    .salaire(secretaire1.getSalaire() + 500.0)
                    .prime(secretaire1.getPrime() + 100.0)
                    .commission(secretaire1.getCommission() + 50.0)
                    .actif(true)
                    .build();

            Staff updated = staffService.updateStaff(secretaire1.getIdUser(), updateDTO);
            System.out.println("   ✅ Staff mis à jour:");
            System.out.println("      Nouvelle adresse: " + updated.getAdresse());
            System.out.println("      Nouveau téléphone: " + updated.getTel());
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 11. getStaffStatisticsByCabinet()");
        try {
            StaffStatisticsDTO stats = staffService.getStaffStatisticsByCabinet(1L);
            System.out.println("   ✅ Statistiques générées:");
            System.out.println("      Total staff: " + stats.getTotalStaff());
            System.out.println("      Médecins: " + stats.getTotalMedecins());
            System.out.println("      Secrétaires: " + stats.getTotalSecretaires());
            System.out.println("      Actifs: " + stats.getTotalActifs());
            System.out.println("      Inactifs: " + stats.getTotalInactifs());
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 12. findStaffByCabinetWithPagination()");
        try {
            List<Staff> page = staffService.findStaffByCabinetWithPagination(1L, 0, 10);
            System.out.println("   ✅ Pagination: " + page.size() + " éléments page 0");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 13. convertToDTO() et convertToDTOList()");
        try {
            StaffDTO dto = staffService.convertToDTO(secretaire1);
            System.out.println("   ✅ DTO individuel créé:");
            System.out.println("      Type: " + dto.getTypeStaff());
            System.out.println("      Email: " + dto.getEmail());
            System.out.println("      Rôles: " + dto.getRoles());

            List<Staff> allStaff = staffService.getAllStaff();
            List<StaffDTO> dtoList = staffService.convertToDTOList(allStaff);
            System.out.println("   ✅ Liste de " + dtoList.size() + " DTOs créée");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }
    }

    private static void testAllSecretaireMethods() {
        System.out.println("\n📌 1. getSecretaireById()");
        try {
            Secretaire sec = secretaireService.getSecretaireById(secretaire1.getIdUser());
            System.out.println("   ✅ Secrétaire trouvée: " + sec.getNom() + " " + sec.getPrenom());
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 2. getAllSecretaires()");
        try {
            List<Secretaire> allSecretaires = secretaireService.getAllSecretaires();
            System.out.println("   ✅ " + allSecretaires.size() + " secrétaires trouvées");
            for (Secretaire s : allSecretaires) {
                System.out.println("      - " + s.getNom() + " " + s.getPrenom() +
                        " (CNSS: " + s.getNumCNSS() + ")");
            }
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 3. updateSecretaire()");
        try {
            UpdateSecretaireDTO updateDTO = UpdateSecretaireDTO.builder()
                    .nom("Modifié")
                    .prenom("Test")
                    .commission(secretaire2.getCommission() + 150.0)
                    .numCNSS("CNSS_MODIF_" + System.currentTimeMillis())
                    .actif(true)
                    .build();

            Secretaire updated = secretaireService.updateSecretaire(secretaire2.getIdUser(), updateDTO);
            System.out.println("   ✅ Secrétaire mise à jour:");
            System.out.println("      Nouveau nom: " + updated.getNom());
            System.out.println("      Nouvelle commission: " + updated.getCommission() + " MAD");
            System.out.println("      Nouveau CNSS: " + updated.getNumCNSS());
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 4. getSecretairesByCabinet()");
        try {
            List<Secretaire> cabinetSecretaires = secretaireService.getSecretairesByCabinet(1L);
            System.out.println("   ✅ " + cabinetSecretaires.size() + " secrétaires dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 5. countSecretairesByCabinet()");
        try {
            Long count = secretaireService.countSecretairesByCabinet(1L);
            System.out.println("   ✅ " + count + " secrétaires dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 6. assignSecretaireToCabinet()");
        try {
            secretaireService.assignSecretaireToCabinet(secretaire2.getIdUser(), 1L);
            System.out.println("   ✅ Secrétaire assignée au cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 7. removeSecretaireFromCabinet()");
        try {
            secretaireService.removeSecretaireFromCabinet(secretaire2.getIdUser());
            System.out.println("   ✅ Secrétaire retirée du cabinet");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 8. updateCommission()");
        try {
            Double ancienneCommission = secretaire1.getCommission();
            Double nouvelleCommission = ancienneCommission + 75.0;
            secretaireService.updateCommission(secretaire1.getIdUser(), nouvelleCommission);
            System.out.println("   ✅ Commission mise à jour:");
            System.out.println("      Ancienne: " + ancienneCommission + " MAD");
            System.out.println("      Nouvelle: " + nouvelleCommission + " MAD");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 9. updateStatutDisponibilite()");
        try {
            secretaireService.updateStatutDisponibilite(secretaire1.getIdUser(), false);
            System.out.println("   ✅ Statut de disponibilité mis à jour: Non disponible");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 10. findSecretairesDisponiblesByCabinet()");
        try {
            List<Secretaire> disponibles = secretaireService.findSecretairesDisponiblesByCabinet(1L);
            System.out.println("   ✅ " + disponibles.size() + " secrétaires disponibles dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 11. findByNumeroCNSS()");
        try {
            String numCNSS = secretaire1.getNumCNSS();
            List<Secretaire> resultats = secretaireService.findByNumeroCNSS(numCNSS);
            System.out.println("   ✅ " + resultats.size() + " secrétaire(s) avec CNSS: " + numCNSS);
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 12. findByNomPrenom()");
        try {
            List<Secretaire> resultats = secretaireService.findByNomPrenom(
                    secretaire1.getNom(),
                    secretaire1.getPrenom()
            );
            System.out.println("   ✅ " + resultats.size() + " secrétaire(s) trouvée(s)");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 13. findSecretairesWithPagination()");
        try {
            List<Secretaire> page = secretaireService.findSecretairesWithPagination(0, 5);
            System.out.println("   ✅ Pagination: " + page.size() + " secrétaires page 0");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 14. getSecretaireStatisticsByCabinet()");
        try {
            SecretaireStatisticsDTO stats = secretaireService.getSecretaireStatisticsByCabinet(1L);
            System.out.println("   ✅ Statistiques secrétaires:");
            System.out.println("      Total: " + stats.getTotalSecretaires());
            System.out.println("      Actives: " + stats.getTotalActifs());
            System.out.println("      Inactives: " + stats.getTotalInactifs());
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 15. convertToDTO() et convertToDTOList()");
        try {
            SecretaireDTO dto = secretaireService.convertToDTO(secretaire1);
            System.out.println("   ✅ DTO secrétaire créé:");
            System.out.println("      CNSS: " + dto.getNumCNSS());
            System.out.println("      Commission: " + dto.getCommission() + " MAD");
            System.out.println("      Type: " + dto.getTypeStaff());

            List<Secretaire> allSecretaires = secretaireService.getAllSecretaires();
            List<SecretaireDTO> dtoList = secretaireService.convertToDTOList(allSecretaires);
            System.out.println("   ✅ Liste de " + dtoList.size() + " DTOs secrétaires créée");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }
    }

    private static void testIntegration() {
        System.out.println("\n📌 1. Cohérence des données");
        try {
            List<Staff> allStaff = staffService.getAllStaff();
            List<Secretaire> allSecretaires = secretaireService.getAllSecretaires();

            long countSecretairesInStaff = allStaff.stream()
                    .filter(s -> s instanceof Secretaire)
                    .count();

            System.out.println("   ✅ Vérification cohérence:");
            System.out.println("      Total staff: " + allStaff.size());
            System.out.println("      Secrétaires (via Staff): " + countSecretairesInStaff);
            System.out.println("      Secrétaires (via SecretaireService): " + allSecretaires.size());

            if (countSecretairesInStaff == allSecretaires.size()) {
                System.out.println("      ✓ Les données sont cohérentes");
            } else {
                System.out.println("      ✗ Incohérence détectée!");
            }
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 2. Test de performance avec pagination");
        try {
            System.out.println("   Test pagination cabinet (staff):");
            for (int i = 0; i < 2; i++) {
                List<Staff> page = staffService.findStaffByCabinetWithPagination(1L, i, 2);
                System.out.println("      Page " + i + ": " + page.size() + " éléments");
            }

            System.out.println("   Test pagination (secrétaires):");
            for (int i = 0; i < 2; i++) {
                List<Secretaire> page = secretaireService.findSecretairesWithPagination(i, 2);
                System.out.println("      Page " + i + ": " + page.size() + " secrétaires");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 3. Test de conversion multiple");
        try {
            List<Staff> allStaff = staffService.getAllStaff();
            List<StaffDTO> staffDTOs = staffService.convertToDTOList(allStaff);

            List<Secretaire> allSecretaires = secretaireService.getAllSecretaires();
            List<SecretaireDTO> secretaireDTOs = secretaireService.convertToDTOList(allSecretaires);

            System.out.println("   ✅ Conversion réussie:");
            System.out.println("      Staff DTOs: " + staffDTOs.size());
            System.out.println("      Secrétaire DTOs: " + secretaireDTOs.size());

            // Afficher quelques DTOs
            if (!staffDTOs.isEmpty()) {
                StaffDTO firstDTO = staffDTOs.get(0);
                System.out.println("      Premier DTO: " + firstDTO.getNom() +
                        " - Type: " + firstDTO.getTypeStaff());
            }
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }
    }

    private static void cleanupTestData() {
        try {
            System.out.println("   Suppression des données de test...");

            if (secretaire2 != null) {
                try {
                    secretaireService.deleteSecretaire(secretaire2.getIdUser());
                    System.out.println("   ✅ Secrétaire 2 supprimée: " + secretaire2.getNom());
                } catch (Exception e) {
                    System.out.println("   ⚠️  Erreur suppression secrétaire 2: " + e.getMessage());
                }
            }

            if (secretaire1 != null) {
                try {
                    secretaireService.deleteSecretaire(secretaire1.getIdUser());
                    System.out.println("   ✅ Secrétaire 1 supprimée: " + secretaire1.getNom());
                } catch (Exception e) {
                    System.out.println("   ⚠️  Erreur suppression secrétaire 1: " + e.getMessage());
                }
            }

            // Vérification finale
            List<Secretaire> remaining = secretaireService.getAllSecretaires();
            System.out.println("   ✅ Secrétaires restantes en base: " + remaining.size());

        } catch (Exception e) {
            System.out.println("   ⚠️  Erreur nettoyage: " + e.getMessage());
        }
    }
}
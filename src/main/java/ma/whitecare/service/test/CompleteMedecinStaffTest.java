package ma.whitecare.service.test;




import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.user.Staff;
import ma.whitecare.mvc.dto.UserDto.*;
import ma.whitecare.repository.modules.UserManager.api.RoleRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.repository.modules.UserManager.impl.*;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.whitecare.service.modules.UserManager.impl.MedecinServiceImpl;
import ma.whitecare.service.modules.UserManager.impl.StaffServiceImpl;
import ma.whitecare.service.modules.UserManager.impl.UserServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CompleteMedecinStaffTest {

    private static final Random random = new Random();
    private static Medecin medecin1;
    private static Medecin medecin2;
    private static StaffServiceImpl staffService;
    private static MedecinServiceImpl medecinService;
    private static CabinetMedicaleRepository cabinetRepo;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  TEST COMPLET MÉDECIN & STAFF SERVICES       ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        try {
            // 1. INITIALISATION
            System.out.println("🔧 PHASE 1: INITIALISATION DES SERVICES");
            initializeServices();

            System.out.println("✅ Services initialisés avec succès\n");

            // 2. CRÉATION DES MÉDECINS
            System.out.println("👨‍⚕️ PHASE 2: CRÉATION DES MÉDECINS DE TEST");
            medecin1 = createAndTestMedecin("test", "Ahmed", "Cardiologie", 1);
            medecin2 = createAndTestMedecin("hjyuio", "Samira", "Pédiatrie", 2);

            System.out.println("✅ Médecins créés avec succès\n");

            // 3. TEST DU SERVICE STAFF
            System.out.println("📋 PHASE 3: TEST COMPLET DU SERVICE STAFF");
            testAllStaffMethods();

            // 4. TEST DU SERVICE MÉDECIN
            System.out.println("\n👨‍⚕️ PHASE 4: TEST COMPLET DU SERVICE MÉDECIN");
            testAllMedecinMethods();

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
        MedecinRepositoryImpl medecinRepo = new MedecinRepositoryImpl();
        UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();
        RoleRepository roleRepo = new RoleRepositoryImpl();
        cabinetRepo = new CabinetMedicaleRepositoryImpl();

        System.out.println("   Création du UserService...");
        UserServiceImpl userService = new UserServiceImpl(userRepo, roleRepo);

        System.out.println("   Création du StaffService...");
        staffService = new StaffServiceImpl(staffRepo, userService, cabinetRepo);

        System.out.println("   Création du MedecinService...");
        medecinService = new MedecinServiceImpl(medecinRepo, userService, cabinetRepo, staffRepo);
    }

    private static Medecin createAndTestMedecin(String nom, String prenom, String specialite, int numero) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(9);
            String uniqueId = timestamp + numero;

            CreateMedecinDTO dto = CreateMedecinDTO.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .login(prenom.toLowerCase() + "l" + nom.toLowerCase() + uniqueId)
                    .cin("MED" + uniqueId + "000")
                    .email(prenom.toLowerCase() + "-" + nom.toLowerCase() + uniqueId + "@whitecare.com")
                    .telephone("06" + (10000000 + random.nextInt(90000000)))
                    .adresse(numero + " Rue " + nom + ", Casablanca")
                    .dateNaissance(LocalDate.of(1975 + random.nextInt(15), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                    .sexe(random.nextBoolean() ? Sexe.HOMME : Sexe.FEMME)
                    .actif(true)
                    .password("Password123!")
                    .specialite(specialite)
                    .salaire(15000.0 + random.nextInt(20000))
                    .prime(2000.0 + random.nextInt(3000))
                    .dateRecrutement(LocalDate.now().minusMonths(random.nextInt(60)))
                    .soldeConge(25 + random.nextInt(20))
                    .cabinetMedicaleId(1L)
                    .roles(Arrays.asList(LibelleRole.MEDECIN))
                    .build();

            System.out.println("   Création de Dr. " + nom + " " + prenom + "...");
            Medecin medecin = medecinService.createMedecin(dto);
            System.out.println("   ✅ Créé avec ID: " + medecin.getIdUser());
            System.out.println("      Login: " + medecin.getLogin());
            System.out.println("      Email: " + medecin.getEmail());
            System.out.println("      Spécialité: " + medecin.getSpecialite());
            System.out.println("      Salaire: " + medecin.getSalaire() + " MAD");

            return medecin;

        } catch (Exception e) {
            System.out.println("   ❌ Erreur création " + nom + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void testAllStaffMethods() {
        System.out.println("\n📌 1. getStaffById()");
        try {
            Staff staff = staffService.getStaffById(medecin1.getIdUser());
            System.out.println("   ✅ Staff trouvé: " + staff.getNom() + " " + staff.getPrenom());
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 2. getAllStaff()");
        try {
            List<Staff> allStaff = staffService.getAllStaff();
            System.out.println("   ✅ " + allStaff.size() + " staff trouvés");
            for (Staff s : allStaff) {
                String type = s instanceof Medecin ? "Médecin" : "Autre";
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
            staffService.assignStaffToCabinet(medecin1.getIdUser(), 1L);
            System.out.println("   ✅ Médecin assigné au cabinet 1");
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
            Double ancienSalaire = medecin1.getSalaire();
            Double nouveauSalaire = ancienSalaire + 2000.0;
            staffService.updateSalaire(medecin1.getIdUser(), nouveauSalaire);
            System.out.println("   ✅ Salaire mis à jour:");
            System.out.println("      Ancien: " + ancienSalaire + " MAD");
            System.out.println("      Nouveau: " + nouveauSalaire + " MAD");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 8. updatePrime()");
        try {
            Double anciennePrime = medecin1.getPrime();
            Double nouvellePrime = (anciennePrime != null ? anciennePrime : 0.0) + 500.0;
            staffService.updatePrime(medecin1.getIdUser(), nouvellePrime);
            System.out.println("   ✅ Prime mise à jour: " + nouvellePrime + " MAD");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 9. updateSoldeConge() et getSoldeConge()");
        try {
            Integer ancienSolde = staffService.getSoldeConge(medecin1.getIdUser());
            Integer nouveauSolde = (ancienSolde != null ? ancienSolde : 0) + 10;
            staffService.updateSoldeConge(medecin1.getIdUser(), nouveauSolde);
            Integer verifSolde = staffService.getSoldeConge(medecin1.getIdUser());
            System.out.println("   ✅ Solde congé mis à jour:");
            System.out.println("      Ancien: " + ancienSolde + " jours");
            System.out.println("      Nouveau: " + verifSolde + " jours");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 10. updateStaff()");
        try {
            UpdateMedecinDTO updateDTO = UpdateMedecinDTO.builder()
                    .nom(medecin1.getNom())
                    .prenom(medecin1.getPrenom())
                    .adresse("Nouvelle adresse clinique, Rabat")
                    .telephone("06" + (20000000 + random.nextInt(80000000)))
                    .salaire(medecin1.getSalaire() + 1000.0)
                    .prime(medecin1.getPrime() + 200.0)
                    .specialite("Cardiologie Interventionnelle")
                    .actif(true)
                    .build();

            Staff updated = staffService.updateStaff(medecin1.getIdUser(), updateDTO);
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
            StaffDTO dto = staffService.convertToDTO(medecin1);
            System.out.println("   ✅ DTO individuel créé:");
            System.out.println("      Type: " + dto.getTypeStaff());
            System.out.println("      Email: " + dto.getEmail());
            System.out.println("      Spécialité: " + dto.getSpecialite());
            System.out.println("      Rôles: " + dto.getRoles());

            List<Staff> allStaff = staffService.getAllStaff();
            List<StaffDTO> dtoList = staffService.convertToDTOList(allStaff);
            System.out.println("   ✅ Liste de " + dtoList.size() + " DTOs créée");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }
    }

    private static void testAllMedecinMethods() {
        System.out.println("\n📌 1. getMedecinById()");
        try {
            Medecin med = medecinService.getMedecinById(medecin1.getIdUser());
            System.out.println("   ✅ Médecin trouvé: Dr. " + med.getNom() + " " + med.getPrenom());
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 2. getAllMedecins()");
        try {
            List<Medecin> allMedecins = medecinService.getAllMedecins();
            System.out.println("   ✅ " + allMedecins.size() + " médecins trouvés");
            for (Medecin m : allMedecins) {
                System.out.println("      - Dr. " + m.getNom() + " " + m.getPrenom() +
                        " (" + m.getSpecialite() + ")");
            }
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 3. updateMedecin()");
        try {
            UpdateMedecinDTO updateDTO = UpdateMedecinDTO.builder()
                    .nom(medecin2.getNom())
                    .prenom(medecin2.getPrenom())
                    .specialite("Pédiatrie Néonatale")
                    .salaire(medecin2.getSalaire() + 3000.0)
                    .actif(true)
                    .build();

            Medecin updated = medecinService.updateMedecin(medecin2.getIdUser(), updateDTO);
            System.out.println("   ✅ Médecin mis à jour:");
            System.out.println("      Nouvelle spécialité: " + updated.getSpecialite());
            System.out.println("      Nouveau salaire: " + updated.getSalaire() + " MAD");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 4. getMedecinsByCabinet()");
        try {
            List<Medecin> cabinetMedecins = medecinService.getMedecinsByCabinet(1L);
            System.out.println("   ✅ " + cabinetMedecins.size() + " médecins dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 5. countMedecinsByCabinet()");
        try {
            Long count = medecinService.countMedecinsByCabinet(1L);
            System.out.println("   ✅ " + count + " médecins dans le cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 6. assignMedecinToCabinet()");
        try {
            medecinService.assignMedecinToCabinet(medecin2.getIdUser(), 1L);
            System.out.println("   ✅ Médecin assigné au cabinet 1");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 7. removeMedecinFromCabinet()");
        try {
            medecinService.removeMedecinFromCabinet(medecin2.getIdUser());
            System.out.println("   ✅ Médecin retiré du cabinet");
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 8. updateSpecialite()");
        try {
            String ancienneSpecialite = medecin1.getSpecialite();
            String nouvelleSpecialite = "Cardiologie Avancée";
            medecinService.updateSpecialite(medecin1.getIdUser(), nouvelleSpecialite);
            System.out.println("   ✅ Spécialité mise à jour:");
            System.out.println("      Ancienne: " + ancienneSpecialite);
            System.out.println("      Nouvelle: " + nouvelleSpecialite);
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 9. findMedecinsBySpecialite()");
        try {
            String specialite = medecin1.getSpecialite();
            List<Medecin> resultats = medecinService.findMedecinsBySpecialite(specialite);
            System.out.println("   ✅ " + resultats.size() + " médecin(s) en " + specialite);
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 10. findMedecinsByNomPrenom()");
        try {
            List<Medecin> resultats = medecinService.findMedecinsByNomPrenom(
                    medecin1.getNom(),
                    medecin1.getPrenom()
            );
            System.out.println("   ✅ " + resultats.size() + " médecin(s) trouvé(s)");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }



        System.out.println("\n📌 12. findMedecinsWithPagination()");
        try {
            List<Medecin> page = medecinService.findMedecinsWithPagination(0, 5);
            System.out.println("   ✅ Pagination: " + page.size() + " médecins page 0");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 13. getMedecinStatisticsByCabinet()");
        try {
            MedecinStatisticsDTO stats = medecinService.getMedecinStatisticsByCabinet(1L);
            System.out.println("   ✅ Statistiques médecins:");
            System.out.println("      Total: " + stats.getTotalMedecins());
            System.out.println("      Actifs: " + stats.getTotalActifs());
            System.out.println("      Inactifs: " + stats.getTotalInactifs());
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 14. convertToDTO() et convertToDTOList()");
        try {
            MedecinDTO dto = medecinService.convertToDTO(medecin1);
            System.out.println("   ✅ DTO médecin créé:");
            System.out.println("      Spécialité: " + dto.getSpecialite());
            System.out.println("      Salaire: " + dto.getSalaire() + " MAD");
            System.out.println("      Type: " + dto.getTypeStaff());

            List<Medecin> allMedecins = medecinService.getAllMedecins();
            List<MedecinDTO> dtoList = medecinService.convertToDTOList(allMedecins);
            System.out.println("   ✅ Liste de " + dtoList.size() + " DTOs médecins créée");
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }
    }

    private static void testIntegration() {
        System.out.println("\n📌 1. Cohérence des données");
        try {
            List<Staff> allStaff = staffService.getAllStaff();
            List<Medecin> allMedecins = medecinService.getAllMedecins();

            long countMedecinsInStaff = allStaff.stream()
                    .filter(s -> s instanceof Medecin)
                    .count();

            System.out.println("   ✅ Vérification cohérence:");
            System.out.println("      Total staff: " + allStaff.size());
            System.out.println("      Médecins (via Staff): " + countMedecinsInStaff);
            System.out.println("      Médecins (via MedecinService): " + allMedecins.size());

            if (countMedecinsInStaff == allMedecins.size()) {
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

            System.out.println("   Test pagination (médecins):");
            for (int i = 0; i < 2; i++) {
                List<Medecin> page = medecinService.findMedecinsWithPagination(i, 2);
                System.out.println("      Page " + i + ": " + page.size() + " médecins");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }

        System.out.println("\n📌 3. Test de conversion multiple");
        try {
            List<Staff> allStaff = staffService.getAllStaff();
            List<StaffDTO> staffDTOs = staffService.convertToDTOList(allStaff);

            List<Medecin> allMedecins = medecinService.getAllMedecins();
            List<MedecinDTO> medecinDTOs = medecinService.convertToDTOList(allMedecins);

            System.out.println("   ✅ Conversion réussie:");
            System.out.println("      Staff DTOs: " + staffDTOs.size());
            System.out.println("      Médecin DTOs: " + medecinDTOs.size());

            // Afficher quelques DTOs
            if (!staffDTOs.isEmpty()) {
                StaffDTO firstDTO = staffDTOs.get(0);
                System.out.println("      Premier DTO: " + firstDTO.getNom() +
                        " - Type: " + firstDTO.getTypeStaff());
            }
        } catch (Exception e) {
            System.out.println("   ❌ " + e.getMessage());
        }

        System.out.println("\n📌 4. Test des statistiques combinées");
        try {
            StaffStatisticsDTO staffStats = staffService.getStaffStatisticsByCabinet(1L);
            MedecinStatisticsDTO medecinStats = medecinService.getMedecinStatisticsByCabinet(1L);

            System.out.println("   ✅ Comparaison statistiques:");
            System.out.println("      Médecins (StaffStats): " + staffStats.getTotalMedecins());
            System.out.println("      Médecins (MedecinStats): " + medecinStats.getTotalMedecins());

            if (staffStats.getTotalMedecins().equals(medecinStats.getTotalMedecins())) {
                System.out.println("      ✓ Statistiques cohérentes");
            } else {
                System.out.println("      ✗ Incohérence dans les statistiques!");
            }
        } catch (Exception e) {
            System.out.println("   ⚠️  " + e.getMessage());
        }
    }

    private static void cleanupTestData() {
        try {
            System.out.println("   Suppression des données de test...");

            if (medecin2 != null) {
                try {
                    medecinService.deleteMedecin(medecin2.getIdUser());
                    System.out.println("   ✅ Médecin 2 supprimé: Dr. " + medecin2.getNom());
                } catch (Exception e) {
                    System.out.println("   ⚠️  Erreur suppression médecin 2: " + e.getMessage());
                }
            }

            if (medecin1 != null) {
                try {
                    medecinService.deleteMedecin(medecin1.getIdUser());
                    System.out.println("   ✅ Médecin 1 supprimé: Dr. " + medecin1.getNom());
                } catch (Exception e) {
                    System.out.println("   ⚠️  Erreur suppression médecin 1: " + e.getMessage());
                }
            }

            // Vérification finale
            List<Medecin> remaining = medecinService.getAllMedecins();
            System.out.println("   ✅ Médecins restants en base: " + remaining.size());

        } catch (Exception e) {
            System.out.println("   ⚠️  Erreur nettoyage: " + e.getMessage());
        }
    }
}
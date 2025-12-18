package ma.whitecare.service.test;

import ma.whitecare.common.exceptions.CabinetNotFoundException;
import ma.whitecare.common.exceptions.ChargesNotFoundException;
import ma.whitecare.common.exceptions.InvalidChargeException;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;
import ma.whitecare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.whitecare.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.whitecare.service.modules.caisse.api.ChargesService;
import ma.whitecare.service.modules.caisse.impl.ChargesServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

public class ChargesServiceTest {

    private static ChargesService chargesService;
    private static ChargesRepository chargesRepository;
    private static CabinetMedicaleRepository cabinetRepository;
    private static final Long TEST_CABINET_ID = 2L;
    private static Charges testCharge;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     TEST COMPLET DU SERVICE CHARGES                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        try {
            // 1. INITIALISATION
            System.out.println("🔧 PHASE 1: INITIALISATION DES SERVICES");
            initializeServices();
            System.out.println("✅ Services initialisés avec succès\n");

            // 2. TESTS DE VALIDATION
            System.out.println("📋 PHASE 2: TESTS DE VALIDATION");
            testValidation();
            System.out.println("✅ Tests de validation réussis\n");

            // 3. TESTS CRUD
            System.out.println("📝 PHASE 3: TESTS CRUD (CREATE, READ, UPDATE, DELETE)");
            testCRUD();
            System.out.println("✅ Tests CRUD réussis\n");

            // 4. TESTS DE RECHERCHE
            System.out.println("🔍 PHASE 4: TESTS DE RECHERCHE");
            testSearch();
            System.out.println("✅ Tests de recherche réussis\n");

            // 5. TESTS DE CALCUL
            System.out.println("💰 PHASE 5: TESTS DE CALCUL");
            testCalculations();
            System.out.println("✅ Tests de calcul réussis\n");

            // 6. TESTS D'EXCEPTIONS
            System.out.println("⚠️  PHASE 6: TESTS D'EXCEPTIONS");
            testExceptions();
            System.out.println("✅ Tests d'exceptions réussis\n");

            // 7. NETTOYAGE
            System.out.println("🧹 PHASE 7: NETTOYAGE");
            cleanup();
            System.out.println("✅ Nettoyage terminé\n");

            System.out.println("🎉 ===========================================");
            System.out.println("🎉  TOUS LES TESTS ONT RÉUSSI !");
            System.out.println("🎉 ===========================================");

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR FATALE : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeServices() {
        chargesRepository = new ChargesRepositoryImpl();
        cabinetRepository = new CabinetMedicaleRepositoryImpl();
        chargesService = new ChargesServiceImpl(chargesRepository, cabinetRepository);
    }

    private static void testValidation() {
        System.out.println("  → Test validation charge valide...");
        Charges validCharge = Charges.builder()
                .titre("Test Charge")
                .description("Description test")
                .montant(100.0)
                .date(LocalDateTime.now())
                .cabinetMedicaleId(TEST_CABINET_ID)
                .creePar("test_user")
                .modifiePar("test_user")
                .build();
        chargesService.createCharge(validCharge);
        System.out.println("    ✅ Charge valide créée avec ID: " + validCharge.getId());
        testCharge = validCharge;

        System.out.println("  → Test validation titre vide...");
        try {
            Charges invalidCharge = Charges.builder()
                    .titre("")
                    .montant(100.0)
                    .date(LocalDateTime.now())
                    .cabinetMedicaleId(TEST_CABINET_ID)
                    .build();
            chargesService.createCharge(invalidCharge);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidChargeException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test validation montant négatif...");
        try {
            Charges invalidCharge = Charges.builder()
                    .titre("Test")
                    .montant(-100.0)
                    .date(LocalDateTime.now())
                    .cabinetMedicaleId(TEST_CABINET_ID)
                    .build();
            chargesService.createCharge(invalidCharge);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidChargeException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test validation date future...");
        try {
            Charges invalidCharge = Charges.builder()
                    .titre("Test")
                    .montant(100.0)
                    .date(LocalDateTime.now().plusDays(1))
                    .cabinetMedicaleId(TEST_CABINET_ID)
                    .build();
            chargesService.createCharge(invalidCharge);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidChargeException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }
    }

    private static void testCRUD() {
        // CREATE
        System.out.println("  → Test création charge...");
        Charges newCharge = Charges.builder()
                .titre("Achat équipement")
                .description("Achat de nouveaux équipements médicaux")
                .montant(5000.75)
                .date(LocalDateTime.now())
                .cabinetMedicaleId(TEST_CABINET_ID)
                .creePar("test_user")
                .modifiePar("test_user")
                .build();
        chargesService.createCharge(newCharge);
        System.out.println("    ✅ Charge créée avec ID: " + newCharge.getId());

        // READ
        System.out.println("  → Test récupération par ID...");
        Charges found = chargesService.getChargeById(newCharge.getId());
        System.out.println("    ✅ Charge trouvée: " + found.getTitre() + " - " + found.getMontant() + " MAD");

        System.out.println("  → Test récupération toutes les charges...");
        List<Charges> allCharges = chargesService.getAllCharges();
        System.out.println("    ✅ Nombre total de charges: " + allCharges.size());

        // UPDATE
        System.out.println("  → Test mise à jour charge...");
        newCharge.setTitre("Achat équipement urgent");
        newCharge.setMontant(6000.50);
        newCharge.setModifiePar("test_update");
        chargesService.updateCharge(newCharge);
        Charges updated = chargesService.getChargeById(newCharge.getId());
        System.out.println("    ✅ Charge mise à jour: " + updated.getTitre() + " - " + updated.getMontant() + " MAD");

        // DELETE
        System.out.println("  → Test suppression charge...");
        chargesService.deleteCharge(newCharge.getId());
        try {
            chargesService.getChargeById(newCharge.getId());
            System.out.println("    ❌ Erreur: Charge devrait être supprimée");
        } catch (ChargesNotFoundException e) {
            System.out.println("    ✅ Charge supprimée avec succès");
        }
    }

    private static void testSearch() {
        System.out.println("  → Test recherche par cabinet...");
        List<Charges> chargesByCabinet = chargesService.getChargesByCabinet(TEST_CABINET_ID);
        System.out.println("    ✅ " + chargesByCabinet.size() + " charges trouvées pour le cabinet " + TEST_CABINET_ID);

        System.out.println("  → Test recherche par titre...");
        List<Charges> chargesByTitre = chargesService.searchChargesByTitre("Test");
        System.out.println("    ✅ " + chargesByTitre.size() + " charges trouvées avec 'Test'");

        System.out.println("  → Test recherche par description...");
        List<Charges> chargesByDesc = chargesService.searchChargesByDescription("test");
        System.out.println("    ✅ " + chargesByDesc.size() + " charges trouvées avec 'test'");
    }

    private static void testCalculations() {
        System.out.println("  → Test calcul total charges...");
        Double total = chargesService.calculateTotalCharges(TEST_CABINET_ID);
        System.out.println("    ✅ Total charges: " + total + " MAD");

        System.out.println("  → Test calcul total par période...");
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        Double totalPeriod = chargesService.calculateTotalChargesByPeriod(TEST_CABINET_ID, startDate, endDate);
        System.out.println("    ✅ Total période (30 jours): " + totalPeriod + " MAD");

        System.out.println("  → Test calcul total par titre...");
        Double totalByTitre = chargesService.calculateTotalChargesByTitre(TEST_CABINET_ID, "Test Charge");
        System.out.println("    ✅ Total par titre: " + totalByTitre + " MAD");

        System.out.println("  → Test comptage charges...");
        Long count = chargesService.countChargesByCabinet(TEST_CABINET_ID);
        System.out.println("    ✅ Nombre de charges: " + count);

        System.out.println("  → Test comptage par période...");
        Long countPeriod = chargesService.countChargesByPeriod(TEST_CABINET_ID, startDate, endDate);
        System.out.println("    ✅ Nombre de charges (30 jours): " + countPeriod);

        System.out.println("  → Test récupération charges par période...");
        List<Charges> chargesPeriod = chargesService.getChargesByCabinetAndPeriod(TEST_CABINET_ID, startDate, endDate);
        System.out.println("    ✅ " + chargesPeriod.size() + " charges trouvées pour la période");
    }

    private static void testExceptions() {
        System.out.println("  → Test exception charge non trouvée...");
        try {
            chargesService.getChargeById(999999L);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (ChargesNotFoundException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test exception cabinet non trouvé...");
        try {
            Charges invalidCharge = Charges.builder()
                    .titre("Test")
                    .montant(100.0)
                    .date(LocalDateTime.now())
                    .cabinetMedicaleId(999999L)
                    .build();
            chargesService.createCharge(invalidCharge);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (CabinetNotFoundException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test exception ID invalide...");
        try {
            chargesService.getChargeById(-1L);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidChargeException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test exception plage de dates invalide...");
        try {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = LocalDateTime.now().minusDays(1);
            chargesService.calculateTotalChargesByPeriod(TEST_CABINET_ID, start, end);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidChargeException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }
    }

    private static void cleanup() {
        if (testCharge != null && testCharge.getId() != null) {
            try {
                chargesService.deleteCharge(testCharge.getId());
                System.out.println("  ✅ Charge de test supprimée");
            } catch (Exception e) {
                System.out.println("  ⚠️  Erreur lors du nettoyage: " + e.getMessage());
            }
        }
    }
}


package ma.whitecare.service.test;

import ma.whitecare.common.exceptions.CabinetNotFoundException;
import ma.whitecare.common.exceptions.InvalidRevenueException;
import ma.whitecare.common.exceptions.RevenuesNotFoundException;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.api.RevenuesRepository;
import ma.whitecare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.whitecare.repository.modules.cabinet.impl.RevenuesRepositoryImpl;
import ma.whitecare.service.modules.caisse.api.RevenuesService;
import ma.whitecare.service.modules.caisse.impl.RevenuesServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

public class RevenuesServiceTest {

    private static RevenuesService revenuesService;
    private static RevenuesRepository revenuesRepository;
    private static CabinetMedicaleRepository cabinetRepository;
    private static final Long TEST_CABINET_ID = 1L;
    private static Revenues testRevenue;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     TEST COMPLET DU SERVICE REVENUES                     ║");
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
        revenuesRepository = new RevenuesRepositoryImpl();
        cabinetRepository = new CabinetMedicaleRepositoryImpl();
        revenuesService = new RevenuesServiceImpl(revenuesRepository, cabinetRepository);
    }

    private static void testValidation() {
        System.out.println("  → Test validation revenu valide...");
        Revenues validRevenue = Revenues.builder()
                .titre("Test Revenue")
                .description("Description test")
                .montant(200.0)
                .date(LocalDateTime.now())
                .cabinetMedicaleId(TEST_CABINET_ID)
                .creePar("test_user")
                .modifiePar("test_user")
                .build();
        revenuesService.createRevenue(validRevenue);
        System.out.println("    ✅ Revenu valide créé avec ID: " + validRevenue.getId());
        testRevenue = validRevenue;

        System.out.println("  → Test validation titre vide...");
        try {
            Revenues invalidRevenue = Revenues.builder()
                    .titre("")
                    .montant(200.0)
                    .date(LocalDateTime.now())
                    .cabinetMedicaleId(TEST_CABINET_ID)
                    .build();
            revenuesService.createRevenue(invalidRevenue);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidRevenueException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test validation montant négatif...");
        try {
            Revenues invalidRevenue = Revenues.builder()
                    .titre("Test")
                    .montant(-200.0)
                    .date(LocalDateTime.now())
                    .cabinetMedicaleId(TEST_CABINET_ID)
                    .build();
            revenuesService.createRevenue(invalidRevenue);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidRevenueException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test validation date future...");
        try {
            Revenues invalidRevenue = Revenues.builder()
                    .titre("Test")
                    .montant(200.0)
                    .date(LocalDateTime.now().plusDays(1))
                    .cabinetMedicaleId(TEST_CABINET_ID)
                    .build();
            revenuesService.createRevenue(invalidRevenue);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidRevenueException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }
    }

    private static void testCRUD() {
        // CREATE
        System.out.println("  → Test création revenu...");
        Revenues newRevenue = Revenues.builder()
                .titre("Consultation spécialisée")
                .description("Consultation cardiologie")
                .montant(500.0)
                .date(LocalDateTime.now())
                .cabinetMedicaleId(TEST_CABINET_ID)
                .creePar("test_user")
                .modifiePar("test_user")
                .build();
        revenuesService.createRevenue(newRevenue);
        System.out.println("    ✅ Revenu créé avec ID: " + newRevenue.getId());

        // READ
        System.out.println("  → Test récupération par ID...");
        Revenues found = revenuesService.getRevenueById(newRevenue.getId());
        System.out.println("    ✅ Revenu trouvé: " + found.getTitre() + " - " + found.getMontant() + " MAD");

        System.out.println("  → Test récupération tous les revenus...");
        List<Revenues> allRevenues = revenuesService.getAllRevenues();
        System.out.println("    ✅ Nombre total de revenus: " + allRevenues.size());

        // UPDATE
        System.out.println("  → Test mise à jour revenu...");
        newRevenue.setTitre("Consultation spécialisée urgente");
        newRevenue.setMontant(600.0);
        newRevenue.setModifiePar("test_update");
        revenuesService.updateRevenue(newRevenue);
        Revenues updated = revenuesService.getRevenueById(newRevenue.getId());
        System.out.println("    ✅ Revenu mis à jour: " + updated.getTitre() + " - " + updated.getMontant() + " MAD");

        // DELETE
        System.out.println("  → Test suppression revenu...");
        revenuesService.deleteRevenue(newRevenue.getId());
        try {
            revenuesService.getRevenueById(newRevenue.getId());
            System.out.println("    ❌ Erreur: Revenu devrait être supprimé");
        } catch (RevenuesNotFoundException e) {
            System.out.println("    ✅ Revenu supprimé avec succès");
        }
    }

    private static void testSearch() {
        System.out.println("  → Test recherche par cabinet...");
        List<Revenues> revenuesByCabinet = revenuesService.getRevenuesByCabinet(TEST_CABINET_ID);
        System.out.println("    ✅ " + revenuesByCabinet.size() + " revenus trouvés pour le cabinet " + TEST_CABINET_ID);

        System.out.println("  → Test recherche par titre...");
        List<Revenues> revenuesByTitle = revenuesService.searchRevenuesByTitle("Test");
        System.out.println("    ✅ " + revenuesByTitle.size() + " revenus trouvés avec 'Test'");

        System.out.println("  → Test recherche par description...");
        List<Revenues> revenuesByDesc = revenuesService.searchRevenuesByDescription("test");
        System.out.println("    ✅ " + revenuesByDesc.size() + " revenus trouvés avec 'test'");
    }

    private static void testCalculations() {
        System.out.println("  → Test calcul total revenus...");
        Double total = revenuesService.calculateTotalRevenues(TEST_CABINET_ID);
        System.out.println("    ✅ Total revenus: " + total + " MAD");

        System.out.println("  → Test calcul total par période...");
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        Double totalPeriod = revenuesService.calculateTotalRevenuesByPeriod(TEST_CABINET_ID, startDate, endDate);
        System.out.println("    ✅ Total période (30 jours): " + totalPeriod + " MAD");

        System.out.println("  → Test calcul total par titre...");
        Double totalByTitle = revenuesService.calculateTotalRevenuesByTitle(TEST_CABINET_ID, "Test Revenue");
        System.out.println("    ✅ Total par titre: " + totalByTitle + " MAD");

        System.out.println("  → Test calcul moyenne revenus...");
        Double average = revenuesService.calculateAverageRevenue(TEST_CABINET_ID);
        System.out.println("    ✅ Moyenne revenus: " + average + " MAD");

        System.out.println("  → Test comptage revenus...");
        Long count = revenuesService.countRevenuesByCabinet(TEST_CABINET_ID);
        System.out.println("    ✅ Nombre de revenus: " + count);

        System.out.println("  → Test comptage par période...");
        Long countPeriod = revenuesService.countRevenuesByPeriod(TEST_CABINET_ID, startDate, endDate);
        System.out.println("    ✅ Nombre de revenus (30 jours): " + countPeriod);

        System.out.println("  → Test récupération revenus par période...");
        List<Revenues> revenuesPeriod = revenuesService.getRevenuesByCabinetAndPeriod(TEST_CABINET_ID, startDate, endDate);
        System.out.println("    ✅ " + revenuesPeriod.size() + " revenus trouvés pour la période");
    }

    private static void testExceptions() {
        System.out.println("  → Test exception revenu non trouvé...");
        try {
            revenuesService.getRevenueById(999999L);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (RevenuesNotFoundException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test exception cabinet non trouvé...");
        try {
            Revenues invalidRevenue = Revenues.builder()
                    .titre("Test")
                    .montant(200.0)
                    .date(LocalDateTime.now())
                    .cabinetMedicaleId(999999L)
                    .build();
            revenuesService.createRevenue(invalidRevenue);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (CabinetNotFoundException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test exception ID invalide...");
        try {
            revenuesService.getRevenueById(-1L);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidRevenueException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }

        System.out.println("  → Test exception plage de dates invalide...");
        try {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = LocalDateTime.now().minusDays(1);
            revenuesService.calculateTotalRevenuesByPeriod(TEST_CABINET_ID, start, end);
            System.out.println("    ❌ Erreur: Devrait lever une exception");
        } catch (InvalidRevenueException e) {
            System.out.println("    ✅ Exception levée correctement: " + e.getMessage());
        }
    }

    private static void cleanup() {
        if (testRevenue != null && testRevenue.getId() != null) {
            try {
                revenuesService.deleteRevenue(testRevenue.getId());
                System.out.println("  ✅ Revenu de test supprimé");
            } catch (Exception e) {
                System.out.println("  ⚠️  Erreur lors du nettoyage: " + e.getMessage());
            }
        }
    }
}


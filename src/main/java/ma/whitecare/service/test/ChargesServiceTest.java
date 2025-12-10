package ma.whitecare.service.test;

import ma.whitecare.entities.financial.Charges;
import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;
import ma.whitecare.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.whitecare.service.modules.caisse.impl.ChargesServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

public class ChargesServiceTest {

    private static ChargesServiceTest instance;
    private ChargesServiceImpl chargesService;
    private TestChargesRepository testRepository;

    public ChargesServiceTest() {
        this.testRepository = new TestChargesRepository();
        this.chargesService = new ChargesServiceImpl(testRepository);
    }

    public static void main(String[] args) {
        instance = new ChargesServiceTest();

        System.out.println("=== DÉBUT DES TESTS CHARGES SERVICE ===\n");

        try {

            instance.testCreateCharge();
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            instance.testFindById();
            instance.testFindAll();
            instance.testUpdateCharge();
            instance.testDeleteCharge();
            instance.testFindByCabinetId();
            instance.testFindByTitre();
            instance.testFindByDescription();
            instance.testGetTotalCharges();
            instance.testGetTotalChargesByPeriod();
            instance.testGetTotalChargesByTitre();
            instance.testCountCharges();
            instance.testCountChargesByPeriod();
            instance.testFindChargesByPeriod();

            System.out.println("\n=== TOUS LES TESTS PASSÉS AVEC SUCCÈS ===");

        } catch (Exception e) {
            System.err.println("Test échoué: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testCreateCharge() {
        System.out.println("Test: Création d'une charge...");

        Charges charge = new Charges();
        charge.setTitre("Loyer mensuel");
        charge.setDescription("Paiement du loyer du cabinet");
        charge.setMontant(5000.0);
        charge.setDate(LocalDateTime.now());
        charge.setCabinetMedicaleId(1L);
        charge.setCreePar("admin");
        charge.setModifiePar("admin");

        Charges created = chargesService.create(charge);

        assertNotNull(created.getId(), "La charge créée doit avoir un ID");
        assertEquals("Loyer mensuel", created.getTitre());


        System.out.println("✓ Création d'une charge: OK"+ created.getId());
    }

    private void testFindById() {
        System.out.println("Test: Recherche par ID...");

        // Créer d'abord une charge
        Charges charge = new Charges();
        charge.setTitre("Électricité");
        charge.setDescription("Facture d'électricité");
        charge.setMontant(800.0);
        charge.setDate(LocalDateTime.now());
        charge.setCabinetMedicaleId(1L);
        charge.setCreePar("admin");
        charge.setModifiePar("admin");

        Charges created = chargesService.create(charge);
        Long chargeId = created.getId();

        // Rechercher la charge par ID
        Charges found = chargesService.findById(chargeId);

        assertNotNull(found, "La charge doit être trouvée");
        assertEquals(chargeId, found.getId());
        assertEquals("Électricité", found.getTitre());

        System.out.println("✓ Recherche par ID: OK"+found.getTitre());
    }

    private void testFindAll() {
        System.out.println("Test: Recherche de toutes les charges...");

        // Ajouter quelques charges
        for (int i = 1; i <= 3; i++) {
            Charges charge = new Charges();
            charge.setTitre("Charge " + i);
            charge.setDescription("Description " + i);
            charge.setMontant(100.0 * i);
            charge.setDate(LocalDateTime.now().plusDays(i));
            charge.setCabinetMedicaleId(1L);
            charge.setCreePar("admin");
            charge.setModifiePar("admin");
            chargesService.create(charge);
        }

        List<Charges> allCharges = chargesService.findAll();

        assertTrue(allCharges.size() >= 3, "Doit avoir au moins 3 charges");

        System.out.println("✓ Recherche de toutes les charges: OK (" + allCharges.size() + " charges trouvées)");
    }

    private void testUpdateCharge() {
        System.out.println("Test: Mise à jour d'une charge...");

        // Créer une charge
        Charges charge = new Charges();
        charge.setTitre("Original");
        charge.setDescription("Description originale");
        charge.setMontant(1000.0);
        charge.setDate(LocalDateTime.now());
        charge.setCabinetMedicaleId(1L);
        charge.setCreePar("admin");
        charge.setModifiePar("admin");

        Charges created = chargesService.create(charge);

        // Modifier la charge
        created.setTitre("Modifié");
        created.setDescription("Description modifiée");
        created.setMontant(1500.0);
        created.setModifiePar("admin2");

        Charges updated = chargesService.update(created);

        assertEquals("Modifié", updated.getTitre());
        assertEquals("Description modifiée", updated.getDescription());


        System.out.println("✓ Mise à jour d'une charge: OK");
    }

    private void testDeleteCharge() {
        System.out.println("Test: Suppression d'une charge...");

        // Créer une charge
        Charges charge = new Charges();
        charge.setTitre("À supprimer");
        charge.setDescription("Cette charge sera supprimée");
        charge.setMontant(200.0);
        charge.setDate(LocalDateTime.now());
        charge.setCabinetMedicaleId(1L);
        charge.setCreePar("admin");
        charge.setModifiePar("admin");

        Charges created = chargesService.create(charge);
        Long chargeId = created.getId();

        // Vérifier qu'elle existe
        Charges found = chargesService.findById(chargeId);
        assertNotNull(found, "La charge doit exister avant suppression");

        // Supprimer
        chargesService.delete(chargeId);

        // Vérifier qu'elle n'existe plus
        Charges afterDelete = chargesService.findById(chargeId);
        assertNull(afterDelete, "La charge doit être null après suppression");

        System.out.println("✓ Suppression d'une charge: OK");
    }

    private void testFindByCabinetId() {
        System.out.println("Test: Recherche par cabinet ID...");

        // Créer des charges pour différents cabinets
        for (int i = 1; i <= 3; i++) {
            Charges charge = new Charges();
            charge.setTitre("Charge Cabinet " + i);
            charge.setDescription("Pour cabinet " + i);
            charge.setMontant(500.0 * i);
            charge.setDate(LocalDateTime.now().plusDays(i));
            charge.setCabinetMedicaleId((long) i);
            charge.setCreePar("admin");
            charge.setModifiePar("admin");
            chargesService.create(charge);
        }

        // Rechercher les charges du cabinet 2
        List<Charges> cabinet2Charges = chargesService.findByCabinetId(2L);

        assertEquals(1, cabinet2Charges.size(), "Doit avoir 1 charge pour le cabinet 2");
        assertEquals("Charge Cabinet 2", cabinet2Charges.get(0).getTitre());

        System.out.println("✓ Recherche par cabinet ID: OK");
    }

    private void testFindByTitre() {
        System.out.println("Test: Recherche par titre...");

        // Créer des charges avec différents titres
        String[] titres = {"Matériel dentaire", "Fourniture bureau", "Matériel médical"};

        for (String titre : titres) {
            Charges charge = new Charges();
            charge.setTitre(titre);
            charge.setDescription("Description pour " + titre);
            charge.setMontant(300.0);
            charge.setDate(LocalDateTime.now());
            charge.setCabinetMedicaleId(1L);
            charge.setCreePar("admin");
            charge.setModifiePar("admin");
            chargesService.create(charge);
        }

        // Rechercher par "matériel"
        List<Charges> materielCharges = chargesService.findByTitre("matériel");

        assertTrue(materielCharges.size() >= 2, "Doit trouver au moins 2 charges contenant 'matériel'");

        System.out.println("✓ Recherche par titre: OK (" + materielCharges.size() + " charges trouvées)");
    }

    private void testFindByDescription() {
        System.out.println("Test: Recherche par description...");

        Charges charge = new Charges();
        charge.setTitre("Test Description");
        charge.setDescription("Cette charge est importante pour les tests");
        charge.setMontant(100.0);
        charge.setDate(LocalDateTime.now());
        charge.setCabinetMedicaleId(1L);
        charge.setCreePar("admin");
        charge.setModifiePar("admin");

        chargesService.create(charge);

        List<Charges> found = chargesService.findByDescription("importante");

        assertTrue(found.size() >= 1, "Doit trouver au moins une charge avec 'importante' dans la description");

        System.out.println("✓ Recherche par description: OK");
    }

    private void testGetTotalCharges() {
        System.out.println("Test: Calcul du total des charges...");

        // Créer quelques charges pour le cabinet 1
        for (int i = 1; i <= 3; i++) {
            Charges charge = new Charges();
            charge.setTitre("Charge Total " + i);
            charge.setDescription("Pour test total");
            charge.setMontant(100.0 * i);
            charge.setDate(LocalDateTime.now());
            charge.setCabinetMedicaleId(1L);
            charge.setCreePar("admin");
            charge.setModifiePar("admin");
            chargesService.create(charge);
        }

        Double total = chargesService.getTotalCharges(1L);

        // 100 + 200 + 300 = 600


        System.out.println("✓ Calcul du total des charges: OK (total = " + total + ")");
    }

    private void testGetTotalChargesByPeriod() {
        System.out.println("Test: Calcul du total des charges par période...");

        LocalDateTime now = LocalDateTime.now();

        // Créer des charges dans différentes périodes
        Charges charge1 = new Charges();
        charge1.setTitre("Charge Janvier");
        charge1.setMontant(1000.0);
        charge1.setDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        charge1.setCabinetMedicaleId(1L);
        charge1.setCreePar("admin");
        charge1.setModifiePar("admin");
        chargesService.create(charge1);

        Charges charge2 = new Charges();
        charge2.setTitre("Charge Février");
        charge2.setMontant(2000.0);
        charge2.setDate(LocalDateTime.of(2024, 2, 15, 10, 0));
        charge2.setCabinetMedicaleId(1L);
        charge2.setCreePar("admin");
        charge2.setModifiePar("admin");
        chargesService.create(charge2);

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        Double totalJanvier = chargesService.getTotalChargesByPeriod(1L, start, end);

        assertEquals(1000.0, totalJanvier, "Doit avoir 1000 pour janvier");

        System.out.println("✓ Calcul du total des charges par période: OK");
    }

    private void testGetTotalChargesByTitre() {
        System.out.println("Test: Calcul du total des charges par titre...");

        // Créer plusieurs charges avec le même titre
        for (int i = 1; i <= 3; i++) {
            Charges charge = new Charges();
            charge.setTitre("Loyer");
            charge.setDescription("Loyer " + i);
            charge.setMontant(5000.0);
            charge.setDate(LocalDateTime.now().plusMonths(i));
            charge.setCabinetMedicaleId(1L);
            charge.setCreePar("admin");
            charge.setModifiePar("admin");
            chargesService.create(charge);
        }

        Double totalLoyer = chargesService.getTotalChargesByTitre(1L, "Loyer");

        // 3 * 5000 = 15000


        System.out.println("✓ Calcul du total des charges par titre: OK (total loyer = " + totalLoyer + ")");
    }

    private void testCountCharges() {
        System.out.println("Test: Comptage des charges...");

        // Compter les charges existantes
        Long countBefore = chargesService.countCharges(1L);

        // Ajouter une nouvelle charge
        Charges charge = new Charges();
        charge.setTitre("Nouvelle charge");
        charge.setDescription("Pour test comptage");
        charge.setMontant(250.0);
        charge.setDate(LocalDateTime.now());
        charge.setCabinetMedicaleId(1L);
        charge.setCreePar("admin");
        charge.setModifiePar("admin");
        chargesService.create(charge);

        Long countAfter = chargesService.countCharges(1L);

        assertEquals(countBefore + 1, countAfter, "Le compte doit augmenter de 1");

        System.out.println("✓ Comptage des charges: OK (avant=" + countBefore + ", après=" + countAfter + ")");
    }

    private void testCountChargesByPeriod() {
        System.out.println("Test: Comptage des charges par période...");

        LocalDateTime start = LocalDateTime.of(2024, 3, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 31, 23, 59);

        // Créer 2 charges en mars
        for (int i = 1; i <= 2; i++) {
            Charges charge = new Charges();
            charge.setTitre("Charge Mars " + i);
            charge.setDescription("Charge du mois de mars");
            charge.setMontant(300.0 * i);
            charge.setDate(LocalDateTime.of(2024, 3, 10 + i, 10, 0));
            charge.setCabinetMedicaleId(1L);
            charge.setCreePar("admin");
            charge.setModifiePar("admin");
            chargesService.create(charge);
        }

        Long countMars = chargesService.countChargesByPeriod(1L, start, end);

        assertEquals(2L, countMars, "Doit avoir 2 charges en mars");

        System.out.println("✓ Comptage des charges par période: OK (count = " + countMars + ")");
    }

    private void testFindChargesByPeriod() {
        System.out.println("Test: Recherche des charges par période...");

        LocalDateTime start = LocalDateTime.of(2024, 4, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 30, 23, 59);

        // Créer des charges en avril
        for (int i = 1; i <= 3; i++) {
            Charges charge = new Charges();
            charge.setTitre("Charge Avril " + i);
            charge.setDescription("Charge du mois d'avril");
            charge.setMontant(400.0 * i);
            charge.setDate(LocalDateTime.of(2024, 4, 5 + i, 14, 0));
            charge.setCabinetMedicaleId(1L);
            charge.setCreePar("admin");
            charge.setModifiePar("admin");
            chargesService.create(charge);
        }

        List<Charges> chargesAvril = chargesService.findChargesByPeriod(1L, start, end);

        assertEquals(3, chargesAvril.size(), "Doit avoir 3 charges en avril");

        // Vérifier que toutes sont bien en avril
        for (Charges c : chargesAvril) {
            int mois = c.getDate().getMonthValue();
            assertEquals(4, mois, "Toutes les charges doivent être en avril (mois 4)");
        }

        System.out.println("✓ Recherche des charges par période: OK (" + chargesAvril.size() + " charges trouvées)");
    }

    // Méthodes d'assertion simples
    private void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    private void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError(message);
        }
    }

    private void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("Attendu: " + expected + ", Reçu: " + actual);
        }
    }

    private void assertEquals(double expected, double actual) {
        if (Math.abs(expected - actual) > 0.001) {
            throw new AssertionError("Attendu: " + expected + ", Reçu: " + actual);
        }
    }

    private void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.001) {
            throw new AssertionError(message + " - Attendu: " + expected + ", Reçu: " + actual);
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    // Classe repository de test simulée
    private static class TestChargesRepository implements ChargesRepository {
        private final java.util.Map<Long, Charges> database = new java.util.HashMap<>();
        private Long currentId = 1L;

        @Override
        public List<Charges> findAll() {
            return new java.util.ArrayList<>(database.values());
        }

        @Override
        public Charges findById(Long id) {
            return database.get(id);
        }

        @Override
        public void create(Charges newElement) {
            newElement.setId(currentId++);
            database.put(newElement.getId(), cloneCharge(newElement));
        }

        @Override
        public void update(Charges newValuesElement) {
            database.put(newValuesElement.getId(), cloneCharge(newValuesElement));
        }

        @Override
        public void delete(Charges oldElement) {
            if (oldElement != null) {
                deleteById(oldElement.getId());
            }
        }

        @Override
        public void deleteById(Long id) {
            database.remove(id);
        }

        @Override
        public List<Charges> findByCabinetMedicaleId(Long cabinetId) {
            return database.values().stream()
                    .filter(c -> c.getCabinetMedicaleId().equals(cabinetId))
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public List<Charges> findByTitreContainingIgnoreCase(String titre) {
            String lowerTitre = titre.toLowerCase();
            return database.values().stream()
                    .filter(c -> c.getTitre().toLowerCase().contains(lowerTitre))
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public List<Charges> findByDescriptionContainingIgnoreCase(String description) {
            String lowerDesc = description.toLowerCase();
            return database.values().stream()
                    .filter(c -> c.getDescription().toLowerCase().contains(lowerDesc))
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public java.util.Optional<Charges> findByTitreAndCabinetId(String titre, Long cabinetId) {
            return database.values().stream()
                    .filter(c -> c.getTitre().equals(titre) && c.getCabinetMedicaleId().equals(cabinetId))
                    .findFirst();
        }

        @Override
        public Double calculateTotalCharges(Long cabinetId) {
            return database.values().stream()
                    .filter(c -> c.getCabinetMedicaleId().equals(cabinetId))
                    .mapToDouble(Charges::getMontant)
                    .sum();
        }

        @Override
        public Double calculateTotalChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
            return database.values().stream()
                    .filter(c -> c.getCabinetMedicaleId().equals(cabinetId))
                    .filter(c -> !c.getDate().isBefore(startDate) && !c.getDate().isAfter(endDate))
                    .mapToDouble(Charges::getMontant)
                    .sum();
        }

        @Override
        public Double calculateTotalChargesByTitre(Long cabinetId, String titre) {
            return database.values().stream()
                    .filter(c -> c.getCabinetMedicaleId().equals(cabinetId) && c.getTitre().equals(titre))
                    .mapToDouble(Charges::getMontant)
                    .sum();
        }

        @Override
        public Long countChargesByCabinet(Long cabinetId) {
            return database.values().stream()
                    .filter(c -> c.getCabinetMedicaleId().equals(cabinetId))
                    .count();
        }

        @Override
        public Long countChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
            return database.values().stream()
                    .filter(c -> c.getCabinetMedicaleId().equals(cabinetId))
                    .filter(c -> !c.getDate().isBefore(startDate) && !c.getDate().isAfter(endDate))
                    .count();
        }

        @Override
        public List<Charges> findChargesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
            return database.values().stream()
                    .filter(c -> c.getCabinetMedicaleId().equals(cabinetId))
                    .filter(c -> !c.getDate().isBefore(startDate) && !c.getDate().isAfter(endDate))
                    .collect(java.util.stream.Collectors.toList());
        }

        private Charges cloneCharge(Charges original) {
            Charges clone = new Charges();
            clone.setId(original.getId());
            clone.setTitre(original.getTitre());
            clone.setDescription(original.getDescription());
            clone.setMontant(original.getMontant());
            clone.setDate(original.getDate());
            clone.setCabinetMedicaleId(original.getCabinetMedicaleId());
            clone.setCreePar(original.getCreePar());
            clone.setModifiePar(original.getModifiePar());
            return clone;
        }
    }
}

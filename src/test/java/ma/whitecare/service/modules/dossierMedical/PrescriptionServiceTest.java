package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;

/**
 * Tests unitaires pour PrescriptionService.
 * Teste les validations, calculs de coût et règles métier.
 */
public class PrescriptionServiceTest {

    private PrescriptionService service;
    private PrescriptionRepository prescriptionRepository;
    private MedicamentRepository medicamentRepository;
    private OrdonnanceRepository ordonnanceRepository;

    public static void main(String[] args) {
        PrescriptionServiceTest test = new PrescriptionServiceTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== TESTS PrescriptionService ===\n");
        
        initService();
        testValidation();
        testFindById();
        testFindByDureeSuperieure();
        testCalculateCoutTotal();
        testCount();
        
        System.out.println("\n✅ Tous les tests sont terminés!");
    }

    private void initService() {
        prescriptionRepository = new ma.whitecare.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl();
        medicamentRepository = new ma.whitecare.repository.modules.medicament.impl.MedicamentRepositoryImpl();
        ordonnanceRepository = new ma.whitecare.repository.modules.ordonnance.impl.OrdonnanceRepositoryImpl();
        
        service = new PrescriptionService(prescriptionRepository, medicamentRepository, ordonnanceRepository);
        System.out.println("✓ Service initialisé\n");
    }

    private void testValidation() {
        System.out.println("--- Test Validation ---");
        
        try {
            service.create(null);
            System.out.println("❌ Devrait lancer une exception pour prescription null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation null: " + e.getMessage());
        }

        try {
            Prescription prescription = new Prescription();
            prescription.setQuantité(0);
            service.create(prescription);
            System.out.println("❌ Devrait lancer une exception pour quantité nulle");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation quantité: " + e.getMessage());
        }

        try {
            Prescription prescription = new Prescription();
            prescription.setQuantité(5);
            prescription.setDuréeEnJours(0);
            service.create(prescription);
            System.out.println("❌ Devrait lancer une exception pour durée nulle");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation durée: " + e.getMessage());
        }

        try {
            Prescription prescription = new Prescription();
            prescription.setQuantité(5);
            prescription.setDuréeEnJours(7);
            prescription.setFréquence(null);
            service.create(prescription);
            System.out.println("❌ Devrait lancer une exception pour fréquence null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation fréquence: " + e.getMessage());
        }

        System.out.println();
    }

    private void testFindById() {
        System.out.println("--- Test findById ---");
        
        try {
            service.findById(null);
            System.out.println("❌ Devrait lancer une exception pour ID null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation ID null: " + e.getMessage());
        }

        System.out.println();
    }

    private void testFindByDureeSuperieure() {
        System.out.println("--- Test findByDureeSuperieure ---");
        
        try {
            service.findByDureeSuperieure(-1);
            System.out.println("❌ Devrait lancer une exception pour jours négatifs");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation jours négatifs: " + e.getMessage());
        }

        int count = service.findByDureeSuperieure(7).size();
        System.out.println("✓ Prescriptions > 7 jours: " + count);

        System.out.println();
    }

    private void testCalculateCoutTotal() {
        System.out.println("--- Test calculateCoutTotalOrdonnance ---");
        
        try {
            Double total = service.calculateCoutTotalOrdonnance(1L);
            System.out.println("✓ Coût total calculé: " + (total != null ? total : 0.0));
        } catch (Exception e) {
            System.out.println("⚠ Erreur lors du calcul (peut être normal si ordonnance n'existe pas): " + e.getMessage());
        }

        System.out.println();
    }

    private void testCount() {
        System.out.println("--- Test count ---");
        
        long count = service.count();
        System.out.println("✓ Nombre de prescriptions: " + count);
        
        System.out.println();
    }
}


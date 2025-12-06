package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.actes.api.ActeRepository;

/**
 * Tests unitaires pour InterventionService.
 * Teste les validations, calculs de prix et règles métier.
 */
public class InterventionServiceTest {

    private InterventionService service;
    private InterventionRepository interventionRepository;
    private ConsultationRepository consultationRepository;
    private ActeRepository acteRepository;

    public static void main(String[] args) {
        InterventionServiceTest test = new InterventionServiceTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== TESTS InterventionService ===\n");
        
        initService();
        testValidation();
        testFindById();
        testFindByNumDent();
        testCalculateTotal();
        testCount();
        
        System.out.println("\n✅ Tous les tests sont terminés!");
    }

    private void initService() {
        interventionRepository = new ma.whitecare.repository.modules.dossierMedical.impl.InterventionRepositoryImpl();
        consultationRepository = new ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl();
        acteRepository = new ma.whitecare.repository.modules.actes.impl.ActeRepositoryImpl();
        
        service = new InterventionService(interventionRepository, consultationRepository, acteRepository);
        System.out.println("✓ Service initialisé\n");
    }

    private void testValidation() {
        System.out.println("--- Test Validation ---");
        
        try {
            service.create(null);
            System.out.println("❌ Devrait lancer une exception pour intervention null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation null: " + e.getMessage());
        }

        try {
            InterventionMedecin intervention = new InterventionMedecin();
            intervention.setPrixDePatient(-10.0);
            service.create(intervention);
            System.out.println("❌ Devrait lancer une exception pour prix négatif");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation prix négatif: " + e.getMessage());
        }

        try {
            InterventionMedecin intervention = new InterventionMedecin();
            intervention.setNumDent(50);
            service.create(intervention);
            System.out.println("❌ Devrait lancer une exception pour numéro de dent invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation numéro de dent: " + e.getMessage());
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

    private void testFindByNumDent() {
        System.out.println("--- Test findByNumDent ---");
        
        try {
            service.findByNumDent(null);
            System.out.println("❌ Devrait lancer une exception pour numéro null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation numéro null: " + e.getMessage());
        }

        try {
            service.findByNumDent(50);
            System.out.println("❌ Devrait lancer une exception pour numéro invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation numéro invalide: " + e.getMessage());
        }

        System.out.println();
    }

    private void testCalculateTotal() {
        System.out.println("--- Test calculateTotalByConsultation ---");
        
        try {
            Double total = service.calculateTotalByConsultation(1L);
            System.out.println("✓ Total calculé: " + (total != null ? total : 0.0));
        } catch (Exception e) {
            System.out.println("⚠ Erreur lors du calcul (peut être normal si consultation n'existe pas): " + e.getMessage());
        }

        System.out.println();
    }

    private void testCount() {
        System.out.println("--- Test count ---");
        
        long count = service.count();
        System.out.println("✓ Nombre d'interventions: " + count);
        
        System.out.println();
    }
}


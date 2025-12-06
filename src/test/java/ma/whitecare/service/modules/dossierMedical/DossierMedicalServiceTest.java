package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests unitaires pour DossierMedicalService.
 * Teste les validations, vérifications d'existence et règles métier.
 */
public class DossierMedicalServiceTest {

    private DossierMedicalService service;
    private DossierMedicalRepository dossierRepository;
    private PatientRepository patientRepository;
    private MedecinRepository medecinRepository;

    public static void main(String[] args) {
        DossierMedicalServiceTest test = new DossierMedicalServiceTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== TESTS DossierMedicalService ===\n");
        
        initService();
        testValidation();
        testFindById();
        testFindByPatientId();
        testFindByMedecinId();
        testExistsByPatientId();
        testCount();
        
        System.out.println("\n✅ Tous les tests sont terminés!");
    }

    private void initService() {
        // Note: Dans un vrai test, on utiliserait des mocks
        // Ici, on utilise les vraies implémentations pour tester l'intégration
        dossierRepository = new ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl();
        patientRepository = new ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl();
        medecinRepository = new ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl();
        
        service = new DossierMedicalService(dossierRepository, patientRepository, medecinRepository);
        System.out.println("✓ Service initialisé\n");
    }

    private void testValidation() {
        System.out.println("--- Test Validation ---");
        
        try {
            // Test avec dossier null
            service.create(null);
            System.out.println("❌ Devrait lancer une exception pour dossier null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation null: " + e.getMessage());
        }

        try {
            // Test avec patient null
            DossierMedicale dossier = new DossierMedicale();
            service.create(dossier);
            System.out.println("❌ Devrait lancer une exception pour patient null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation patient obligatoire: " + e.getMessage());
        }

        System.out.println();
    }

    private void testFindById() {
        System.out.println("--- Test findById ---");
        
        try {
            // Test avec ID null
            service.findById(null);
            System.out.println("❌ Devrait lancer une exception pour ID null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation ID null: " + e.getMessage());
        }

        try {
            // Test avec ID invalide
            service.findById(-1L);
            System.out.println("❌ Devrait lancer une exception pour ID invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation ID invalide: " + e.getMessage());
        }

        System.out.println();
    }

    private void testFindByPatientId() {
        System.out.println("--- Test findByPatientId ---");
        
        try {
            // Test avec ID null
            service.findByPatientId(null);
            System.out.println("❌ Devrait lancer une exception pour ID null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation ID patient null: " + e.getMessage());
        }

        try {
            // Test avec patient inexistant
            service.findByPatientId(99999L);
            System.out.println("❌ Devrait lancer une exception pour patient inexistant");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation patient inexistant: " + e.getMessage());
        }

        System.out.println();
    }

    private void testFindByMedecinId() {
        System.out.println("--- Test findByMedecinId ---");
        
        try {
            // Test avec ID null
            service.findByMedecinId(null);
            System.out.println("❌ Devrait lancer une exception pour ID null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation ID médecin null: " + e.getMessage());
        }

        System.out.println();
    }

    private void testExistsByPatientId() {
        System.out.println("--- Test existsByPatientId ---");
        
        boolean exists = service.existsByPatientId(null);
        if (!exists) {
            System.out.println("✓ Retourne false pour ID null");
        }

        System.out.println();
    }

    private void testCount() {
        System.out.println("--- Test count ---");
        
        long count = service.count();
        System.out.println("✓ Nombre de dossiers: " + count);
        
        System.out.println();
    }
}


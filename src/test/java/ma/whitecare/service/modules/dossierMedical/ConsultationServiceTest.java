package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;

import java.time.LocalDate;

/**
 * Tests unitaires pour ConsultationService.
 * Teste les validations, changements de statut et règles métier.
 */
public class ConsultationServiceTest {

    private ConsultationService service;
    private ConsultationRepository consultationRepository;
    private DossierMedicalRepository dossierRepository;

    public static void main(String[] args) {
        ConsultationServiceTest test = new ConsultationServiceTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== TESTS ConsultationService ===\n");
        
        initService();
        testValidation();
        testFindById();
        testFindByStatut();
        testFindByDateBetween();
        testChangeStatut();
        testCount();
        
        System.out.println("\n✅ Tous les tests sont terminés!");
    }

    private void initService() {
        consultationRepository = new ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl();
        dossierRepository = new ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl();
        
        service = new ConsultationService(consultationRepository, dossierRepository);
        System.out.println("✓ Service initialisé\n");
    }

    private void testValidation() {
        System.out.println("--- Test Validation ---");
        
        try {
            service.create(null);
            System.out.println("❌ Devrait lancer une exception pour consultation null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation null: " + e.getMessage());
        }

        try {
            Consultation consultation = new Consultation();
            service.create(consultation);
            System.out.println("❌ Devrait lancer une exception pour dossier médical null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation dossier obligatoire: " + e.getMessage());
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

    private void testFindByStatut() {
        System.out.println("--- Test findByStatut ---");
        
        try {
            service.findByStatut(null);
            System.out.println("❌ Devrait lancer une exception pour statut null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation statut null: " + e.getMessage());
        }

        long countUrgentes = service.findUrgentes().size();
        System.out.println("✓ Consultations urgentes: " + countUrgentes);

        System.out.println();
    }

    private void testFindByDateBetween() {
        System.out.println("--- Test findByDateBetween ---");
        
        try {
            service.findByDateBetween(null, LocalDate.now());
            System.out.println("❌ Devrait lancer une exception pour date null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation date null: " + e.getMessage());
        }

        try {
            service.findByDateBetween(LocalDate.now(), LocalDate.now().minusDays(1));
            System.out.println("❌ Devrait lancer une exception pour dates inversées");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation dates inversées: " + e.getMessage());
        }

        System.out.println();
    }

    private void testChangeStatut() {
        System.out.println("--- Test changeStatut ---");
        
        try {
            service.changeStatut(null, StatutConsultation.TERMINEE);
            System.out.println("❌ Devrait lancer une exception pour ID null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation ID null: " + e.getMessage());
        }

        try {
            service.changeStatut(1L, null);
            System.out.println("❌ Devrait lancer une exception pour statut null");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validation statut null: " + e.getMessage());
        }

        System.out.println();
    }

    private void testCount() {
        System.out.println("--- Test count ---");
        
        long count = service.count();
        System.out.println("✓ Nombre de consultations: " + count);
        
        long countTerminees = service.countByStatut(StatutConsultation.TERMINEE);
        System.out.println("✓ Consultations terminées: " + countTerminees);
        
        System.out.println();
    }
}


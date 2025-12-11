package ma.whitecare.service.test;

import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl;
import ma.whitecare.service.modules.dossierMedical.impl.ConsultationServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class TestConsultationService {

    public static void main(String[] args) {
        System.out.println("=== TEST DU CONSULTATION SERVICE ===\n");

        try {
            // 1. Initialisation des repositories et service
            ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
            DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
            PatientRepository patientRepo = new PatientRepositoryImpl();
            MedecinRepository medecinRepo = new MedecinRepositoryImpl();
            ConsultationServiceImpl consultationService = new ConsultationServiceImpl(consultationRepo, dossierRepo);

            // 2. Préparation: Créer les données de test nécessaires
            System.out.println("Préparation: Création des données de test");
            
            // Créer un patient
            Patient testPatient = new Patient();
            testPatient.setNom("Bernard");
            testPatient.setPrenom("Marie");
            testPatient.setEmail("marie.bernard.test@email.com");
            testPatient.setTelephone("0623456789");
            testPatient.setAdresse("456 Avenue Test");
            testPatient.setDateNaissance(LocalDate.of(1985, 7, 20));
            testPatient.setSexe(Sexe.FEMME);
            testPatient.setAssurance(Assurance.CNOPS);
            testPatient.setCreePar("system");
            testPatient.setModifiePar("system");
            patientRepo.create(testPatient);
            System.out.println("✓ Patient créé avec ID: " + testPatient.getId_Patient());

            // Récupérer un médecin
            List<Medecin> medecins = medecinRepo.findAll();
            if (medecins.isEmpty()) {
                System.out.println("⚠ Aucun médecin trouvé. Veuillez exécuter seed.sql");
                return;
            }
            Medecin testMedecin = medecins.get(0);
            System.out.println("✓ Médecin trouvé: " + testMedecin.getNom() + " (ID: " + testMedecin.getIdUser() + ")");

            // Créer un dossier médical
            DossierMedicale dossier = new DossierMedicale();
            dossier.setDateDeCreation(LocalDate.now());
            dossier.setPatient(testPatient);
            dossier.setMedecin(testMedecin);
            dossier.setCreePar("system");
            dossier.setModifiePar("system");
            dossierRepo.create(dossier);
            System.out.println("✓ Dossier médical créé avec ID: " + dossier.getIdDM());

            // 3. TEST 1: Liste de toutes les consultations
            System.out.println("\n=== Test 1: Liste de toutes les consultations ===");
            List<Consultation> allConsultations = consultationService.getAllConsultations();
            System.out.println("✓ Nombre total de consultations: " + allConsultations.size());

            // 4. TEST 2: Création d'une consultation
            System.out.println("\n=== Test 2: Création d'une consultation ===");
            Consultation newConsultation = new Consultation();
            newConsultation.setDate(LocalDate.now());
            newConsultation.setStatut(StatutConsultation.EN_ATTENTE);
            newConsultation.setObservationMedecin("Consultation de test - première visite");
            newConsultation.setDossierMedicale(dossier);
            newConsultation.setCreePar("system");
            newConsultation.setModifiePar("system");

            Consultation created = consultationService.createConsultation(newConsultation);
            System.out.println("✓ Consultation créée avec ID: " + created.getIdConsultation());
            System.out.println("  Date: " + created.getDate());
            System.out.println("  Statut: " + created.getStatut());

            // 5. TEST 3: Récupération par ID
            System.out.println("\n=== Test 3: Récupération par ID ===");
            Consultation found = consultationService.getConsultationById(created.getIdConsultation());
            System.out.println("✓ Consultation trouvée: ID " + found.getIdConsultation());

            // TEST 3.5: Recherche par dossier médical
            System.out.println("\n=== Test 3.5: Recherche par dossier médical ID ===");
            List<Consultation> byDossier = consultationService.findByDossierMedicalId(dossier.getIdDM());
            System.out.println("✓ Consultations pour dossier ID " + dossier.getIdDM() + ": " + byDossier.size());

            // 6. TEST 4: Recherche par statut
            System.out.println("\n=== Test 4: Recherche par statut ===");
            List<Consultation> enAttente = consultationService.findByStatut(StatutConsultation.EN_ATTENTE);
            List<Consultation> terminees = consultationService.findByStatut(StatutConsultation.TERMINEE);
            System.out.println("✓ Consultations en attente: " + enAttente.size());
            System.out.println("✓ Consultations terminées: " + terminees.size());

            // 7. TEST 5: Recherche par date
            System.out.println("\n=== Test 5: Recherche par date ===");
            List<Consultation> byDate = consultationService.findByDate(LocalDate.now());
            System.out.println("✓ Consultations aujourd'hui: " + byDate.size());

            // 8. TEST 6: Recherche par période
            System.out.println("\n=== Test 6: Recherche par période ===");
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            List<Consultation> byPeriod = consultationService.findByDateBetween(startDate, endDate);
            System.out.println("✓ Consultations dans les 7 derniers jours: " + byPeriod.size());

            // 9. TEST 7: Recherche par dossier et date
            System.out.println("\n=== Test 7: Recherche par dossier et date ===");
            List<Consultation> byDossierAndDate = consultationService.findByDossierAndDate(dossier.getIdDM(), LocalDate.now());
            System.out.println("✓ Consultations pour ce dossier aujourd'hui: " + byDossierAndDate.size());

            // 10. TEST 8: Changement de statut
            System.out.println("\n=== Test 8: Changement de statut ===");
            consultationService.changeStatut(created.getIdConsultation(), StatutConsultation.EN_COURS);
            System.out.println("✓ Statut changé à EN_COURS");
            
            Consultation updatedStatus = consultationService.getConsultationById(created.getIdConsultation());
            System.out.println("  Nouveau statut: " + updatedStatus.getStatut());

            // 11. TEST 9: Mise à jour
            System.out.println("\n=== Test 9: Mise à jour ===");
            Consultation updateConsultation = new Consultation();
            updateConsultation.setObservationMedecin("Observation mise à jour - examen complet effectué");
            Consultation updatedConsultation = consultationService.updateConsultation(created.getIdConsultation(), updateConsultation);
            System.out.println("✓ Consultation mise à jour");
            System.out.println("  Nouvelle observation: " + updatedConsultation.getObservationMedecin());

            // 12. TEST 10: Statistiques
            System.out.println("\n=== Test 10: Statistiques ===");
            long total = consultationService.countAllConsultations();
            long byStatut = consultationService.countByStatut(StatutConsultation.EN_COURS);
            long byDossierCount = consultationService.countByDossierMedicalId(dossier.getIdDM());
            System.out.println("✓ Total consultations: " + total);
            System.out.println("✓ Consultations en cours: " + byStatut);
            System.out.println("✓ Consultations pour ce dossier: " + byDossierCount);

            // 13. TEST 11: Vérification d'existence
            System.out.println("\n=== Test 11: Vérification d'existence ===");
            boolean exists = consultationService.existsById(created.getIdConsultation());
            System.out.println("✓ Consultation existe: " + exists);

            // 14. TEST 12: Suppression
            System.out.println("\n=== Test 12: Suppression ===");
            consultationService.deleteConsultation(created.getIdConsultation());
            System.out.println("✓ Consultation supprimée");

            // Vérifier qu'elle n'existe plus
            try {
                consultationService.getConsultationById(created.getIdConsultation());
                System.out.println("✗ ERREUR: La consultation devrait être supprimée");
            } catch (Exception e) {
                System.out.println("✓ La consultation a bien été supprimée (exception attendue)");
            }

            System.out.println("\n=== TESTS TERMINÉS AVEC SUCCÈS ===");

        } catch (Exception e) {
            System.err.println("\n✗ ERREUR pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

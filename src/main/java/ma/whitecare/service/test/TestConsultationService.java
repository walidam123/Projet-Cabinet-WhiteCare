package ma.whitecare.service.test;

import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.mvc.dto.dossierMedical.ConsultationDTO;
import ma.whitecare.mvc.dto.dossierMedical.ConsultationCompleteDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreateConsultationDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdateConsultationDTO;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.InterventionRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl;
import ma.whitecare.repository.modules.ordonnance.impl.OrdonnanceRepositoryImpl;
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
            InterventionRepository interventionRepo = new InterventionRepositoryImpl();
            OrdonnanceRepository ordonnanceRepo = new OrdonnanceRepositoryImpl();
            PrescriptionRepository prescriptionRepo = new PrescriptionRepositoryImpl();
            PatientRepository patientRepo = new PatientRepositoryImpl();
            MedecinRepository medecinRepo = new MedecinRepositoryImpl();
            ConsultationServiceImpl consultationService = new ConsultationServiceImpl(consultationRepo, dossierRepo, interventionRepo, ordonnanceRepo, prescriptionRepo);

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
            List<ConsultationDTO> allConsultations = consultationService.getAllConsultations();
            System.out.println("Liste des consultations:");
            for (ConsultationDTO c : allConsultations) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + 
                    ", Date: " + c.getDate() + ", Statut: " + c.getStatut());
            }
            System.out.println("✓ Nombre total de consultations: " + allConsultations.size());

            // 4. TEST 2: Création d'une consultation
            System.out.println("\n=== Test 2: Création d'une consultation ===");
            CreateConsultationDTO createDTO = CreateConsultationDTO.builder()
                    .dossierMedicalId(dossier.getIdDM())
                    .date(LocalDate.now())
                    .statut(StatutConsultation.EN_ATTENTE)
                    .observationMedecin("Consultation de test - première visite")
                    .build();

            ConsultationDTO created = consultationService.createConsultation(createDTO);
            System.out.println("✓ Consultation créée avec ID: " + created.getIdConsultation());
            System.out.println("  Date: " + created.getDate());
            System.out.println("  Statut: " + created.getStatut());

            // 5. TEST 3: Récupération par ID
            System.out.println("\n=== Test 3: Récupération par ID ===");
            ConsultationDTO found = consultationService.getConsultationById(created.getIdConsultation());
            System.out.println("✓ Consultation trouvée: ID " + found.getIdConsultation());

            // TEST 3.5: Recherche par dossier médical
            System.out.println("\n=== Test 3.5: Recherche par dossier médical ID ===");
            List<ConsultationDTO> byDossier = consultationService.findByDossierMedicalId(dossier.getIdDM());
            System.out.println("Consultations pour dossier ID " + dossier.getIdDM() + ":");
            for (ConsultationDTO c : byDossier) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + 
                    ", Date: " + c.getDate() + ", Statut: " + c.getStatut());
            }
            System.out.println("✓ Consultations pour dossier ID " + dossier.getIdDM() + ": " + byDossier.size());

            // 6. TEST 4: Recherche par statut
            System.out.println("\n=== Test 4: Recherche par statut ===");
            List<ConsultationDTO> enAttente = consultationService.findByStatut(StatutConsultation.EN_ATTENTE);
            System.out.println("Consultations en attente:");
            for (ConsultationDTO c : enAttente) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + ", Date: " + c.getDate());
            }
            System.out.println("✓ Consultations en attente: " + enAttente.size());
            
            List<ConsultationDTO> terminees = consultationService.findByStatut(StatutConsultation.TERMINEE);
            System.out.println("Consultations terminées:");
            for (ConsultationDTO c : terminees) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + ", Date: " + c.getDate());
            }
            System.out.println("✓ Consultations terminées: " + terminees.size());

            // 7. TEST 5: Recherche par date
            System.out.println("\n=== Test 5: Recherche par date ===");
            List<ConsultationDTO> byDate = consultationService.findByDate(LocalDate.now());
            System.out.println("Consultations aujourd'hui (" + LocalDate.now() + "):");
            for (ConsultationDTO c : byDate) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + ", Statut: " + c.getStatut());
            }
            System.out.println("✓ Consultations aujourd'hui: " + byDate.size());

            // 8. TEST 6: Recherche par période
            System.out.println("\n=== Test 6: Recherche par période ===");
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            List<ConsultationDTO> byPeriod = consultationService.findByDateBetween(startDate, endDate);
            System.out.println("Consultations entre " + startDate + " et " + endDate + ":");
            for (ConsultationDTO c : byPeriod) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + 
                    ", Date: " + c.getDate() + ", Statut: " + c.getStatut());
            }
            System.out.println("✓ Consultations dans les 7 derniers jours: " + byPeriod.size());

            // 9. TEST 7: Recherche par dossier et date
            System.out.println("\n=== Test 7: Recherche par dossier et date ===");
            List<ConsultationDTO> byDossierAndDate = consultationService.findByDossierAndDate(dossier.getIdDM(), LocalDate.now());
            System.out.println("Consultations pour dossier ID " + dossier.getIdDM() + " aujourd'hui:");
            for (ConsultationDTO c : byDossierAndDate) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + ", Statut: " + c.getStatut());
            }
            System.out.println("✓ Consultations pour ce dossier aujourd'hui: " + byDossierAndDate.size());

            // 10. TEST 8: Changement de statut
            System.out.println("\n=== Test 8: Changement de statut ===");
            consultationService.changeStatut(created.getIdConsultation(), StatutConsultation.EN_COURS);
            System.out.println("✓ Statut changé à EN_COURS");
            
            ConsultationDTO updatedStatus = consultationService.getConsultationById(created.getIdConsultation());
            System.out.println("  Nouveau statut: " + updatedStatus.getStatut());

            // 11. TEST 9: Mise à jour
            System.out.println("\n=== Test 9: Mise à jour ===");
            UpdateConsultationDTO updateDTO = UpdateConsultationDTO.builder()
                    .observationMedecin("Observation mise à jour - examen complet effectué")
                    .build();
            ConsultationDTO updatedConsultation = consultationService.updateConsultation(created.getIdConsultation(), updateDTO);
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

            // 14. TEST 12: Consultations du jour
            System.out.println("\n=== Test 12: Consultations du jour ===");
            List<ConsultationDTO> consultationsDuJour = consultationService.getConsultationsDuJour();
            System.out.println("Consultations prévues aujourd'hui (" + LocalDate.now() + "):");
            for (ConsultationDTO c : consultationsDuJour) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + 
                    ", Statut: " + c.getStatut() + ", Dossier ID: " + 
                    (c.getDossierMedicalId() != null ? c.getDossierMedicalId() : "N/A"));
            }
            System.out.println("✓ Nombre de consultations aujourd'hui: " + consultationsDuJour.size());

            // 15. TEST 13: Consultation complète
            System.out.println("\n=== Test 13: Consultation complète ===");
            ConsultationCompleteDTO consultationComplete = consultationService.getConsultationComplete(created.getIdConsultation());
            if (consultationComplete != null) {
                System.out.println("✓ Consultation complète récupérée: ID " + consultationComplete.getIdConsultation());
                System.out.println("  Nombre d'interventions: " + 
                    (consultationComplete.getInterventions() != null ? consultationComplete.getInterventions().size() : 0));
                System.out.println("  Nombre de prescriptions: " + 
                    (consultationComplete.getPrescriptions() != null ? consultationComplete.getPrescriptions().size() : 0));
            } else {
                System.out.println("✗ ERREUR: Consultation complète non trouvée");
            }

            // 16. TEST 14: Suppression
            System.out.println("\n=== Test 14: Suppression ===");
            try {
                Thread.sleep(20000); // Attendre 20 secondes que les processus en cours se terminent
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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

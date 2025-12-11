package ma.whitecare.service.test;

import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.actes.api.ActeRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.dossierMedical.impl.InterventionRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.whitecare.repository.modules.actes.impl.ActeRepositoryImpl;
import ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl;
import ma.whitecare.service.modules.dossierMedical.impl.InterventionServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class TestInterventionService {

    public static void main(String[] args) {
        System.out.println("=== TEST DU INTERVENTION SERVICE ===\n");

        try {
            // 1. Initialisation des repositories et service
            InterventionRepository interventionRepo = new InterventionRepositoryImpl();
            ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
            ActeRepository acteRepo = new ActeRepositoryImpl();
            DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
            PatientRepository patientRepo = new PatientRepositoryImpl();
            MedecinRepository medecinRepo = new MedecinRepositoryImpl();
            InterventionServiceImpl interventionService = new InterventionServiceImpl(interventionRepo, consultationRepo, acteRepo);

            // 2. Préparation: Créer les données de test nécessaires
            System.out.println("Préparation: Création des données de test");
            
            // Créer un patient
            Patient testPatient = new Patient();
            testPatient.setNom("Lefebvre");
            testPatient.setPrenom("Thomas");
            testPatient.setEmail("thomas.lefebvre.test@email.com");
            testPatient.setTelephone("0634567890");
            testPatient.setAdresse("789 Boulevard Test");
            testPatient.setDateNaissance(LocalDate.of(1992, 11, 8));
            testPatient.setSexe(Sexe.HOMME);
            testPatient.setAssurance(Assurance.PRIVEE);
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

            // Créer une consultation
            Consultation consultation = new Consultation();
            consultation.setDate(LocalDate.now());
            consultation.setStatut(StatutConsultation.EN_COURS);
            consultation.setObservationMedecin("Consultation pour intervention");
            consultation.setDossierMedicale(dossier);
            consultation.setCreePar("system");
            consultation.setModifiePar("system");
            consultationRepo.create(consultation);
            System.out.println("✓ Consultation créée avec ID: " + consultation.getIdConsultation());

            // Créer un acte
            Acte acte = new Acte();
            acte.setLibelle("Détartrage");
            acte.setCategorie("Soin préventif");
            acte.setPrixDeBase(300.0);
            acte.setCreePar("system");
            acte.setModifiePar("system");
            acteRepo.create(acte);
            System.out.println("✓ Acte créé avec ID: " + acte.getIdActe());
            System.out.println("  Libellé: " + acte.getLibelle());
            System.out.println("  Prix: " + acte.getPrixDeBase() + " MAD");

            // 3. TEST 1: Liste de toutes les interventions
            System.out.println("\n=== Test 1: Liste de toutes les interventions ===");
            List<InterventionMedecin> allInterventions = interventionService.getAllInterventions();
            System.out.println("✓ Nombre total d'interventions: " + allInterventions.size());

            // 4. TEST 2: Création d'une intervention
            System.out.println("\n=== Test 2: Création d'une intervention ===");
            InterventionMedecin newIntervention = new InterventionMedecin();
            newIntervention.setPrixDePatient(500.0);
            newIntervention.setNumDent(16);
            newIntervention.setConsultation(consultation);
            newIntervention.setActe(acte);
            newIntervention.setCreePar("system");
            newIntervention.setModifiePar("system");

            InterventionMedecin created = interventionService.createIntervention(newIntervention);
            System.out.println("✓ Intervention créée avec ID: " + created.getIdIM());
            System.out.println("  Prix: " + created.getPrixDePatient() + " MAD");
            System.out.println("  Dent: " + created.getNumDent());

            // 5. TEST 3: Récupération par ID
            System.out.println("\n=== Test 3: Récupération par ID ===");
            InterventionMedecin found = interventionService.getInterventionById(created.getIdIM());
            System.out.println("✓ Intervention trouvée: ID " + found.getIdIM());

            // 6. TEST 4: Recherche par consultation
            System.out.println("\n=== Test 4: Recherche par consultation ID ===");
            List<InterventionMedecin> byConsultation = interventionService.findByConsultationId(consultation.getIdConsultation());
            System.out.println("✓ Interventions pour consultation ID " + consultation.getIdConsultation() + ": " + byConsultation.size());

            // 7. TEST 5: Recherche par acte
            System.out.println("\n=== Test 5: Recherche par acte ID ===");
            List<InterventionMedecin> byActe = interventionService.findByActeId(acte.getIdActe());
            System.out.println("✓ Interventions pour acte ID " + acte.getIdActe() + ": " + byActe.size());

            // 8. TEST 6: Recherche par numéro de dent
            System.out.println("\n=== Test 6: Recherche par numéro de dent ===");
            List<InterventionMedecin> byDent = interventionService.findByNumDent(16);
            System.out.println("✓ Interventions pour dent 16: " + byDent.size());

            // 9. TEST 7: Recherche par consultation et acte
            System.out.println("\n=== Test 7: Recherche par consultation et acte ===");
            List<InterventionMedecin> byConsultationAndActe = interventionService.findByConsultationAndActe(
                consultation.getIdConsultation(), acte.getIdActe());
            System.out.println("✓ Interventions pour consultation et acte: " + byConsultationAndActe.size());

            // 10. TEST 8: Calcul du total par consultation
            System.out.println("\n=== Test 8: Calcul du total par consultation ===");
            Double total = interventionService.calculateTotalByConsultation(consultation.getIdConsultation());
            System.out.println("✓ Total pour consultation ID " + consultation.getIdConsultation() + ": " + total + " MAD");

            // 11. TEST 9: Statistiques
            System.out.println("\n=== Test 9: Statistiques ===");
            long totalCount = interventionService.countAllInterventions();
            long byConsultationCount = interventionService.countByConsultationId(consultation.getIdConsultation());
            long byActeCount = interventionService.countByActeId(acte.getIdActe());
            System.out.println("✓ Total interventions: " + totalCount);
            System.out.println("✓ Interventions pour cette consultation: " + byConsultationCount);
            System.out.println("✓ Interventions pour cet acte: " + byActeCount);

            // 12. TEST 10: Vérification d'existence
            System.out.println("\n=== Test 10: Vérification d'existence ===");
            boolean exists = interventionService.existsById(created.getIdIM());
            System.out.println("✓ Intervention existe: " + exists);

            // 13. TEST 11: Mise à jour
            System.out.println("\n=== Test 11: Mise à jour ===");
            InterventionMedecin updateIntervention = new InterventionMedecin();
            updateIntervention.setPrixDePatient(600.0);
            updateIntervention.setNumDent(17);
            InterventionMedecin updated = interventionService.updateIntervention(created.getIdIM(), updateIntervention);
            System.out.println("✓ Intervention mise à jour");
            System.out.println("  Nouveau prix: " + updated.getPrixDePatient() + " MAD");
            System.out.println("  Nouvelle dent: " + updated.getNumDent());

            // 14. TEST 12: Suppression
            System.out.println("\n=== Test 12: Suppression ===");
            interventionService.deleteIntervention(created.getIdIM());
            System.out.println("✓ Intervention supprimée");

            // Vérifier qu'elle n'existe plus
            try {
                interventionService.getInterventionById(created.getIdIM());
                System.out.println("✗ ERREUR: L'intervention devrait être supprimée");
            } catch (Exception e) {
                System.out.println("✓ L'intervention a bien été supprimée (exception attendue)");
            }

            System.out.println("\n=== TESTS TERMINÉS AVEC SUCCÈS ===");

        } catch (Exception e) {
            System.err.println("\n✗ ERREUR pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

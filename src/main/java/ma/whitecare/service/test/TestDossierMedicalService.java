package ma.whitecare.service.test;

import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.mvc.dto.dossierMedical.DossierMedicalDTO;
import ma.whitecare.mvc.dto.dossierMedical.ConsultationDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreateDossierMedicalDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdateDossierMedicalDTO;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl;
import ma.whitecare.service.modules.dossierMedical.impl.DossierMedicalServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class TestDossierMedicalService {

    public static void main(String[] args) {
        System.out.println("=== TEST DU DOSSIER MÉDICAL SERVICE ===\n");

        try {
            // 1. Initialisation des repositories et service
            DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
            PatientRepository patientRepo = new PatientRepositoryImpl();
            MedecinRepository medecinRepo = new MedecinRepositoryImpl();
            ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
            DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl(dossierRepo, patientRepo, medecinRepo, consultationRepo);

            // 2. Créer un patient de test
            System.out.println("Préparation: Création d'un patient de test");
            Patient testPatient = new Patient();
            testPatient.setNom("Martin");
            testPatient.setPrenom("Sophie");
            testPatient.setEmail("sophie.martin.test@email.com");
            testPatient.setTelephone("0612345678");
            testPatient.setAdresse("123 Rue Test");
            testPatient.setDateNaissance(LocalDate.of(1990, 3, 15));
            testPatient.setSexe(Sexe.FEMME);
            testPatient.setAssurance(Assurance.CNSS);
            testPatient.setCreePar("system");
            testPatient.setModifiePar("system");
            patientRepo.create(testPatient);
            System.out.println("✓ Patient créé avec ID: " + testPatient.getId_Patient());

            // 3. Récupérer un médecin existant de la base de données
            System.out.println("\nPréparation: Récupération d'un médecin existant");
            List<Medecin> medecins = medecinRepo.findAll();
            Medecin testMedecin = null;
            if (!medecins.isEmpty()) {
                testMedecin = medecins.get(0);
                System.out.println("✓ Médecin trouvé: " + testMedecin.getNom() + " " + testMedecin.getPrenom());
                System.out.println("  ID: " + testMedecin.getIdUser());
                System.out.println("  Spécialité: " + testMedecin.getSpecialite());
            } else {
                System.out.println("⚠ Aucun médecin trouvé dans la base de données");
                System.out.println("  Veuillez exécuter le script seed.sql pour créer un médecin de test");
                return;
            }

            // 4. TEST 1: Création d'un dossier médical
            System.out.println("\n=== Test 1: Création d'un dossier médical ===");
            CreateDossierMedicalDTO createDTO = CreateDossierMedicalDTO.builder()
                    .patientId(testPatient.getId_Patient())
                    .medecinId(testMedecin.getIdUser())
                    .dateDeCreation(LocalDate.now())
                    .build();

            DossierMedicalDTO createdDossier = dossierService.createDossierMedical(createDTO);
            System.out.println("✓ Dossier médical créé avec ID: " + createdDossier.getIdDM());
            System.out.println("  Date de création: " + createdDossier.getDateDeCreation());

            // 5. TEST 2: Récupération par ID
            System.out.println("\n=== Test 2: Récupération par ID ===");
            DossierMedicalDTO foundDossier = dossierService.getDossierMedicalById(createdDossier.getIdDM());
            System.out.println("✓ Dossier trouvé: ID " + foundDossier.getIdDM());

            // 6. TEST 3: Liste de tous les dossiers
            System.out.println("\n=== Test 3: Liste de tous les dossiers ===");
            List<DossierMedicalDTO> allDossiers = dossierService.getAllDossiersMedicaux();
            System.out.println("Liste des dossiers:");
            for (DossierMedicalDTO d : allDossiers) {
                System.out.println("  - Dossier ID: " + d.getIdDM() + 
                    ", Patient ID: " + (d.getPatientId() != null ? d.getPatientId() : "N/A") +
                    ", Date: " + d.getDateDeCreation());
            }
            System.out.println("✓ Nombre total de dossiers: " + allDossiers.size());

            // 7. TEST 4: Recherche par patient
            System.out.println("\n=== Test 4: Recherche par patient ID ===");
            List<DossierMedicalDTO> byPatient = dossierService.findByPatientId(testPatient.getId_Patient());
            System.out.println("Dossiers trouvés pour le patient ID " + testPatient.getId_Patient() + ":");
            for (DossierMedicalDTO d : byPatient) {
                System.out.println("  - Dossier ID: " + d.getIdDM() + ", Date: " + d.getDateDeCreation());
            }
            System.out.println("✓ Dossiers trouvés pour le patient: " + byPatient.size());

            // 8. TEST 5: Recherche par médecin
            System.out.println("\n=== Test 5: Recherche par médecin ID ===");
            List<DossierMedicalDTO> byMedecin = dossierService.findByMedecinId(testMedecin.getIdUser());
            System.out.println("Dossiers trouvés pour le médecin ID " + testMedecin.getIdUser() + ":");
            for (DossierMedicalDTO d : byMedecin) {
                System.out.println("  - Dossier ID: " + d.getIdDM() + ", Date: " + d.getDateDeCreation());
            }
            System.out.println("✓ Dossiers trouvés pour le médecin: " + byMedecin.size());

            // 9. TEST 6: Vérification d'existence
            System.out.println("\n=== Test 6: Vérification d'existence ===");
            boolean exists = dossierService.existsByPatientId(testPatient.getId_Patient());
            System.out.println("✓ Dossier existe pour ce patient: " + exists);

            // 10. TEST 7: Recherche par date
            System.out.println("\n=== Test 7: Recherche par date ===");
            List<DossierMedicalDTO> byDate = dossierService.findByDateCreation(LocalDate.now());
            System.out.println("Dossiers créés aujourd'hui (" + LocalDate.now() + "):");
            for (DossierMedicalDTO d : byDate) {
                System.out.println("  - Dossier ID: " + d.getIdDM() + ", Patient ID: " + 
                    (d.getPatientId() != null ? d.getPatientId() : "N/A"));
            }
            System.out.println("✓ Dossiers créés aujourd'hui: " + byDate.size());

            // 11. TEST 8: Recherche par période
            System.out.println("\n=== Test 8: Recherche par période ===");
            LocalDate startDate = LocalDate.now().minusDays(30);
            LocalDate endDate = LocalDate.now();
            List<DossierMedicalDTO> byPeriod = dossierService.findByDateCreationBetween(startDate, endDate);
            System.out.println("Dossiers créés entre " + startDate + " et " + endDate + ":");
            for (DossierMedicalDTO d : byPeriod) {
                System.out.println("  - Dossier ID: " + d.getIdDM() + ", Date: " + d.getDateDeCreation());
            }
            System.out.println("✓ Dossiers créés dans les 30 derniers jours: " + byPeriod.size());

            // 12. TEST 9: Statistiques
            System.out.println("\n=== Test 9: Statistiques ===");
            long total = dossierService.countAllDossiers();
            long byPatientCount = dossierService.countByPatientId(testPatient.getId_Patient());
            long byMedecinCount = dossierService.countByMedecinId(testMedecin.getIdUser());
            System.out.println("✓ Total dossiers: " + total);
            System.out.println("✓ Dossiers pour ce patient: " + byPatientCount);
            System.out.println("✓ Dossiers pour ce médecin: " + byMedecinCount);

            // 13. TEST 10: Mise à jour
            System.out.println("\n=== Test 10: Mise à jour ===");
            UpdateDossierMedicalDTO updateDTO = UpdateDossierMedicalDTO.builder()
                    .dateDeCreation(LocalDate.now().minusDays(1))
                    .build();
            DossierMedicalDTO updated = dossierService.updateDossierMedical(createdDossier.getIdDM(), updateDTO);
            System.out.println("✓ Dossier mis à jour");
            System.out.println("  Nouvelle date: " + updated.getDateDeCreation());

            // 14. TEST 11: Dernière consultation
            System.out.println("\n=== Test 11: Dernière consultation ===");
            Long dossierId = createdDossier.getIdDM();
            System.out.println("Dossier ID choisi: " + dossierId);
            ConsultationDTO derniereConsultation = dossierService.getDerniereConsultation(dossierId);
            if (derniereConsultation != null) {
                System.out.println("✓ Dernière consultation trouvée: ID " + derniereConsultation.getIdConsultation());
                System.out.println("  Date: " + derniereConsultation.getDate());
                System.out.println("  Statut: " + derniereConsultation.getStatut());
            } else {
                System.out.println("✓ Aucune consultation trouvée pour ce dossier (normal si aucune consultation n'a été créée)");
            }

            // 15. TEST 12: Historique complet patient
            System.out.println("\n=== Test 12: Historique complet patient ===");
            List<ConsultationDTO> historique = dossierService.getHistoriqueCompletPatient(testPatient.getId_Patient());
            System.out.println("Historique des consultations pour le patient ID " + testPatient.getId_Patient() + ":");
            for (ConsultationDTO c : historique) {
                System.out.println("  - Consultation ID: " + c.getIdConsultation() + 
                    ", Date: " + c.getDate() + ", Statut: " + c.getStatut());
            }
            System.out.println("✓ Nombre de consultations dans l'historique: " + historique.size());

            // 16. TEST 13: Suppression
            System.out.println("\n=== Test 13: Suppression ===");
            try {
                Thread.sleep(20000); // Attendre 20 secondes que les processus en cours se terminent
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            dossierService.deleteDossierMedical(createdDossier.getIdDM());
            System.out.println("✓ Dossier supprimé");

            // Vérifier qu'il n'existe plus
            try {
                dossierService.getDossierMedicalById(createdDossier.getIdDM());
                System.out.println("✗ ERREUR: Le dossier devrait être supprimé");
            } catch (Exception e) {
                System.out.println("✓ Le dossier a bien été supprimé (exception attendue)");
            }

            System.out.println("\n=== TESTS TERMINÉS AVEC SUCCÈS ===");

        } catch (Exception e) {
            System.err.println("\n✗ ERREUR pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

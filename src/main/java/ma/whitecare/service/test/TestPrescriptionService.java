package ma.whitecare.service.test;

import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.entities.enums.FormeMedicament;
import ma.whitecare.mvc.dto.dossierMedical.PrescriptionDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreatePrescriptionDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdatePrescriptionDTO;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl;
import ma.whitecare.repository.modules.ordonnance.impl.OrdonnanceRepositoryImpl;
import ma.whitecare.repository.modules.medicament.impl.MedicamentRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl;
import ma.whitecare.service.modules.dossierMedical.impl.PrescriptionServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class TestPrescriptionService {

    public static void main(String[] args) {
        System.out.println("=== TEST DU PRESCRIPTION SERVICE ===\n");

        try {
            // 1. Initialisation des repositories et service
            PrescriptionRepository prescriptionRepo = new PrescriptionRepositoryImpl();
            OrdonnanceRepository ordonnanceRepo = new OrdonnanceRepositoryImpl();
            MedicamentRepository medicamentRepo = new MedicamentRepositoryImpl();
            ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
            DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
            PatientRepository patientRepo = new PatientRepositoryImpl();
            MedecinRepository medecinRepo = new MedecinRepositoryImpl();
            PrescriptionServiceImpl prescriptionService = new PrescriptionServiceImpl(prescriptionRepo, ordonnanceRepo, medicamentRepo, dossierRepo);

            // 2. Préparation: Créer les données de test nécessaires
            System.out.println("Préparation: Création des données de test");
            
            // Créer un patient
            Patient testPatient = new Patient();
            testPatient.setNom("Garcia");
            testPatient.setPrenom("Lucia");
            testPatient.setEmail("lucia.garcia.test@email.com");
            testPatient.setTelephone("0645678901");
            testPatient.setAdresse("321 Rue Test");
            testPatient.setDateNaissance(LocalDate.of(1988, 4, 12));
            testPatient.setSexe(Sexe.FEMME);
            testPatient.setAssurance(Assurance.CNSS);
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
            consultation.setStatut(StatutConsultation.TERMINEE);
            consultation.setObservationMedecin("Consultation terminée - prescription nécessaire");
            consultation.setDossierMedicale(dossier);
            consultation.setCreePar("system");
            consultation.setModifiePar("system");
            consultationRepo.create(consultation);
            System.out.println("✓ Consultation créée avec ID: " + consultation.getIdConsultation());

            // Créer une ordonnance
            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setDate(LocalDate.now());
            ordonnance.setConsultation(consultation);
            ordonnance.setDossierMedicale(dossier);
            ordonnance.setCreePar("system");
            ordonnance.setModifiePar("system");
            ordonnanceRepo.create(ordonnance);
            System.out.println("✓ Ordonnance créée avec ID: " + ordonnance.getIdOrd());

            // Créer un médicament
            Medicament medicament = new Medicament();
            medicament.setNom("Paracétamol 500mg");
            medicament.setLaboratoire("PharmaLab");
            medicament.setType("Analgésique");
            medicament.setForme(FormeMedicament.COMPRIME);
            medicament.setRemboursable(true);
            medicament.setPrixUnitaire(15.50);
            medicament.setDescription("Antalgique et antipyrétique");
            medicament.setCreePar("system");
            medicament.setModifiePar("system");
            medicamentRepo.create(medicament);
            System.out.println("✓ Médicament créé avec ID: " + medicament.getIdMct());
            System.out.println("  Nom: " + medicament.getNom());
            System.out.println("  Prix unitaire: " + medicament.getPrixUnitaire() + " MAD");

            // 3. TEST 1: Liste de toutes les prescriptions
            System.out.println("\n=== Test 1: Liste de toutes les prescriptions ===");
            List<PrescriptionDTO> allPrescriptions = prescriptionService.getAllPrescriptions();
            System.out.println("Liste des prescriptions:");
            for (PrescriptionDTO p : allPrescriptions) {
                System.out.println("  - Prescription ID: " + p.getIdPr() + 
                    ", Quantité: " + p.getQuantité() + 
                    ", Durée: " + p.getDuréeEnJours() + " jours" +
                    ", Fréquence: " + p.getFréquence());
            }
            System.out.println("✓ Nombre total de prescriptions: " + allPrescriptions.size());

            // 4. TEST 2: Création d'une prescription
            System.out.println("\n=== Test 2: Création d'une prescription ===");
            CreatePrescriptionDTO createDTO = CreatePrescriptionDTO.builder()
                    .ordonnanceId(ordonnance.getIdOrd())
                    .medicamentId(medicament.getIdMct())
                    .quantité(2)
                    .fréquence("2 fois par jour")
                    .duréeEnJours(7)
                    .build();

            PrescriptionDTO created = prescriptionService.createPrescription(createDTO);
            System.out.println("✓ Prescription créée avec ID: " + created.getIdPr());
            System.out.println("  Quantité: " + created.getQuantité());
            System.out.println("  Fréquence: " + created.getFréquence());
            System.out.println("  Durée: " + created.getDuréeEnJours() + " jours");

            // 5. TEST 3: Récupération par ID
            System.out.println("\n=== Test 3: Récupération par ID ===");
            PrescriptionDTO found = prescriptionService.getPrescriptionById(created.getIdPr());
            System.out.println("✓ Prescription trouvée: ID " + found.getIdPr());

            // 6. TEST 4: Recherche par ordonnance
            System.out.println("\n=== Test 4: Recherche par ordonnance ID ===");
            List<PrescriptionDTO> byOrdonnance = prescriptionService.findByOrdonnanceId(ordonnance.getIdOrd());
            System.out.println("Prescriptions pour ordonnance ID " + ordonnance.getIdOrd() + ":");
            for (PrescriptionDTO p : byOrdonnance) {
                System.out.println("  - Prescription ID: " + p.getIdPr() + 
                    ", Quantité: " + p.getQuantité() + 
                    ", Durée: " + p.getDuréeEnJours() + " jours");
            }
            System.out.println("✓ Prescriptions pour ordonnance ID " + ordonnance.getIdOrd() + ": " + byOrdonnance.size());

            // 7. TEST 5: Recherche par médicament
            System.out.println("\n=== Test 5: Recherche par médicament ID ===");
            List<PrescriptionDTO> byMedicament = prescriptionService.findByMedicamentId(medicament.getIdMct());
            System.out.println("Prescriptions pour médicament ID " + medicament.getIdMct() + ":");
            for (PrescriptionDTO p : byMedicament) {
                System.out.println("  - Prescription ID: " + p.getIdPr() + 
                    ", Quantité: " + p.getQuantité() + 
                    ", Durée: " + p.getDuréeEnJours() + " jours");
            }
            System.out.println("✓ Prescriptions pour médicament ID " + medicament.getIdMct() + ": " + byMedicament.size());

            // 8. TEST 6: Recherche par durée supérieure
            System.out.println("\n=== Test 6: Recherche par durée supérieure ===");
            List<PrescriptionDTO> byDuree = prescriptionService.findByDureeSuperieure(7);
            System.out.println("Prescriptions avec durée >= 7 jours:");
            for (PrescriptionDTO p : byDuree) {
                System.out.println("  - Prescription ID: " + p.getIdPr() + 
                    ", Durée: " + p.getDuréeEnJours() + " jours" +
                    ", Quantité: " + p.getQuantité());
            }
            System.out.println("✓ Prescriptions avec durée >= 7 jours: " + byDuree.size());

            // 9. TEST 7: Recherche par ordonnance et médicament
            System.out.println("\n=== Test 7: Recherche par ordonnance et médicament ===");
            List<PrescriptionDTO> byOrdonnanceAndMedicament = prescriptionService.findByOrdonnanceAndMedicament(
                ordonnance.getIdOrd(), medicament.getIdMct());
            System.out.println("Prescriptions pour ordonnance ID " + ordonnance.getIdOrd() + 
                " et médicament ID " + medicament.getIdMct() + ":");
            for (PrescriptionDTO p : byOrdonnanceAndMedicament) {
                System.out.println("  - Prescription ID: " + p.getIdPr() + 
                    ", Quantité: " + p.getQuantité() + 
                    ", Durée: " + p.getDuréeEnJours() + " jours");
            }
            System.out.println("✓ Prescriptions pour ordonnance et médicament: " + byOrdonnanceAndMedicament.size());

            // 10. TEST 8: Calcul du coût total d'une ordonnance
            System.out.println("\n=== Test 8: Calcul du coût total d'une ordonnance ===");
            Double total = prescriptionService.calculateCoutTotalOrdonnance(ordonnance.getIdOrd());
            System.out.println("✓ Coût total pour ordonnance ID " + ordonnance.getIdOrd() + ": " + total + " MAD");

            // 11. TEST 9: Statistiques
            System.out.println("\n=== Test 9: Statistiques ===");
            long totalCount = prescriptionService.countAllPrescriptions();
            long byOrdonnanceCount = prescriptionService.countByOrdonnanceId(ordonnance.getIdOrd());
            long byMedicamentCount = prescriptionService.countByMedicamentId(medicament.getIdMct());
            System.out.println("✓ Total prescriptions: " + totalCount);
            System.out.println("✓ Prescriptions pour cette ordonnance: " + byOrdonnanceCount);
            System.out.println("✓ Prescriptions pour ce médicament: " + byMedicamentCount);

            // 12. TEST 10: Vérification d'existence
            System.out.println("\n=== Test 10: Vérification d'existence ===");
            boolean exists = prescriptionService.existsById(created.getIdPr());
            System.out.println("✓ Prescription existe: " + exists);

            // 13. TEST 11: Mise à jour
            System.out.println("\n=== Test 11: Mise à jour ===");
            UpdatePrescriptionDTO updateDTO = UpdatePrescriptionDTO.builder()
                    .quantité(3)
                    .fréquence("3 fois par jour")
                    .duréeEnJours(10)
                    .build();
            PrescriptionDTO updated = prescriptionService.updatePrescription(created.getIdPr(), updateDTO);
            System.out.println("✓ Prescription mise à jour");
            System.out.println("  Nouvelle quantité: " + updated.getQuantité());
            System.out.println("  Nouvelle fréquence: " + updated.getFréquence());
            System.out.println("  Nouvelle durée: " + updated.getDuréeEnJours() + " jours");

            // 14. TEST 12: Prescriptions actives
            System.out.println("\n=== Test 12: Prescriptions actives ===");
            List<PrescriptionDTO> prescriptionsActives = prescriptionService.getPrescriptionsActives(testPatient.getId_Patient());
            System.out.println("Prescriptions actives pour patient ID " + testPatient.getId_Patient() + ":");
            for (PrescriptionDTO p : prescriptionsActives) {
                System.out.println("  - Prescription ID: " + p.getIdPr() + 
                    ", Quantité: " + p.getQuantité() + 
                    ", Durée: " + p.getDuréeEnJours() + " jours" +
                    ", Fréquence: " + p.getFréquence());
            }
            System.out.println("✓ Nombre de prescriptions actives: " + prescriptionsActives.size());

            // 15. TEST 13: Historique prescriptions
            System.out.println("\n=== Test 13: Historique prescriptions ===");
            List<PrescriptionDTO> historique = prescriptionService.getHistoriquePrescriptions(testPatient.getId_Patient());
            System.out.println("Historique des prescriptions pour patient ID " + testPatient.getId_Patient() + ":");
            for (PrescriptionDTO p : historique) {
                System.out.println("  - Prescription ID: " + p.getIdPr() + 
                    ", Quantité: " + p.getQuantité() + 
                    ", Durée: " + p.getDuréeEnJours() + " jours");
            }
            System.out.println("✓ Nombre de prescriptions dans l'historique: " + historique.size());

            // 16. TEST 14: Suppression
            System.out.println("\n=== Test 14: Suppression ===");
            try {
                Thread.sleep(20000); // Attendre 20 secondes que les processus en cours se terminent
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            prescriptionService.deletePrescription(created.getIdPr());
            System.out.println("✓ Prescription supprimée");

            // Vérifier qu'elle n'existe plus
            try {
                prescriptionService.getPrescriptionById(created.getIdPr());
                System.out.println("✗ ERREUR: La prescription devrait être supprimée");
            } catch (Exception e) {
                System.out.println("✓ La prescription a bien été supprimée (exception attendue)");
            }

            System.out.println("\n=== TESTS TERMINÉS AVEC SUCCÈS ===");

        } catch (Exception e) {
            System.err.println("\n✗ ERREUR pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

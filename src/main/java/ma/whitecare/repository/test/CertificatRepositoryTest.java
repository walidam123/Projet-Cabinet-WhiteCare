package ma.whitecare.repository.test;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.entities.medical.Certificat;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl;
import ma.whitecare.repository.modules.certificat.api.CertificatRepository;
import ma.whitecare.repository.modules.certificat.impl.CertificatRepositoryImpl;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class CertificatRepositoryTest {

    private static CertificatRepository certificatRepository = new CertificatRepositoryImpl();
    private static PatientRepository patientRepository = new PatientRepositoryImpl();
    private static MedecinRepository medecinRepository = new MedecinRepositoryImpl();

    // IDs des entités de test (seront créées et supprimées)
    private static final Long EXISTING_CABINET_ID = 1L; // Cabinet médical existant
    private static Long testPatientId;
    private static Long testMedecinId;
    private static Long testDossierMedicaleId;
    private static Long testConsultationId;
    private static Long testCertificatId;

    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS CERTIFICAT REPOSITORY (CRUD) ===\n");

        try {
            // Créer les dépendances nécessaires
            setupTestData();

            // Exécuter les tests CRUD
            testCreate();
            testFindById();
            testFindAll();
            testUpdate();
            testDelete();

            System.out.println("\n=== TOUS LES TESTS CRUD ONT REUSSI ===");

        } catch (Exception e) {
            System.err.println("❌ Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Nettoyer les données de test
            cleanupTestData();
        }
    }

    /**
     * Crée les entités nécessaires pour les tests (Patient, Médecin, DossierMedicale, Consultation)
     */
    private static void setupTestData() {
        System.out.println("=== SETUP: Création des données de test ===\n");

        try {
            // 1. Créer un Patient
            Patient patient = new Patient();
            patient.setNom("TEST_PATIENT");
            patient.setPrenom("Certificat");
            patient.setDateNaissance(LocalDate.of(1990, 5, 15));
            patient.setSexe(Sexe.HOMME);
            patient.setAdresse("123 Rue Test");
            patient.setTelephone("0612345678");
            patient.setEmail("test.patient@example.com");
            patient.setAssurance(Assurance.CNSS);
            patient.setCreePar("test_user");
            patient.setModifiePar("test_user");

            patientRepository.create(patient);
            testPatientId = patient.getId_Patient();
            System.out.println("✓ Patient créé avec ID: " + testPatientId);

            // 2. Créer un Médecin (nécessaire pour le dossier médical)
            // Utilise le CabinetMedicale existant avec ID = 1
            Medecin medecin = new Medecin();
            medecin.setNom("TEST_MEDECIN");
            medecin.setPrenom("Certificat");
            medecin.setMotDePass("test123");
            medecin.setEmail("test.medecin@example.com");
            medecin.setSexe(Sexe.HOMME);
            medecin.setLogin("medecin_cert_test");
            medecin.setActif(true);
            medecin.setSalaire(10000.0);
            medecin.setPrime(1000.0);
            medecin.setDateRecrutement(LocalDate.now());
            medecin.setSoldeConge(25);
            medecin.setSpecialite("Généraliste");
            medecin.setCabinetMedicaleId(EXISTING_CABINET_ID); // Utilise le cabinet existant
            medecin.setCreePar("test_user");
            medecin.setModifiePar("test_user");

            medecinRepository.create(medecin);
            testMedecinId = medecin.getIdUser();
            System.out.println("✓ Médecin créé avec ID: " + testMedecinId);
            System.out.println("✓ Médecin associé au Cabinet ID: " + EXISTING_CABINET_ID);

            // 3. Créer un DossierMedicale (via SQL direct car le repository n'est pas implémenté)
            testDossierMedicaleId = createDossierMedicale(testPatientId, testMedecinId);
            System.out.println("✓ Dossier médical créé avec ID: " + testDossierMedicaleId);

            // 4. Créer une Consultation (via SQL direct car le repository n'est pas implémenté)
            testConsultationId = createConsultation(testDossierMedicaleId);
            System.out.println("✓ Consultation créée avec ID: " + testConsultationId);

            System.out.println("\n=== SETUP TERMINÉ ===\n");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création des données de test: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible de créer les données de test", e);
        }
    }

    /**
     * Crée un DossierMedicale via SQL direct
     */
    private static Long createDossierMedicale(Long patientId, Long medecinId) throws SQLException {
        String sql = "INSERT INTO dossierMedicale (dateDecreation, patient_id, medecin_id, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setLong(2, patientId);
            ps.setLong(3, medecinId);
            ps.setString(4, "test_user");
            ps.setString(5, "test_user");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Impossible de récupérer l'ID du dossier médical créé");
    }

    /**
     * Crée une Consultation via SQL direct
     */
    private static Long createConsultation(Long dossierMedicaleId) throws SQLException {
        String sql = "INSERT INTO consultation (date, statut, observation_medecin, dossier_medicale_id, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setString(2, StatutConsultation.TERMINEE.name());
            ps.setString(3, "Consultation de test pour certificat");
            ps.setLong(4, dossierMedicaleId);
            ps.setString(5, "test_user");
            ps.setString(6, "test_user");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Impossible de récupérer l'ID de la consultation créée");
    }

    /**
     * Test CREATE - Création d'un certificat
     */
    private static void testCreate() {
        System.out.println("=== TEST CREATE CERTIFICAT ===");

        LocalDate dateDebut = LocalDate.now().plusDays(1);
        LocalDate dateFin = dateDebut.plusDays(15);

        // Créer les objets de relation
        DossierMedicale dossier = new DossierMedicale();
        dossier.setIdDM(testDossierMedicaleId);

        Consultation consultation = new Consultation();
        consultation.setIdConsultation(testConsultationId);

        // Créer le certificat
        Certificat certificat = Certificat.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .duree(15)
                .noteMedecin("Repos médical prescrit pour récupération complète - Test CRUD")
                .dossierMedicale(dossier)
                .consultation(consultation)
                .creePar("test_user")
                .modifiePar("test_user")
                .build();

        certificatRepository.create(certificat);
        testCertificatId = certificat.getIdCertif();

        assert testCertificatId != null : "L'ID du certificat devrait être généré";
        assert testCertificatId > 0 : "L'ID du certificat devrait être positif";

        System.out.println("✓ Certificat créé avec ID: " + testCertificatId);
        System.out.println("✓ Date début: " + certificat.getDateDebut());
        System.out.println("✓ Date fin: " + certificat.getDateFin());
        System.out.println("✓ Durée: " + certificat.getDuree() + " jours");
        System.out.println("✓ Dossier médical ID: " + testDossierMedicaleId);
        System.out.println("✓ Consultation ID: " + testConsultationId);
        System.out.println("✓ Test CREATE réussi\n");
    }

    /**
     * Test FIND BY ID - Recherche d'un certificat par son ID
     */
    private static void testFindById() {
        System.out.println("=== TEST FIND BY ID ===");

        Certificat found = certificatRepository.findById(testCertificatId);

        assert found != null : "Le certificat devrait être trouvé par ID";
        assert found.getIdCertif().equals(testCertificatId) : "L'ID devrait correspondre";
        assert found.getDateDebut() != null : "La date de début devrait être présente";
        assert found.getDuree() == 15 : "La durée devrait être 15 jours";

        System.out.println("✓ Certificat trouvé par ID: " + found.getIdCertif());
        System.out.println("✓ Date début: " + found.getDateDebut());
        System.out.println("✓ Date fin: " + found.getDateFin());
        System.out.println("✓ Durée: " + found.getDuree() + " jours");
        System.out.println("✓ Note médecin: " + found.getNoteMedecin());
        System.out.println("✓ Test FIND BY ID réussi\n");
    }

    /**
     * Test FIND ALL - Récupération de tous les certificats
     */
    private static void testFindAll() {
        System.out.println("=== TEST FIND ALL ===");

        List<Certificat> certificats = certificatRepository.findAll();

        assert certificats != null : "La liste ne devrait pas être null";
        assert !certificats.isEmpty() : "La liste devrait contenir au moins le certificat de test";

        System.out.println("📊 Nombre total de certificats dans la base : " + certificats.size());

        // Vérifier que notre certificat de test est dans la liste
        boolean found = certificats.stream()
                .anyMatch(c -> c.getIdCertif().equals(testCertificatId));
        assert found : "Le certificat de test devrait être dans la liste";

        System.out.println("✓ Certificat de test trouvé dans la liste");
        System.out.println("✓ Test FIND ALL réussi\n");
    }

    /**
     * Test UPDATE - Mise à jour d'un certificat
     */
    private static void testUpdate() {
        System.out.println("=== TEST UPDATE CERTIFICAT ===");

        // Récupérer le certificat existant
        Certificat certificat = certificatRepository.findById(testCertificatId);
        assert certificat != null : "Le certificat devrait exister avant la mise à jour";

        System.out.println("Certificat avant modification:");
        System.out.println("  - Date début: " + certificat.getDateDebut());
        System.out.println("  - Date fin: " + certificat.getDateFin());
        System.out.println("  - Durée: " + certificat.getDuree());
        System.out.println("  - Note: " + certificat.getNoteMedecin());

        // Modifier le certificat
        LocalDate nouvelleDateDebut = LocalDate.now().plusDays(5);
        LocalDate nouvelleDateFin = nouvelleDateDebut.plusDays(20);

        certificat.setDateDebut(nouvelleDateDebut);
        certificat.setDateFin(nouvelleDateFin);
        certificat.setDuree(20);
        certificat.setNoteMedecin("Note modifiée - Repos prolongé pour récupération complète - Test UPDATE");
        certificat.setModifiePar("update_test_user");

        certificatRepository.update(certificat);
        System.out.println("✅ Certificat modifié");

        // Vérifier les modifications
        Certificat updated = certificatRepository.findById(testCertificatId);
        assert updated != null : "Le certificat modifié devrait exister";
        assert updated.getDateDebut().equals(nouvelleDateDebut) : "La date de début devrait être modifiée";
        assert updated.getDateFin().equals(nouvelleDateFin) : "La date de fin devrait être modifiée";
        assert updated.getDuree() == 20 : "La durée devrait être modifiée à 20 jours";
        assert updated.getNoteMedecin().contains("Note modifiée") : "La note devrait être modifiée";

        System.out.println("Certificat après modification:");
        System.out.println("  - Date début: " + updated.getDateDebut());
        System.out.println("  - Date fin: " + updated.getDateFin());
        System.out.println("  - Durée: " + updated.getDuree() + " jours");
        System.out.println("  - Note: " + updated.getNoteMedecin());
        System.out.println("✓ Test UPDATE réussi\n");
    }

    /**
     * Test DELETE - Suppression d'un certificat
     */
    private static void testDelete() {
        System.out.println("=== TEST DELETE CERTIFICAT ===");

        // Vérifier que le certificat existe avant suppression
        Certificat beforeDelete = certificatRepository.findById(testCertificatId);
        assert beforeDelete != null : "Le certificat devrait exister avant la suppression";
        System.out.println("✓ Certificat existe avant suppression (ID: " + testCertificatId + ")");

        // Vérifier existsById
        boolean existsBefore = certificatRepository.existsById(testCertificatId);
        assert existsBefore : "existsById devrait retourner true avant suppression";
        System.out.println("✓ existsById retourne true avant suppression");

        // Supprimer le certificat
        certificatRepository.deleteById(testCertificatId);
        System.out.println("✅ Certificat supprimé");

        // Vérifier qu'il n'existe plus
        Certificat afterDelete = certificatRepository.findById(testCertificatId);
        assert afterDelete == null : "Le certificat ne devrait plus exister après la suppression";

        // Vérifier existsById après suppression
        boolean existsAfter = certificatRepository.existsById(testCertificatId);
        assert !existsAfter : "existsById devrait retourner false après suppression";

        System.out.println("✓ Certificat n'existe plus après suppression");
        System.out.println("✓ existsById retourne false pour le certificat supprimé");
        System.out.println("✓ Test DELETE réussi\n");
    }

    /**
     * Nettoie les données de test créées
     */
    private static void cleanupTestData() {
        System.out.println("\n=== CLEANUP: Suppression des données de test ===");

        try {
            // Supprimer dans l'ordre inverse des dépendances
            // 1. Certificat (déjà supprimé dans testDelete, mais on vérifie)
            if (testCertificatId != null) {
                try {
                    certificatRepository.deleteById(testCertificatId);
                    System.out.println("✓ Certificat de test supprimé");
                } catch (Exception e) {
                    // Peut déjà être supprimé
                }
            }

            // 2. Consultation
            if (testConsultationId != null) {
                deleteConsultation(testConsultationId);
                System.out.println("✓ Consultation de test supprimée");
            }

            // 3. Dossier médical
            if (testDossierMedicaleId != null) {
                deleteDossierMedicale(testDossierMedicaleId);
                System.out.println("✓ Dossier médical de test supprimé");
            }

            // 4. Médecin
            if (testMedecinId != null) {
                try {
                    medecinRepository.deleteById(testMedecinId);
                    System.out.println("✓ Médecin de test supprimé");
                } catch (Exception e) {
                    System.err.println("⚠ Erreur lors de la suppression du médecin: " + e.getMessage());
                }
            }

            // 5. Patient
            if (testPatientId != null) {
                try {
                    patientRepository.deleteById(testPatientId);
                    System.out.println("✓ Patient de test supprimé");
                } catch (Exception e) {
                    System.err.println("⚠ Erreur lors de la suppression du patient: " + e.getMessage());
                }
            }

            System.out.println("✓ Cleanup terminé\n");

        } catch (Exception e) {
            System.err.println("⚠ Erreur lors du cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprime une consultation via SQL direct
     */
    private static void deleteConsultation(Long consultationId) {
        String sql = "DELETE FROM consultation WHERE id_consultation = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠ Erreur lors de la suppression de la consultation: " + e.getMessage());
        }
    }

    /**
     * Supprime un dossier médical via SQL direct
     */
    private static void deleteDossierMedicale(Long dossierId) {
        String sql = "DELETE FROM dossierMedicale WHERE idDM = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠ Erreur lors de la suppression du dossier médical: " + e.getMessage());
        }
    }
}

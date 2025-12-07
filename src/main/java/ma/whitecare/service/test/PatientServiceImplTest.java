package ma.whitecare.service.test;

import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.whitecare.service.modules.patient.api.PatientService;
import ma.whitecare.service.modules.patient.impl.PatientServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class PatientServiceImplTest {

    private static PatientRepository patientRepository = new PatientRepositoryImpl();
    private static PatientService patientService = new PatientServiceImpl(patientRepository);

    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS PATIENT SERVICE ===\n");

        try {
            // Création
            Patient created = testCreatePatient();

            // Lecture
            testGetAllPatients();
            testGetPatientById(created.getId_Patient());

            // Update
            testUpdatePatient(created.getId_Patient());

            // Recherches
            testFindByEmail(created.getEmail());
            testFindByTelephone(created.getTelephone());
            testFindByAssurance("CNSS");
            testFindBySexe("HOMME");
            testFindByDateNaissanceBetween();

            // Suppression
            testDeletePatientById(created.getId_Patient());

            System.out.println("\n=== TOUS LES TESTS ONT REUSSI ===");
        } catch (Exception e) {
            System.err.println("❌ Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Patient testCreatePatient() {
        System.out.println("=== TEST CREATE PATIENT ===");
        Patient patient = new Patient();
        patient.setNom("TEST");
        patient.setPrenom("Service");
        patient.setDateNaissance(LocalDate.of(1990, 1, 1));
        patient.setSexe(Sexe.HOMME);
        patient.setAdresse("Adresse test");
        patient.setTelephone("0611111111");
        patient.setEmail("service@test.com");
        patient.setAssurance(Assurance.CNSS);
        patient.setCreePar("test_user");
        patient.setModifiePar("test_user");

        patientService.createPatient(patient);
        System.out.println("✓ Patient créé avec ID: " + patient.getId_Patient());
        return patient;
    }

    private static void testGetAllPatients() {
        System.out.println("=== TEST GET ALL PATIENTS ===");
        List<Patient> patients = patientService.getAllPatients();
        System.out.println("📊 Nombre total de patients: " + patients.size());
        patients.forEach(p -> System.out.println(" - " + p));
    }

    private static void testGetPatientById(Long id) {
        System.out.println("=== TEST GET PATIENT BY ID ===");
        try {
            Patient patient = patientService.getPatientById(id);
            System.out.println("✓ Patient trouvé: " + patient);
        } catch (RuntimeException e) {
            System.out.println("⚠️ Patient avec ID " + id + " non trouvé");
        }
    }

    private static void testUpdatePatient(Long id) {
        System.out.println("=== TEST UPDATE PATIENT ===");
        Patient patient = patientService.getPatientById(id);
        if (patient != null) {
            patient.setNom("MODIFIE");
            patientService.updatePatient(patient);
            System.out.println("✓ Patient modifié: " + patient);
        }
    }

    private static void testDeletePatientById(Long id) {
        System.out.println("=== TEST DELETE PATIENT BY ID ===");
        try {
            patientService.deletePatientById(id);
            System.out.println("✓ Patient supprimé avec ID: " + id);
        } catch (Exception e) {
            System.out.println("⚠️ Impossible de supprimer patient avec ID " + id);
        }
    }

    private static void testFindByEmail(String email) {
        System.out.println("=== TEST FIND BY EMAIL ===");
        List<Patient> patients = patientService.findByEmail(email);
        patients.forEach(p -> System.out.println("✓ " + p));
    }

    private static void testFindByTelephone(String tel) {
        System.out.println("=== TEST FIND BY TELEPHONE ===");
        List<Patient> patients = patientService.findByTelephone(tel);
        patients.forEach(p -> System.out.println("✓ " + p));
    }

    private static void testFindByAssurance(String assurance) {
        System.out.println("=== TEST FIND BY ASSURANCE ===");
        List<Patient> patients = patientService.findByAssurance(assurance);
        patients.forEach(p -> System.out.println("✓ " + p));
    }

    private static void testFindBySexe(String sexe) {
        System.out.println("=== TEST FIND BY SEXE ===");
        List<Patient> patients = patientService.findBySexe(sexe);
        patients.forEach(p -> System.out.println("✓ " + p));
    }

    private static void testFindByDateNaissanceBetween() {
        System.out.println("=== TEST FIND BY DATE NAISSANCE BETWEEN ===");
        List<Patient> patients = patientService.findByDateNaissanceBetween(
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2000, 12, 31)
        );
        patients.forEach(p -> System.out.println("✓ " + p));
    }
}

package ma.whitecare.service.test;

import ma.whitecare.common.exceptions.PatientNotFoundException;
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

    private static final PatientRepository patientRepository = new PatientRepositoryImpl();
    private static final PatientService patientService = new PatientServiceImpl(patientRepository);

    public static void main(String[] args) {
        System.out.println("=== DÉBUT DES TESTS PATIENT SERVICE ===\n");

        try {
            Patient created = testCreatePatient();
            testGetAllPatients();
            testGetPatientById(created.getId_Patient());
            testUpdatePatient(created.getId_Patient());
            testFindByEmail(created.getEmail());
            testFindByTelephone(created.getTelephone());
            testFindByAssurance(created.getAssurance().name());
            testFindBySexe(created.getSexe().name());
            testFindByDateNaissanceBetween();
            testDeletePatientById(created.getId_Patient());

            // Test exception
            testPatientNotFound();

            System.out.println("\n=== TOUS LES TESTS ONT RÉUSSI ===");
        } catch (Exception e) {
            System.err.println("❌ Erreur pendant les tests : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Patient testCreatePatient() {
        System.out.println("=== TEST CREATE PATIENT ===");
        Patient patient = new Patient();
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setDateNaissance(LocalDate.of(1990, 5, 20));
        patient.setSexe(Sexe.HOMME);
        patient.setAdresse("Rabat, Maroc");
        patient.setTelephone("0612345678");
        patient.setEmail("jean.dupont@test.com");
        patient.setAssurance(Assurance.CNOPS);
        patient.setCreePar("test_user");
        patient.setModifiePar("test_user");

        patientService.createPatient(patient);
        System.out.println("✓ Patient créé avec ID : " + patient.getId_Patient());
        return patient;
    }

    private static void testGetAllPatients() {
        System.out.println("=== TEST GET ALL PATIENTS ===");
        List<Patient> patients = patientService.getAllPatients();
        System.out.println("📊 Nombre total de patients : " + patients.size());
        patients.forEach(p -> System.out.println(" - " + p));
    }

    private static void testGetPatientById(Long id) {
        System.out.println("=== TEST GET PATIENT BY ID ===");
        Patient patient = patientService.getPatientById(id);
        System.out.println("✓ Patient trouvé : " + patient);
    }

    private static void testUpdatePatient(Long id) {
        System.out.println("=== TEST UPDATE PATIENT ===");
        Patient patient = patientService.getPatientById(id);
        patient.setNom("Dupont modifié");
        patientService.updatePatient(patient);
        System.out.println("✓ Patient modifié : " + patient);
    }

    private static void testDeletePatientById(Long id) {
        System.out.println("=== TEST DELETE PATIENT BY ID ===");
        patientService.deletePatientById(id);
        System.out.println("✓ Patient supprimé avec ID : " + id);
    }

    private static void testFindByEmail(String email) {
        System.out.println("=== TEST FIND BY EMAIL ===");
        List<Patient> patients = patientService.findByEmail(email);
        patients.forEach(p -> System.out.println("✓ " + p));
    }

    private static void testFindByTelephone(String telephone) {
        System.out.println("=== TEST FIND BY TELEPHONE ===");
        List<Patient> patients = patientService.findByTelephone(telephone);
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

    private static void testPatientNotFound() {
        System.out.println("=== TEST PATIENT NOT FOUND EXCEPTION ===");
        try {
            patientService.getPatientById(-1L); // ID inexistant
        } catch (PatientNotFoundException e) {
            System.out.println("✓ Exception bien lancée : " + e.getMessage());
        }
    }
}

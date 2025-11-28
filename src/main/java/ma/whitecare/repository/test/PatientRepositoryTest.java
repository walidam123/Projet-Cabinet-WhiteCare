package ma.whitecare.repository.test;

import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;

import ma.whitecare.entities.enums.Assurance;
import ma.whitecare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.whitecare.repository.modules.patient.api.PatientRepository;

import java.time.LocalDate;
import java.util.List;

public class PatientRepositoryTest {

    private static PatientRepository patientRepository = new PatientRepositoryImpl();

    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS PATIENT REPOSITORY ===\n");

        try {
            //testCreatePatient();
            //testFindById();
            //testFindAll();
            //testFindByEmail();
            //testFindByTelephone();
            //testSearchByNomPrenom();
            //testFindByAssurance();
            //testFindByDateNaissanceBetween();
            //testCount();
            //testAddAntecedentToPatient();
            //testGetAntecedentsOfPatient();
            //testGetPatientsByAntecedent();
            //testRemoveAntecedentFromPatient();
            //removeAllAntecedentsFromPatient();
            //testFindPageSimple();
            //testUpdatePatientSimple();
            System.out.println("\n=== TOUS LES TESTS ONT REUSSI ===");

        } catch (Exception e) {
            System.err.println("Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testCreatePatient() {
        System.out.println("=== TEST CREATION PATIENT ===");

        Patient patient = new Patient();
        patient.setNom("EL FILALI");
        patient.setPrenom("Mohamed");
        patient.setDateNaissance(LocalDate.of(1985, 5, 15));
        patient.setSexe(Sexe.HOMME);
        patient.setAdresse("123 Rue Casablanca");
        patient.setTelephone("0612345678");
        patient.setEmail("mohamed.elfilali@email.com");
        patient.setAssurance(Assurance.CNSS);
        patient.setCreePar("test_user");
        patient.setModifiePar("test_user");

        patientRepository.create(patient);

        assert patient.getId_Patient() != null : "L'ID du patient devrait être généré";
        assert patient.getId_Patient() > 0 : "L'ID du patient devrait être positif";

        System.out.println("✓ Patient créé avec ID: " + patient.getId_Patient());
        System.out.println("✓ Test création réussi\n");
    }

    private static void testFindById() {
        System.out.println("=== TEST FIND BY ID ===");

        // Créer un patient pour le test
        //Patient testPatient = createTestPatient("TestFindById", "0698765432", "findbyid@test.com");
        //patientRepository.create(testPatient);
        Long ID_test = 4L;
        Patient found = patientRepository.findById(ID_test);

        //assert found != null : "Le patient devrait être trouvé par ID";
        //assert found.getNom().equals("TestFindById") : "Le nom devrait correspondre";
        //assert found.getTelephone().equals("0698765432") : "Le téléphone devrait correspondre";
        if (found != null) {
            System.out.println("✓ Patient trouvé par ID: " + found.getNom());
            System.out.println("✓ Prenom " + found.getPrenom());
            System.out.println("✓ Test findById réussi\n");
        } else {
            System.out.println("Patient de ID: " + ID_test + " n'existe pas");

        }

    }

    private static Patient createTestPatient(String nom, String Prenom, String telephone) {
        Patient patient = new Patient();
        patient.setNom(nom);
        patient.setPrenom(Prenom);
        patient.setDateNaissance(LocalDate.of(1980 + (int) (Math.random() * 30),
                (int) (Math.random() * 12) + 1,
                (int) (Math.random() * 28) + 1));
        patient.setSexe(Math.random() > 0.5 ? Sexe.HOMME : Sexe.FEMME);
        patient.setAdresse("Adresse test pour " + nom);
        patient.setTelephone(telephone);
        patient.setEmail(nom + Prenom + "@email.com");
        patient.setAssurance(Assurance.values()[(int) (Math.random() * Assurance.values().length)]);
        patient.setCreePar("test_user");
        patient.setModifiePar("test_user");
        return patient;
    }

    private static void testFindAll() {
        System.out.println("=== TEST FIND ALL AVEC TOSTRING() ===");

        try {
            // Récupérer tous les patients
            List<Patient> patients = patientRepository.findAll();

            System.out.println("📊 Nombre total de patients dans la base : " + patients.size());

            if (patients.isEmpty()) {
                System.out.println("ℹ️ Aucun patient trouvé dans la base de données");
                return;
            }

            System.out.println("\n🎯 LISTE DES PATIENTS (via toString()):");
            System.out.println("═".repeat(50));

            // Afficher chaque patient avec toString()
            for (int i = 0; i < patients.size(); i++) {
                Patient patient = patients.get(i);
                System.out.println((i + 1) + ". " + patient.toString());
            }

            System.out.println("═".repeat(50));


        } catch (Exception e) {
            System.err.println("❌ Erreur pendant testFindAll: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testFindByEmail() {
        System.out.println("=== TEST FIND BY EMAIL ===");

        String testEmail = "unique.email@test.com";
        //Patient testPatient = createTestPatient("TestEmail", "0611111111", testEmail);
        //patientRepository.create(testPatient);

        List<Patient> found = patientRepository.findByEmail(testEmail);
        if (found.isEmpty()) {
            System.out.println("aucun patient avec email:  " + testEmail);
        } else {
            System.out.println("Les Patients avec email " + testEmail + " sont");

            for (int i = 0; i < found.size(); i++) {
                Patient patient = found.get(i);
                System.out.println((i + 1) + ". " + patient.getNom());
            }
        }


    }

    private static void testFindByTelephone() {
        System.out.println("=== TEST FIND BY Telephone ===");

        String testTele = "0698765432";


        List<Patient> found = patientRepository.findByTelephone(testTele);
        if (found.isEmpty()) {
            System.out.println("aucun patient avec Tele:  " + testTele);
        } else {
            System.out.println("Les Patients avec Tele " + testTele + " sont");

            for (int i = 0; i < found.size(); i++) {
                Patient patient = found.get(i);
                System.out.println((i + 1) + ". " + patient.getNom());
            }
        }
    }

    private static void testSearchByNomPrenom() {
        System.out.println("=== TEST FIND BY Nom/Prenom===");


        // Créer quelques patients de test
        //Patient testPatient=createTestPatient("DUPONT", "Pierre", "0601010101");patientRepository.create(testPatient);
        //Patient testPatient1=createTestPatient("DURAND", "Marie", "0602020202");patientRepository.create(testPatient1);
        //Patient testPatient2=createTestPatient("MARTIN", "Jean", "0603030303");patientRepository.create(testPatient2);
        //Patient testPatient3=createTestPatient("DUBOIS", "Paul", "0604040404");//patientRepository.create(testPatient3);

        // Tests de recherche
        System.out.println("\n🔍 Recherche 'DUP' (doit trouver DUPONT)");
        patientRepository.searchByNomPrenom("DUP").forEach(p ->
                System.out.println("   ✅ " + p.getNom() + " " + p.getPrenom()));

        System.out.println("\n🔍 Recherche 'mar' (doit trouver MARIE et MARTIN)");
        patientRepository.searchByNomPrenom("mar").forEach(p ->
                System.out.println("   ✅ " + p.getNom() + " " + p.getPrenom()));

        System.out.println("\n🔍 Recherche 'p' (doit trouver Pierre et Paul)");
        patientRepository.searchByNomPrenom("p").forEach(p ->
                System.out.println("   ✅ " + p.getNom() + " " + p.getPrenom()));
    }


    private static void testFindByAssurance() {
        System.out.println("=== TEST FIND BY Assu ===");

        String testass = "CNSS";


        List<Patient> found = patientRepository.findByAssurance(testass);
        if (found.isEmpty()) {
            System.out.println("aucun patient avec :  " + testass);
        } else {
            System.out.println("Les Patients avec  " + testass + " sont");

            for (int i = 0; i < found.size(); i++) {
                Patient patient = found.get(i);
                System.out.println((i + 1) + ". " + patient.getNom() + " " + patient.getPrenom());
            }
        }
    }


    private static void testFindByDateNaissanceBetween() {

        System.out.println("=== TEST FIND BY Date===");
        System.out.println("\n📅 testFindByDateNaissanceBetween");
        List<Patient> found = patientRepository.findByDateNaissanceBetween(
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2000, 12, 31)
        );
        System.out.println("   → " + found.size() + " patient(s) né(s) entre 1990-2000");

        for (int i = 0; i < found.size(); i++) {
            Patient patient = found.get(i);
            System.out.println((i + 1) + ". " + patient.getNom() + " " + patient.getPrenom());
        }
    }


    private static void testCount() {
        System.out.println("=== TEST FIND BY Count  ===");
        System.out.println("\n📊 testCount");
        long count = patientRepository.count();
        long countBySexe = patientRepository.countBySexe("HOMME");
        long countByAssurance = patientRepository.countByAssurance("CNSS");

        System.out.println("   → " + count + " patient(s) au total");
        System.out.println("   → " + countBySexe + " patient(s) sexe: Homme");
        System.out.println("   → " + countByAssurance + " patient(s) Assurance:CNSS");
    }

    private static void testAddAntecedentToPatient() {
        Long patientId = 2L;
        Long antecedentId = 3L;
        System.out.println("\n➕ testAddAntecedentToPatient");
        patientRepository.addAntecedentToPatient(patientId, antecedentId);
        System.out.println("   ✅ Antécédent " + antecedentId + " ajouté au patient " + patientId);
    }


    private static void testGetAntecedentsOfPatient() {
        Long patientId = 2L;
        System.out.println("\n📋 testGetAntecedentsOfPatient");
        List<Antecedents> antecedents = patientRepository.getAntecedentsOfPatient(patientId);
        System.out.println("   → " + antecedents.size() + " antécédent(s) pour le patient:" + patientId);
        antecedents.forEach(a -> System.out.println("      - " + a.getNom() + " (" + a.getCategorie() + ")"));
    }


    private static void testGetPatientsByAntecedent() {
        Long antecedentId = 1L;
        System.out.println("\n👥 testGetPatientsByAntecedent");
        List<Patient> patients = patientRepository.getPatientsByAntecedent(antecedentId);
        System.out.println("   → " + patients.size() + " patient(s) avec cet antécédent");
        patients.forEach(p -> System.out.println("      - " + p.getNom() + " " + p.getPrenom()));
    }

    private static void testRemoveAntecedentFromPatient() {
        Long patientId = 2L;
        Long antecedentId = 1L;
        System.out.println("\n➖ testRemoveAntecedentFromPatient");
        patientRepository.removeAntecedentFromPatient(patientId, antecedentId);
        System.out.println("   ✅ Antécédent " + antecedentId + " retiré du patient " + patientId);

        // Vérifier
        List<Antecedents> afterRemove = patientRepository.getAntecedentsOfPatient(patientId);
        System.out.println("   → " + afterRemove.size() + " antécédent(s) après retrait");
    }

    private static void removeAllAntecedentsFromPatient() {
        Long patientId = 2L;

        System.out.println("\n➖ testRemoveAllAntecedentFromPatient");
        // Vérifier avant
        List<Antecedents> BeforeRemove = patientRepository.getAntecedentsOfPatient(patientId);
        System.out.println("   → " + BeforeRemove.size() + " antécédent(s) avant retrait");

        patientRepository.removeAllAntecedentsFromPatient(patientId);


        // Vérifier apres
        List<Antecedents> afterRemove = patientRepository.getAntecedentsOfPatient(patientId);
        System.out.println("   → " + afterRemove.size() + " antécédent(s) après retrait");
    }

    private static void testFindPageSimple() {
        System.out.println("📄 TEST findPage SIMPLE");

        // Test page 1
        List<Patient> page1 = patientRepository.findPage(3, 0);
        System.out.println("→ Page 1 (limit 3, offset 0): " + page1.size() + " patient(s)");
        System.out.println("\n🎯 LISTE DES PATIENTS (via toString()):");
        System.out.println("═".repeat(50));

        // Afficher chaque patient avec toString()
        for (int i = 0; i < page1.size(); i++) {
            Patient patient = page1.get(i);
            System.out.println((i + 1) + ". " + patient.toString());
        }

        System.out.println("═".repeat(50));

        // Test page 1
        List<Patient> page2 = patientRepository.findPage(3, 3);
        System.out.println("→ Page 2 (limit 3, offset 3): " + page2.size() + " patient(s)");
        System.out.println("\n🎯 LISTE DES PATIENTS (via toString()):");
        System.out.println("═".repeat(50));

        // Afficher chaque patient avec toString()
        for (int i = 0; i < page2.size(); i++) {
            Patient patient = page2.get(i);
            System.out.println((i + 1) + ". " + patient.toString());
        }
    }


    private static void testUpdatePatientSimple() {
        System.out.println("✏️ TEST UPDATE PATIENT SIMPLE");

        // 1. Créer un patient pour le test
        Patient patient = Patient.builder()
                .nom("AVANT_MODIF")
                .prenom("Test")
                .telephone("0644444444")
                .email("avant@test.com")
                .assurance(Assurance.CNSS)
                .adresse("Ancienne adresse")
                .creePar("test")
                .modifiePar("test")
                .build();

        //patientRepository.create(patient);
        System.out.println("Patient Cree:");
        System.out.println(patient.toString());
        String patientNomC = "APRES_MODIF Modifié";
        // 2. Modifier le patient
        patient.setId_Patient(7L);
        patient.setNom("APRES_MODIF");
        patient.setPrenom("Modifié");
        patient.setSexe(Sexe.FEMME);
        patient.setTelephone("0655555555");
        patient.setEmail("apres@test.com");
        patient.setAssurance(Assurance.CNOPS);
        patient.setAdresse("Nouvelle adresse");
        patient.setModifiePar("update_test");
        patientRepository.update(patient);
        System.out.println("✅ Patient modifié");


    }
}












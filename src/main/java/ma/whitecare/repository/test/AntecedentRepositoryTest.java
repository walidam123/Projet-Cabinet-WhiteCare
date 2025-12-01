package ma.whitecare.repository.test;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.repository.modules.patient.api.AntecedentRepository;
import ma.whitecare.repository.modules.patient.impl.AntecedentRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryTest {

    private static AntecedentRepository antecedentRepository = new AntecedentRepositoryImpl();

    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS Antecedent REPOSITORY ===\n");

        try {

            //testFindAll();
            //testCreateAntecedent();
            //testupdateAntecedent();
            //testdeleteByIdAntecedent();

            //testFindById();
            //testFindByNom();
            //testfindByCategorie();

            //findByNiveauRisque();
            //testcount();
            //testFindPageSimple();
            //testgetPatientsHavingAntecedent();
            //testremoveAntecedentFromAllPatients();
            System.out.println("\n=== TOUS LES TESTS ONT REUSSI ===");

        } catch (Exception e) {
            System.err.println("Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static Antecedents createtestAntecedent(String Nom, String Categorie) {
        // Créer un antécédent de test
        Antecedents antecedent = new Antecedents();
        antecedent.setNom(Nom + "_test");
        antecedent.setCategorie(Categorie + "_test");
        antecedent.setNiveauDeRisque(Math.random() > 0.5 ? NiveauDeRisque.FAIBLE : NiveauDeRisque.ELEVE);
        antecedent.setCreePar("user_test");
        antecedent.setModifiePar("user_test");
        return antecedent;
    }

    private static void testFindAll() {
        System.out.println("\n📋 testFindAll");
        List<Antecedents> antecedents = antecedentRepository.findAll();
        System.out.println("→ " + antecedents.size() + " antécédent(s) trouvé(s)");
        System.out.println("\n🎯 LISTE DES Antecedents (via toString()):");
        System.out.println("*".repeat(50));

        // Afficher chaque patient avec toString()
        for (int i = 0; i < antecedents.size(); i++) {
            Antecedents antecedent = antecedents.get(i);
            System.out.println((i + 1) + ". " + antecedent.toString());
        }
        System.out.println("*".repeat(50));
    }


    private static void testCreateAntecedent() {
        Antecedents antecedent = createtestAntecedent("antec1","cat1");
        antecedentRepository.create(antecedent);
        String NomAntecedent = antecedent.getNom();
        System.out.println("🎯 Antécédent test créé - Nom: " + NomAntecedent);

        ;
    }


    private static void testFindById() {
        System.out.println("\n🔍 testFindById");
       Long  testAntecedentId=5l;
        Antecedents antecedent = antecedentRepository.findById(testAntecedentId);
        if (antecedent != null) {
            System.out.println(antecedent.toString());
        } else {
            System.out.println("❌ Non trouvé");
        }
    }

    private static void testFindByNom() {
        System.out.println("\n🔎 testFindByNom");
        Optional<Antecedents> result = antecedentRepository.findByNom("Arthrose");
        if (result.isPresent()) {
            System.out.println(result.toString());
        } else {
            System.out.println("❌ Non trouvé");
        }
    }
    private static void testfindByCategorie() {
        System.out.println("\n📁 testFindByCategorie");
        List<Antecedents> resultats = antecedentRepository.findByCategorie("Métabolique");
        System.out.println("→ " + resultats.size() + " antécédent(s) dans la catégorie 'Métabolique'");
        resultats.forEach(a -> System.out.println(a.toString()));
    }
    private static void findByNiveauRisque() {
        System.out.println("\n⚠️ testFindByNiveauRisque");
        List<Antecedents> resultats = antecedentRepository.findByNiveauRisque(NiveauDeRisque.FAIBLE);
        System.out.println("→ " + resultats.size() + " antécédent(s) avec risque FAIBLE");
        resultats.forEach(a -> System.out.println("   - " + a.getNom()));
    }
    private static void testcount(){
        System.out.println("\n📊 testCount");
        long count = antecedentRepository.count();
        System.out.println("→ " + count + " antécédent(s) au total");

    }
    private static void testFindPageSimple() {

            System.out.println("\n📄 testFindPage");
            List<Antecedents> page = antecedentRepository.findPage(2, 0);
            System.out.println("→ Page 1: " + page.size() + " antécédent(s)");
            page.forEach(a -> System.out.println("   - " + a.getNom()));

    }


    private static void testgetPatientsHavingAntecedent(){
        System.out.println("\n👥 testGetPatientsHavingAntecedent");
        Long testAntecedentId=2l;
        List<Patient> patients = antecedentRepository.getPatientsHavingAntecedent(testAntecedentId);
        System.out.println("→ " + patients.size() + " patient(s) avec cet antécédent");
        patients.forEach(p -> System.out.println("   - " + p.getNom() + " " + p.getPrenom()));

    }
    private static void testupdateAntecedent() {
        System.out.println("\n✏️ testUpdate");
        Long testAntecedentId=11l;
        Antecedents antecedent = antecedentRepository.findById(testAntecedentId);
        String ancienNom = antecedent.getNom();
        antecedent.setNom("antec1_test Modif");
        antecedent.setNiveauDeRisque(NiveauDeRisque.MOYEN);
        antecedent.setModifiePar("test_update");

        antecedentRepository.update(antecedent);

        Antecedents updated = antecedentRepository.findById(testAntecedentId);
        System.out.println("→ " + ancienNom + " → " + updated.getNom());
        System.out.println("→ Niveau risque: " + updated.getNiveauDeRisque());

        ;
    }

    private static void testdeleteByIdAntecedent() {

        System.out.println("\n🗑️ testDelete");

        // Créer un antécédent temporaire à supprimer
        Antecedents temp = createtestAntecedent("test_delete2", "test_cat_delete");
        antecedentRepository.create(temp);

        // Recuperationdu ID un antécédent temporaire
        Optional<Antecedents> result_delete = antecedentRepository.findByNom(temp.getNom());
        if (result_delete.isPresent()) {
            System.out.println(result_delete.toString());
        } else {
            System.out.println("❌ Non trouvé");
        }
        Long tempId = result_delete.get().getId_Antecedent();

        try {
            // 2. Attendre 8 secondes
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Supprimer et vérifier
        antecedentRepository.deleteById(tempId);
        boolean existsAfter = antecedentRepository.existsById(tempId);
        System.out.println("→ Antécédent supprimé, existe après: " + existsAfter);
    }

    public static void  testremoveAntecedentFromAllPatients() {

        System.out.println("\n🧹 testremoveAntecedentFromAllPatients");
        Long testAntecedentId=3l;//Deja lier antecedent 3 avec 2 Patient a partir de patientRepositoryTest.testAddAntecedentToPatient()
        List<Patient> patients = antecedentRepository.getPatientsHavingAntecedent(testAntecedentId);
        System.out.println("→ " + patients.size() + " patient(s) avec cet antécédent");
        patients.forEach(p -> System.out.println("   - " + p.getNom() + " " + p.getPrenom()));
        antecedentRepository.removeAntecedentFromAllPatients(testAntecedentId);
        //Apres le nettoyage
        List<Patient> patients_apres = antecedentRepository.getPatientsHavingAntecedent(testAntecedentId);
        System.out.println("→ " + patients_apres.size() + " patient(s) avec cet antécédent");
        patients_apres.forEach(p -> System.out.println("   - " + p.getNom() + " " + p.getPrenom()));

    }


}

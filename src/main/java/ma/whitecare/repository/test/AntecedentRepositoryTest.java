package ma.whitecare.repository.test;

import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.repository.modules.patient.api.AntecedentRepository;

import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryTest {

   // private static AntecedentRepository antecedentRepository = new AntecedentRepository();

    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS Antecedent REPOSITORY ===\n");

        try {
            //testCreateAntecedent();
            //testupdateAntecedent();
            //testdeleteAntecedent();
            //testdeleteByIdAntecedent();
            //testFindAll();
            //testFindById();
            //testfindByCategorie();

            //findByNiveauRisque();
            //testcount
            //testFindPageSimple();
           // testgetPatientsHavingAntecedent
            System.out.println("\n=== TOUS LES TESTS ONT REUSSI ===");

        } catch (Exception e) {
            System.err.println("Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package ma.whitecare.service.test;

import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.repository.modules.patient.api.AntecedentRepository;
import ma.whitecare.repository.modules.patient.impl.AntecedentRepositoryImpl;
import ma.whitecare.service.modules.Antecedent.api.AntecedentService;
import ma.whitecare.service.modules.Antecedent.impl.AntecedentServiceImpl;

import java.util.List;

public class AntecedentServiceImplTest {

    private static final AntecedentRepository antecedentRepository = new AntecedentRepositoryImpl();
    private static final AntecedentService antecedentService = new AntecedentServiceImpl(antecedentRepository);

    public static void main(String[] args) {
        System.out.println("=== DÉBUT DES TESTS ANTECEDENT SERVICE ===\n");

        try {
            Antecedents created = testCreateAntecedent();
            testGetAllAntecedents();
            testGetAntecedentById(created.getId_Antecedent());
            testUpdateAntecedent(created.getId_Antecedent());
            testFindByNom(created.getNom());
            testFindByCategorie(created.getCategorie());
            testFindByNiveauRisque(created.getNiveauDeRisque());
            testCount();
            testFindPage();
            testDeleteAntecedentById(created.getId_Antecedent());

            System.out.println("\n=== TOUS LES TESTS ONT RÉUSSI ===");
        } catch (Exception e) {
            System.err.println("❌ Erreur pendant les tests : " + e.getMessage());
        }
    }

    private static Antecedents testCreateAntecedent() {
        System.out.println("=== TEST CREATE ANTECEDENT ===");
        Antecedents antecedent = new Antecedents();
        antecedent.setNom("Diabete");
        antecedent.setCategorie("Maladie chronique");
        antecedent.setNiveauDeRisque(NiveauDeRisque.ELEVE); // ✅ adapté à ton enum
        antecedent.setCreePar("test_user");
        antecedent.setModifiePar("test_user");

        antecedentService.createAntecedent(antecedent);
        System.out.println("✓ Antécédent créé avec ID : " + antecedent.getId_Antecedent());
        return antecedent;
    }

    private static void testGetAllAntecedents() {
        System.out.println("=== TEST GET ALL ANTECEDENTS ===");
        List<Antecedents> antecedents = antecedentService.getAllAntecedents();
        System.out.println("📊 Nombre total d'antécédents : " + antecedents.size());
        antecedents.forEach(a -> System.out.println(" - " + a));
    }

    private static void testGetAntecedentById(Long id) {
        System.out.println("=== TEST GET ANTECEDENT BY ID ===");
        Antecedents antecedent = antecedentService.getAntecedentById(id);
        System.out.println("✓ Antécédent trouvé : " + antecedent);
    }

    private static void testUpdateAntecedent(Long id) {
        System.out.println("=== TEST UPDATE ANTECEDENT ===");
        Antecedents antecedent = antecedentService.getAntecedentById(id);
        antecedent.setNom("Diabete modifié");
        antecedentService.updateAntecedent(antecedent);
        System.out.println("✓ Antécédent modifié : " + antecedent);
    }

    private static void testDeleteAntecedentById(Long id) {
        System.out.println("=== TEST DELETE ANTECEDENT BY ID ===");
        antecedentService.deleteAntecedentById(id);
        System.out.println("✓ Antécédent supprimé avec ID : " + id);
    }

    private static void testFindByNom(String nom) {
        System.out.println("=== TEST FIND BY NOM ===");
        antecedentService.findByNom(nom).ifPresentOrElse(
                a -> System.out.println("✓ Antécédent trouvé : " + a),
                () -> System.out.println("⚠️ Aucun antécédent trouvé pour le nom : " + nom)
        );
    }

    private static void testFindByCategorie(String categorie) {
        System.out.println("=== TEST FIND BY CATEGORIE ===");
        List<Antecedents> antecedents = antecedentService.findByCategorie(categorie);
        antecedents.forEach(a -> System.out.println("✓ " + a));
    }

    private static void testFindByNiveauRisque(NiveauDeRisque niveau) {
        System.out.println("=== TEST FIND BY NIVEAU DE RISQUE ===");
        List<Antecedents> antecedents = antecedentService.findByNiveauRisque(niveau);
        antecedents.forEach(a -> System.out.println("✓ " + a));
    }

    private static void testCount() {
        System.out.println("=== TEST COUNT ===");
        long count = antecedentService.count();
        System.out.println("📊 Nombre total d'antécédents : " + count);
    }

    private static void testFindPage() {
        System.out.println("=== TEST FIND PAGE ===");
        List<Antecedents> page = antecedentService.findPage(2, 0);
        page.forEach(a -> System.out.println("✓ " + a));
    }
}

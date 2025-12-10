package ma.whitecare.repository.test;



import ma.whitecare.entities.enums.FormeMedicament;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.modules.Medicament.MedicamentRepositoryImpl;

import java.util.List;

public class MedicamentRepositoryTest {

    public static void main(String[] args) {
        System.out.println("=== TEST SIMPLE MÉDICAMENT REPOSITORY ===\n");

        try {
            // 1. Initialisation
            MedicamentRepositoryImpl repo = new MedicamentRepositoryImpl();
            System.out.println("1. Repository initialisé");

            // 2.Créer un médicament de test
            Medicament test = new Medicament();
            test.setNom("Paracétamol Test");
            test.setLaboratoire("LabTest");
            test.setType("Antidouleur");
            test.setForme(FormeMedicament.COMPRIME);
            test.setRemboursable(true);
            test.setPrixUnitaire(15.5);
            test.setDescription("Test description");
            test.setCreePar("test");
            test.setModifiePar("test");
            Medicament test2 = new Medicament();
            test2.setNom("medict Test");
            test2.setLaboratoire("LabtLab");
            test2.setType("AntiTest");
            test2.setForme(FormeMedicament.GELULE);
            test2.setRemboursable(true);
            test2.setPrixUnitaire(25.5);
            test2.setDescription("Test test");
            test2.setCreePar("test_system");
            test2.setModifiePar("test_system");
            repo.create(test);
            repo.create(test2);
            System.out.println("✅ Médicament de test créé avec ID: " + test.getIdMct());
            System.out.println("✅ Médicament de test créé avec ID: " + test2.getIdMct());

            // 2. Lister tous les médicaments
            System.out.println("\n2. Liste des médicaments existants:");
            List<Medicament> tous = repo.findAll();
            System.out.println("   Total: " + tous.size() + " médicament(s)");
            tous.forEach(md-> System.out.println(md.toString()));

                // 3. Test recherche par ID
                System.out.println("\n3. Test recherche par ID: " + test.getIdMct());
                Medicament trouve = repo.findById(test.getIdMct());
                if (trouve != null) {
                    System.out.println("   ✅ Trouvé: " + trouve.getNom());
                }

                // 4. Test recherche par nom
                System.out.println("\n4. Test recherche par nom: " + test2.getNom());
                var parNom = repo.findByNomExact(test2.getNom());
                if (parNom.isPresent()) {
                    System.out.println("   ✅ Trouvé par nom");
                }

                // 5. Test recherche par laboratoire
                System.out.println("\n5. Test recherche par laboratoire: " + test.getLaboratoire());
                List<Medicament> parLabo = repo.findByLaboratoire(test.getLaboratoire());
                System.out.println("   ✅ " + parLabo.size() + " médicament(s) trouvé(s)");

                // 6. Test recherche par type
                System.out.println("\n6. Test recherche par type: " + test2.getType());
                List<Medicament> parType = repo.findByType(test2.getType());
                System.out.println("   ✅ " + parType.size() + " médicament(s) trouvé(s)");

              repo.deleteById(test.getIdMct());
              repo.delete(test);




            System.out.println("\n✅ TEST TERMINÉ AVEC SUCCÈS !");

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }











}

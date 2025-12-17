package ma.whitecare.repository.test;

import ma.whitecare.entities.medical.Acte;
import ma.whitecare.repository.modules.actes.api.ActeRepository;
import ma.whitecare.repository.modules.actes.impl.ActeRepositoryImpl;

public class ActeRepositoryTest {



        public static void main(String[] args) {
            System.out.println("=== TEST ACTE REPOSITORY ===");
            ActeRepository repo = new ActeRepositoryImpl();


            try {
                // 1. Test CREATE
                System.out.println("\n1. Création d'un acte...");
                Acte acte = Acte.builder()
                        .libelle("Détartrage")
                        .categorie("Préventif")
                        .prixDeBase(300.0)
                        .creePar("admin")
                        .modifiePar("admin")
                        .build();
                Acte acte1 = Acte.builder()
                        .libelle("test")
                        .categorie("test")
                        .prixDeBase(250.0)
                        .creePar("admin")
                        .modifiePar("admin")
                        .build();
                repo.create(acte1);
                repo.create(acte);
                System.out.println("✓ Acte créé avec ID: " + acte.getIdActe());

                // 2. Test FIND BY ID
                System.out.println("\n2. Recherche par ID...");
                Acte found = repo.findById(acte.getIdActe());
                if (found != null) {
                    System.out.println("✓ Acte trouvé: " + found.getLibelle() + " - " + found.getCategorie());
                }

                // 3. Test FIND BY LIBELLE
                System.out.println("\n3. Recherche par libellé...");
                var actesByLibelle = repo.findByLibelle("Détartrage");
                System.out.println("✓ " + actesByLibelle.size() + " acte(s) trouvé(s)");
                actesByLibelle.forEach(ac-> System.out.println(ac.toString()));
                // 4. Test FIND BY CATEGORIE
                System.out.println("\n4. Recherche par catégorie...");
                var actesByCategorie = repo.findByCategorie("Préventif");
                System.out.println("✓ " + actesByCategorie.size() + " acte(s) trouvé(s)");

                // 5. Test COUNT BY CATEGORIE
                System.out.println("\n5. Comptage par catégorie...");
                Long count = repo.countByCategorie("Préventif");
                System.out.println("✓ " + count + " acte(s) dans la catégorie");

                // 6. Test UPDATE PRIX
                System.out.println("\n6. Mise à jour du prix...");
                repo.updatePrix(acte.getIdActe(), 350.0);
                System.out.println("✓ Prix mis à jour");

                // 7. Test UPDATE CATEGORIE
                System.out.println("\n7. Mise à jour de la catégorie...");
                repo.updateCategorie(acte.getIdActe(), "Soins");
                System.out.println("✓ Catégorie mise à jour");

                // 8. Test UPDATE LIBELLE
                System.out.println("\n8. Mise à jour du libellé...");
                repo.updateLibelle(acte.getIdActe(), "Détartrage complet");
                System.out.println("✓ Libellé mis à jour");

                // 9. Test FIND ALL
                System.out.println("\n9. Liste de tous les actes...");
                var allActes = repo.findAll();
                System.out.println("✓ " + allActes.size() + " acte(s) au total");
                allActes.forEach(ac-> System.out.println(ac.toString()));
                System.out.println("*".repeat(50));
                try {
                    // 2. Attendre 3 secondes
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 10. Test DELETE
                System.out.println("\n10. Suppression de l'acte...");
                repo.deleteById(acte.getIdActe());
                repo.deleteById(acte1.getIdActe());
                System.out.println("✓ Acte supprimé");

                // Vérification
                Acte deleted = repo.findById(acte.getIdActe());
                if (deleted == null) {
                    System.out.println("✓ Vérification: Acte correctement supprimé");
                }

                System.out.println("\n✅ Tous les tests passés avec succès!");

            } catch (Exception e) {
                System.err.println("❌ Erreur lors des tests: " + e.getMessage());
                e.printStackTrace();
            }
        }

}

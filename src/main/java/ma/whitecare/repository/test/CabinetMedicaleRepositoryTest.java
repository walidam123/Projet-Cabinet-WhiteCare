package ma.whitecare.repository.test;

import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class CabinetMedicaleRepositoryTest {

    private static CabinetMedicaleRepository cabinetrepository=new CabinetMedicaleRepositoryImpl();


    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS CabinetMedicale REPOSITORY ===\n");

        try {

            testCabinet();


            System.out.println("\n=== TOUS LES TESTS ONT REUSSI ===");

        } catch (Exception e) {
            System.err.println("Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void testCabinet(){

        // 1. TEST CREATE
        System.out.println("1. 📝 Test création cabinet...");
        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom("Clinique a supprimerv2")
                .email("rapidesup@test.com")
                .cin("R123456789")
                .adresse("123 Test Rue")
                .logo("testlogov2")
                .siteWeb("testsiteweb")
                .description("testdesc")
                .facebook("testfacebook")
                .instagram("testinsta")
                .tel1("0888888888")
                .tel2("0612345678")
                .creePar("test_rapide")
                .modifiePar("test_rapide")
                .build();

        cabinetrepository.create(cabinet);
        System.out.println("✅ Cabinet créé avec ID: " + cabinet.getId());
        System.out.println("*".repeat(50));

        // 2. TEST FIND BY ID
        System.out.println("\n2. 🔍 Test recherche par ID...");
        CabinetMedicale found = cabinetrepository.findById(cabinet.getId());
        System.out.println("✅ Cabinet trouvé: " + found.getNom());
        System.out.println("*".repeat(50));

        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 3. TEST FIND BY NOM
        System.out.println("\n3. 🔍 Test recherche par nom...");
        Optional<CabinetMedicale> byNom = cabinetrepository.findByNom("Clinique Test Rapide");
        System.out.println("✅ Cabinet trouvé par nom: " + byNom.get().getNom());
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 4. TEST FIND BY EMAIL
        System.out.println("\n4. 📧 Test recherche par email...");
        Optional<CabinetMedicale> byEmail = cabinetrepository.findByEmail("rapide@test.com");
        System.out.println("✅ Cabinet trouvé par email: " + byEmail.get().getEmail());
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 5. TEST FIND BY CIN
        System.out.println("\n5. 🆔 Test recherche par CIN...");
        Optional<CabinetMedicale> byCin = cabinetrepository.findByCin("R123456789");
        System.out.println("✅ Cabinet trouvé par CIN: " + byCin.get().getCin());
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 6. TEST FIND ALL
        System.out.println("\n6. 📊 Test recherche tous les cabinets...");
        List<CabinetMedicale> allCabinets = cabinetrepository.findAll();
        System.out.println("✅ " + allCabinets.size() + " cabinets trouvés au total");
        allCabinets.forEach(c-> System.out.println(c.toString()));
        System.out.println("*".repeat(50));

        // 7. TEST UPDATE
        System.out.println("\n7. ✏️ Test modification...");
        cabinet.setNom("Clinique Modifiée Rapide");
        cabinet.setEmail("modifie@rapide.com");
        cabinetrepository.update(cabinet);
        System.out.println("✅ Cabinet modifié: " + cabinet.getNom());
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 8. VERIFICATION APRES UPDATE
        CabinetMedicale updated = cabinetrepository.findById(cabinet.getId());
        System.out.println("✅ Vérification: " + updated.getNom() + " - " + updated.getEmail());
        System.out.println("*".repeat(50));

        // 9. TEST DELETE
        System.out.println("\n8. 🗑️ Test suppression...");
        cabinetrepository.deleteById(cabinet.getId());
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 10. VERIFICATION SUPPRESSION
        CabinetMedicale deleted = cabinetrepository.findById(cabinet.getId());
        if (deleted == null) {
            System.out.println("✅ Cabinet supprimé avec succès");
        } else {
            System.out.println("❌ Erreur: Cabinet toujours présent");
        }


    }


}

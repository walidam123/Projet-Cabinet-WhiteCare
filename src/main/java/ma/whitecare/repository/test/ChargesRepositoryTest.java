package ma.whitecare.repository.test;

import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;
import ma.whitecare.repository.modules.cabinet.impl.ChargesRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;


public class ChargesRepositoryTest {



    private static ChargesRepository chargesRepository = new ChargesRepositoryImpl();

    static Long testCabinetId = 1L;
    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS Charges REPOSITORY ===\n");

        try {
            testCharges();


            System.out.println("\n=== TOUS LES TESTS ONT REUSSI ===");

        } catch (Exception e) {
            System.err.println("Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void testCharges(){
        // 1. TEST CREATE

        CabinetMedicale cabinettest=new CabinetMedicale();
        cabinettest.setId(testCabinetId);
        System.out.println("1. 📝 Test création charge...");
        Charges charge = Charges.builder()
                .titre("Achat matériel médical")
                .description("Stéthoscopes et thermomètres")
                .montant(2500.50)
                .date(LocalDateTime.now())
                .Cabinet(cabinettest)
                .creePar("test_user")
                .modifiePar("test_user")
                .build();

        chargesRepository.create(charge);
        System.out.println("✅ Charge créée avec ID: " + charge.getId());
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("*".repeat(50));
        // 2. TEST FIND BY ID
        System.out.println("\n2. 🔍 Test recherche par ID...");
        Charges found = chargesRepository.findById(charge.getId());
        System.out.println(found.toString());
        System.out.println("*".repeat(50));

        // 3. TEST FIND BY CABINET
        System.out.println("\n3. 🏥 Test recherche par cabinet...");
        List<Charges> chargesCabinet = chargesRepository.findByCabinetMedicaleId(testCabinetId);
        System.out.println("✅ " + chargesCabinet.size() + " charges trouvées pour le cabinet " + testCabinetId);
        System.out.println("*".repeat(50));

        // 4. TEST CALCUL TOTAL
        System.out.println("\n4. 💰 Test calcul total charges...");
        Double total = chargesRepository.calculateTotalCharges(testCabinetId);
        System.out.println("✅ Total charges: " + total + " MAD");
        System.out.println("*".repeat(50));

        // 5. TEST FIND BY TITRE
        System.out.println("\n5. 🔎 Test recherche par titre...");
        List<Charges> byTitre = chargesRepository.findByTitreContainingIgnoreCase("matériel");
        System.out.println("✅ " + byTitre.size() + " charges trouvées avec 'matériel'");

        System.out.println("*".repeat(50));
        // 6. TEST COUNT
        System.out.println("\n6. 📊 Test comptage charges...");
        Long count = chargesRepository.countChargesByCabinet(testCabinetId);
        System.out.println("✅ Nombre total de charges: " + count);
        System.out.println("*".repeat(50));
        // 7. TEST UPDATE
        System.out.println("\n7. ✏️ Test modification...");
        charge.setTitre("Achat matériel médical urgent");
        charge.setMontant(3000.75);
        charge.setModifiePar("test_update");
        chargesRepository.update(charge);
        System.out.println("✅ Charge modifiée: " + charge.getTitre() + " - " + charge.getMontant() + " MAD");
        System.out.println("*".repeat(50));

        // 8. TEST CALCUL PAR PÉRIODE
        System.out.println("\n8. 📅 Test calcul par période...");
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Double totalPeriod = chargesRepository.calculateTotalChargesByPeriod(testCabinetId, startDate, endDate);
        System.out.println("✅ Total période: " + totalPeriod + " MAD");


        try {
            // 2. Attendre 3 secondes
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("*".repeat(50));
        // 9. TEST DELETE
        System.out.println("\n9. 🗑️ Test suppression...");
        chargesRepository.deleteById(charge.getId());

        // Vérification suppression
        Charges deleted = chargesRepository.findById(charge.getId());
        if (deleted == null) {
            System.out.println("✅ Charge supprimée avec succès");
        } else {
            System.out.println("❌ Erreur: Charge toujours présente");
        }




    }
}

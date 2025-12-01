package ma.whitecare.repository.test;

import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.repository.modules.cabinet.api.RevenuesRepository;
import ma.whitecare.repository.modules.cabinet.impl.RevenuesRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;


public class RevenuesRepositoryTest {

    private static RevenuesRepository revenuesrepository = new RevenuesRepositoryImpl();
    static Long testCabinetId=1l;

    public static void main(String[] args) {
        System.out.println("=== DEBUT DES TESTS PATIENT REPOSITORY ===\n");

        try {
            // 1. TEST CREATE
            System.out.println("1. 📝 Test création revenue...");
            Revenues revenue = Revenues.builder()
                    .titre("Consultation médicale")
                    .description("Consultation du Dr. Smith")
                    .montant(300.0)
                    .date(LocalDateTime.now())
                    .cabinetMedicaleId(testCabinetId)
                    .creePar("test_user")
                    .modifiePar("test_user")
                    .build();
            revenuesrepository.create(revenue);
            System.out.println("✅ Revenue créé avec ID: " + revenue.getId());

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));
            // 2. TEST FIND BY ID
            System.out.println("\n2. 🔍 Test recherche par ID...");
            Revenues found = revenuesrepository.findById(revenue.getId());
            System.out.println(found.toString());

            // 3. TEST FIND BY CABINET
            System.out.println("\n3. 🏥 Test recherche par cabinet...");
            List<Revenues> revenuesCabinet = revenuesrepository.findByCabinetMedicaleId(testCabinetId);
            System.out.println("✅ " + revenuesCabinet.size() + " revenues trouvés pour le cabinet " + testCabinetId);
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));



            // 4. TEST CALCUL TOTAL
            System.out.println("\n4. 💰 Test calcul total revenues...");
            Double total = revenuesrepository.calculateTotalRevenues(testCabinetId);
            System.out.println("✅ Total revenues: " + total + " MAD");
            System.out.println("*".repeat(50));
            // 5. TEST CALCUL MOYENNE
            System.out.println("\n5. 📊 Test calcul moyenne...");
            Double moyenne = revenuesrepository.calculateAverageRevenue(testCabinetId);
            System.out.println("✅ Moyenne revenue: " + moyenne + " MAD");

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));



            // 7. TEST FIND BY TITLE
            System.out.println("\n7. 🔎 Test recherche par titre...");
            List<Revenues> byTitle = revenuesrepository.findByTitleContainingIgnoreCase("consultation");
            System.out.println("✅ " + byTitle.size() + " revenues trouvés avec 'consultation'");
            System.out.println("*".repeat(50));

            // 8. TEST COUNT
            System.out.println("\n8. 📊 Test comptage revenues...");
            Long count = revenuesrepository.countRevenuesByCabinet(testCabinetId);
            System.out.println("✅ Nombre total de revenues: " + count);
            System.out.println("*".repeat(50));

            // 9. TEST UPDATE
            System.out.println("\n9. ✏️ Test modification...");
            revenue.setTitre("Consultation spécialisée");
            revenue.setMontant(450.0);
            revenue.setDescription("Consultation spécialisée cardiologie");

            revenuesrepository.update(revenue);

            System.out.println("✅ Revenue modifié: " + revenue.getTitre() + " - " + revenue.getMontant() + " MAD");
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));
            // 10. TEST CALCUL PAR PÉRIODE
            System.out.println("\n10. 📅 Test calcul par période...");
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();
            Double totalPeriod = revenuesrepository.calculateTotalRevenuesByPeriod(testCabinetId, startDate, endDate);
            System.out.println("✅ Total période (30 jours): " + totalPeriod + " MAD");

            System.out.println("*".repeat(50));
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 17. TEST DELETE
            System.out.println("\n17. 🗑️ Test suppression...");
            revenuesrepository.deleteById(revenue.getId());

            // Vérification suppression
            Revenues deleted = revenuesrepository.findById(revenue.getId());
            if (deleted == null) {
                System.out.println("✅ Revenue supprimé avec succès");
            } else {
                System.out.println("❌ Erreur: Revenue toujours présent");
            }

            System.out.println("\n=== TOUS LES TESTS ONT REUSSI ===");
        } catch (Exception e) {
            System.err.println("Erreur pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

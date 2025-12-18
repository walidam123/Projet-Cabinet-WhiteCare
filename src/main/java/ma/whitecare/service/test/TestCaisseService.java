package ma.whitecare.service.test;

import ma.whitecare.repository.modules.cabinet.impl.RevenuesRepositoryImpl;
import ma.whitecare.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.whitecare.service.modules.caisse.api.CaisseService;
import ma.whitecare.service.modules.caisse.impl.CaisseServiceImpl;
import ma.whitecare.service.modules.caisse.impl.ChargesServiceImpl;
import ma.whitecare.service.modules.caisse.impl.RevenuesServiceImpl;

public class TestCaisseService {

    public static void main(String[] args) {
        // Instancie d'abord les repositories (qui utilisent SessionFactory pour la connexion)
        RevenuesRepositoryImpl revenuesRepo = new RevenuesRepositoryImpl();
        ChargesRepositoryImpl chargesRepo = new ChargesRepositoryImpl();

        // Passe les repositories aux services (les implémentations que je t'ai fournies attendent un repo)
        RevenuesServiceImpl revenuesService = new RevenuesServiceImpl(revenuesRepo);
        ChargesServiceImpl chargesService = new ChargesServiceImpl(chargesRepo);

        // Maintenant instancie le CaisseService avec les deux services
        CaisseService caisse = new CaisseServiceImpl(revenuesService, chargesService);

        Long cabinetId = 2L;

        System.out.println("=== TEST CAISSE SERVICE ===");

        System.out.println("Total Revenues : " + caisse.getTotalRevenues(cabinetId));
        System.out.println("Total Charges : " + caisse.getTotalCharges(cabinetId));
        System.out.println("Solde Global : " + caisse.getSolde(cabinetId));

        System.out.println("\n--- période 2024 ---");
        System.out.println("Revenues period : " + caisse.getTotalRevenuesByPeriod(
                cabinetId, "2024-01-01T00:00", "2024-12-31T23:59"));

        System.out.println("Charges period : " + caisse.getTotalChargesByPeriod(
                cabinetId, "2024-01-01T00:00", "2024-12-31T23:59"));

        System.out.println("Solde period : " + caisse.getSoldeByPeriod(
                cabinetId, "2024-01-01T00:00", "2024-12-31T23:59"));
    }
}

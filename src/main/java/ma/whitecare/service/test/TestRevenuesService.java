package ma.whitecare.service.test;

import ma.whitecare.repository.modules.cabinet.impl.RevenuesRepositoryImpl; // si tu utilises l'impl concrète
import ma.whitecare.service.modules.caisse.api.RevenuesService;
import ma.whitecare.service.modules.caisse.impl.RevenuesServiceImpl;

import java.time.LocalDateTime;

public class TestRevenuesService {

    public static void main(String[] args) {
        // --- si tu utilises l'impl Repository concrète (connexion via SessionFactory) :
        RevenuesService service = new RevenuesServiceImpl(new RevenuesRepositoryImpl());

        Long cabinetId = 1L;

        Double total = service.getTotalRevenues(cabinetId);
        System.out.println("Total Revenues = " + total);

        Double totalPeriod = service.getTotalRevenuesByPeriod(
                cabinetId,
                LocalDateTime.parse("2024-01-01T00:00"),
                LocalDateTime.parse("2024-12-31T23:59")
        );
        System.out.println("Total Revenues in Period = " + totalPeriod);

        // <-- ici on appelle la méthode correcte définie dans l'interface/service :
        Long count = service.countRevenues(cabinetId);
        System.out.println("Count Revenues = " + count);
    }
}


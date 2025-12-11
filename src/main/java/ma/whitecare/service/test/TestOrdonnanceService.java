package ma.whitecare.service.test;


import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepository;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepositoryImpl;
import ma.whitecare.service.modules.ordonnance.api.OrdonnanceService;
import ma.whitecare.service.modules.ordonnance.impl.OrdonnanceServiceImpl;

public class TestOrdonnanceService {

    public static void main(String[] args) {
        OrdonnanceRepository ordonnanceRepository=new OrdonnanceRepositoryImpl();
        OrdonnanceService ordonnanceService = new OrdonnanceServiceImpl(ordonnanceRepository);

        Long idOrdonnance = 1L;

        System.out.println("=== TEST ORDONNANCE SERVICE ===");

        System.out.println("→ Get ordonnance by ID : ");
        System.out.println(ordonnanceService.findById(idOrdonnance));

        System.out.println("\n→ Get all ordonnances of dossier 1 : ");
        System.out.println(ordonnanceService.findByDossierMedicaleId(4L));

        System.out.println("\n→ Check medicines of ordonnance 1 : ");
        System.out.println(ordonnanceService.findByConsultationId(2L));
    }
}


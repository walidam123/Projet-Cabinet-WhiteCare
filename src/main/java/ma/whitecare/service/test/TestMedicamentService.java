package ma.whitecare.service.test;

import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.modules.Medicament.MedicamentRepositoryImpl;
import ma.whitecare.service.modules.medicament.impl.MedicamentServiceImpl;

public class TestMedicamentService {

    public static void main(String[] args) {

        MedicamentServiceImpl service = new MedicamentServiceImpl(new MedicamentRepositoryImpl());

        System.out.println("=== TEST MEDICAMENT SERVICE ===");

        System.out.println("\n--- getAll ---");
        service.getAll().forEach(System.out::println);

        System.out.println("\n--- getById(1) ---");
        System.out.println(service.getById(1L));

        System.out.println("\n--- getByExactName(\"Doliprane\") ---");
        System.out.println(service.getByExactName("Doliprane"));

        System.out.println("\n--- getByLaboratoire(\"Sanofi\") ---");
        service.getByLaboratoire("Sanofi").forEach(System.out::println);

        System.out.println("\n--- getByType(\"Antalgique\") ---");
        service.getByType("Antalgique").forEach(System.out::println);
    }
}

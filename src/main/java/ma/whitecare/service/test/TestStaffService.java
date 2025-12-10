package ma.whitecare.service.test;



import ma.whitecare.mvc.dto.StaffDTO;
import ma.whitecare.mvc.dto.StaffStatisticsDTO;
import ma.whitecare.mvc.dto.UpdateStaffDTO;
import ma.whitecare.repository.modules.UserManager.api.RoleRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.repository.modules.UserManager.impl.RoleRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.StaffRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.UtilisateurRepositoryImpl;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.whitecare.service.modules.UserManager.impl.StaffServiceImpl;
import ma.whitecare.service.modules.UserManager.impl.UserServiceImpl;

public class TestStaffService {

    public static void main(String[] args) {
        System.out.println("=== TEST STAFF SERVICE ===\n");

        try {
            // Initialisation
             UtilisateurRepository utilisateurRepository=new UtilisateurRepositoryImpl();
             RoleRepository roleRepository=new RoleRepositoryImpl();
            StaffRepositoryImpl staffRepo = new StaffRepositoryImpl();
            UserServiceImpl userService = new UserServiceImpl(utilisateurRepository, roleRepository);
             CabinetMedicaleRepository cabinetRepository=new CabinetMedicaleRepositoryImpl();
            StaffServiceImpl staffService = new StaffServiceImpl(staffRepo, userService,cabinetRepository);

            // Test 1: Récupérer tous les staff
            System.out.println("1. Liste de tous les staff:");
            var allStaff = staffService.getAllStaff();
            System.out.println("   ✅ Nombre total: " + allStaff.size());

            // Test 2: Statistiques
            System.out.println("\n2. Statistiques:");
            StaffStatisticsDTO stats = staffService.getStaffStatistics();
            System.out.println("   ✅ Total staff: " + stats.getTotalStaff());
            System.out.println("   ✅ Médecins: " + stats.getTotalMedecins());
            System.out.println("   ✅ Secrétaires: " + stats.getTotalSecretaires());
            System.out.println("   ✅ Actifs: " + stats.getTotalActifs());

            // Test 3: Mise à jour salaire
            System.out.println("\n3. Mise à jour salaire:");
            try {
                staffService.updateSalaire(1L, 15000.0);
                System.out.println("   ✅ Salaire mis à jour");
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            // Test 4: Conversion DTO
            System.out.println("\n4. Conversion en DTO:");
            if (!allStaff.isEmpty()) {
                StaffDTO dto = staffService.convertToDTO(allStaff.get(0));
                System.out.println("   ✅ DTO créé: " + dto.getNom() + " " + dto.getPrenom());
                System.out.println("   Type: " + dto.getTypeStaff());
            }

            // Test 5: Staff par cabinet
            System.out.println("\n5. Staff par cabinet:");
            try {
                var staffCabinet = staffService.getStaffByCabinet(1L);
                System.out.println("   ✅ Staff cabinet 1: " + staffCabinet.size() + " membres");
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            System.out.println("\n=== TESTS TERMINÉS ===");

        } catch (Exception e) {
            System.err.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
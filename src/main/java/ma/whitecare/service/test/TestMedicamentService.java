package ma.whitecare.service.test;

import ma.whitecare.entities.enums.FormeMedicament;
import ma.whitecare.mvc.dto.MedicamentDto.CreateMedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.MedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.UpdateMedicamentDTO;
import ma.whitecare.repository.modules.Medicament.MedicamentRepository;
import ma.whitecare.repository.modules.Medicament.MedicamentRepositoryImpl;
import ma.whitecare.service.modules.medicament.api.MedicamentService;
import ma.whitecare.service.modules.medicament.impl.MedicamentServiceImpl;

public class TestMedicamentService {

    public static void main(String[] args) {
        System.out.println("=== TEST MEDICAMENT SERVICE ===\n");

        try {
            // Initialisation
            MedicamentRepository medicamentRepository = new MedicamentRepositoryImpl();
            MedicamentService medicamentService = new MedicamentServiceImpl(medicamentRepository);

            // Test 1: FIND - Récupérer tous les médicaments
            System.out.println("1. TEST FIND - Récupérer tous les médicaments:");
            var allMedicaments = medicamentService.getAll();
            System.out.println("   ✅ Nombre total de médicaments: " + allMedicaments.size());

            // Test 2: FIND - Récupérer par ID (si existe)
            System.out.println("\n2. TEST FIND - Récupérer médicament par ID:");
            try {
                MedicamentDTO medicamentDTO = medicamentService.convertToDTO(
                        medicamentService.getMedicamentById(1L)
                );
                System.out.println("   ✅ Médicament trouvé:");
                System.out.println("      - ID: " + medicamentDTO.getIdMct());
                System.out.println("      - Nom: " + medicamentDTO.getNom());
                System.out.println("      - Laboratoire: " + medicamentDTO.getLaboratoire());
                System.out.println("      - Type: " + medicamentDTO.getType());
                System.out.println("      - Prix: " + medicamentDTO.getPrixUnitaire() + " DH");
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            // Test 3: CREATE - Créer un nouveau médicament
            System.out.println("\n3. TEST CREATE - Créer un nouveau médicament:");
            try {
                CreateMedicamentDTO createDTO = CreateMedicamentDTO.builder()
                        .nom("Paracetamol Test")
                        .laboratoire("Test Lab")
                        .type("Antalgique")
                        .forme(FormeMedicament.COMPRIME)
                        .remboursable(true)
                        .prixUnitaire(15.50)
                        .description("Médicament de test")
                        .creePar("test_user")
                        .modifiePar("test_user")
                        .build();

                var createdMedicament = medicamentService.createMedicament(createDTO);
                System.out.println("   ✅ Médicament créé avec succès:");
                System.out.println("      - ID: " + createdMedicament.getIdMct());
                System.out.println("      - Nom: " + createdMedicament.getNom());
                System.out.println("      - Laboratoire: " + createdMedicament.getLaboratoire());
                System.out.println("      - Prix: " + createdMedicament.getPrixUnitaire() + " DH");

                // Test 4: UPDATE - Mettre à jour le médicament créé
                System.out.println("\n4. TEST UPDATE - Mettre à jour le médicament:");
                UpdateMedicamentDTO updateDTO = UpdateMedicamentDTO.builder()
                        .nom("Paracetamol Test Modifié")
                        .prixUnitaire(18.75)
                        .description("Médicament de test modifié")
                        .modifiePar("test_user_updated")
                        .build();

                var updatedMedicament = medicamentService.updateMedicament(
                        createdMedicament.getIdMct(),
                        updateDTO
                );
                System.out.println("   ✅ Médicament mis à jour:");
                System.out.println("      - ID: " + updatedMedicament.getIdMct());
                System.out.println("      - Nom: " + updatedMedicament.getNom());
                System.out.println("      - Prix: " + updatedMedicament.getPrixUnitaire() + " DH");
                System.out.println("      - Description: " + updatedMedicament.getDescription());

                // Test 5: DELETE - Supprimer le médicament
                System.out.println("\n5. TEST DELETE - Supprimer le médicament:");
                medicamentService.deleteMedicament(createdMedicament.getIdMct());
                System.out.println("   ✅ Médicament supprimé avec succès (ID: " + createdMedicament.getIdMct() + ")");

                // Vérifier que le médicament n'existe plus
                try {
                    medicamentService.getMedicamentById(createdMedicament.getIdMct());
                    System.out.println("   ⚠️  Le médicament existe encore!");
                } catch (Exception e) {
                    System.out.println("   ✅ Vérification: Le médicament n'existe plus");
                }

            } catch (Exception e) {
                System.out.println("   ❌ Erreur: " + e.getMessage());
                e.printStackTrace();
            }

            // Test 6: FIND - Recherche par nom exact
            System.out.println("\n6. TEST FIND - Recherche par nom exact:");
            try {
                var medicament = medicamentService.getByExactName("Doliprane");
                if (medicament.isPresent()) {
                    System.out.println("   ✅ Médicament trouvé: " + medicament.get().getNom());
                } else {
                    System.out.println("   ⚠️  Médicament 'Doliprane' non trouvé");
                }
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            // Test 7: FIND - Recherche par laboratoire
            System.out.println("\n7. TEST FIND - Recherche par laboratoire:");
            try {
                var medicamentsByLab = medicamentService.getByLaboratoire("Sanofi");
                System.out.println("   ✅ Nombre de médicaments trouvés: " + medicamentsByLab.size());
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            // Test 8: FIND - Recherche par type
            System.out.println("\n8. TEST FIND - Recherche par type:");
            try {
                var medicamentsByType = medicamentService.getByType("Antalgique");
                System.out.println("   ✅ Nombre de médicaments trouvés: " + medicamentsByType.size());
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            System.out.println("\n=== TOUS LES TESTS TERMINÉS ===");

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR GÉNÉRALE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

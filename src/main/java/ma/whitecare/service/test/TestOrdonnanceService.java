package ma.whitecare.service.test;

import ma.whitecare.mvc.dto.OrdonnanceDto.CreateOrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdonnanceDto.OrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdonnanceDto.UpdateOrdonnanceDTO;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepository;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.whitecare.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.whitecare.service.modules.ordonnance.api.OrdonnanceService;
import ma.whitecare.service.modules.ordonnance.impl.OrdonnanceServiceImpl;

import java.time.LocalDate;

public class TestOrdonnanceService {

    private static final Long CONSULTATION_ID = 3L;
    private static final Long DOSSIER_MEDICALE_ID = 3L;

    public static void main(String[] args) {
        System.out.println("=== TEST ORDONNANCE SERVICE ===\n");

        try {
            // Initialisation des repositories
            OrdonnanceRepository ordonnanceRepository = new OrdonnanceRepositoryImpl();
            ConsultationRepository consultationRepository = new ConsultationRepositoryImpl();
            DossierMedicalRepository dossierMedicalRepository = new DossierMedicalRepositoryImpl();

            // Initialisation du service
            OrdonnanceService ordonnanceService = new OrdonnanceServiceImpl(
                    ordonnanceRepository,
                    consultationRepository,
                    dossierMedicalRepository
            );

            // Test 1: FIND - Récupérer toutes les ordonnances
            System.out.println("1. TEST FIND - Récupérer toutes les ordonnances:");
            var allOrdonnances = ordonnanceService.findAll();
            System.out.println("   ✅ Nombre total d'ordonnances: " + allOrdonnances.size());

            // Test 2: FIND - Récupérer par ID (si existe)
            System.out.println("\n2. TEST FIND - Récupérer ordonnance par ID:");
            try {
                OrdonnanceDTO ordonnanceDTO = ordonnanceService.convertToDTO(
                        ordonnanceService.getOrdonnanceById(1L)
                );
                System.out.println("   ✅ Ordonnance trouvée:");
                System.out.println("      - ID: " + ordonnanceDTO.getIdOrd());
                System.out.println("      - Date: " + ordonnanceDTO.getDate());
                System.out.println("      - Dossier ID: " + ordonnanceDTO.getDossierMedicaleId());
                System.out.println("      - Consultation ID: " + ordonnanceDTO.getConsultationId());
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            // Test 3: CREATE - Créer une nouvelle ordonnance
            System.out.println("\n3. TEST CREATE - Créer une nouvelle ordonnance:");
            try {
                CreateOrdonnanceDTO createDTO = CreateOrdonnanceDTO.builder()
                        .date(LocalDate.now())
                        .dossierMedicaleId(DOSSIER_MEDICALE_ID)
                        .consultationId(CONSULTATION_ID)
                        .creePar("test_user")
                        .modifiePar("test_user")
                        .build();

                var createdOrdonnance = ordonnanceService.createOrdonnance(createDTO);
                System.out.println("   ✅ Ordonnance créée avec succès:");
                System.out.println("      - ID: " + createdOrdonnance.getIdOrd());
                System.out.println("      - Date: " + createdOrdonnance.getDate());
                System.out.println("      - Dossier ID: " + createdOrdonnance.getDossierMedicaleid());
                System.out.println("      - Consultation ID: " + createdOrdonnance.getConsultationid());

                // Test 4: UPDATE - Mettre à jour l'ordonnance créée
                System.out.println("\n4. TEST UPDATE - Mettre à jour l'ordonnance:");
                UpdateOrdonnanceDTO updateDTO = UpdateOrdonnanceDTO.builder()
                        .date(LocalDate.now().minusDays(1))
                        .dossierMedicaleId(DOSSIER_MEDICALE_ID)
                        .consultationId(CONSULTATION_ID)
                        .modifiePar("test_user_updated")
                        .build();

                var updatedOrdonnance = ordonnanceService.updateOrdonnance(
                        createdOrdonnance.getIdOrd(),
                        updateDTO
                );
                System.out.println("   ✅ Ordonnance mise à jour:");
                System.out.println("      - ID: " + updatedOrdonnance.getIdOrd());
                System.out.println("      - Date: " + updatedOrdonnance.getDate());
                System.out.println("      - Modifié par: " + updatedOrdonnance.getModifiePar());

                // Test 5: DELETE - Supprimer l'ordonnance
                System.out.println("\n5. TEST DELETE - Supprimer l'ordonnance:");
                ordonnanceService.deleteOrdonnance(createdOrdonnance.getIdOrd());
                System.out.println("   ✅ Ordonnance supprimée avec succès (ID: " + createdOrdonnance.getIdOrd() + ")");

                // Vérifier que l'ordonnance n'existe plus
                try {
                    ordonnanceService.getOrdonnanceById(createdOrdonnance.getIdOrd());
                    System.out.println("   ⚠️  L'ordonnance existe encore!");
                } catch (Exception e) {
                    System.out.println("   ✅ Vérification: L'ordonnance n'existe plus");
                }

            } catch (Exception e) {
                System.out.println("   ❌ Erreur: " + e.getMessage());
                e.printStackTrace();
            }

            // Test 6: FIND - Recherche par dossier médical
            System.out.println("\n6. TEST FIND - Recherche par dossier médical (ID: " + 3l + "):");
            try {
                var ordonnancesByDossier = ordonnanceService.findByDossierMedicaleId(3l);
                System.out.println("   ✅ Nombre d'ordonnances trouvées: " + ordonnancesByDossier.size());
            } catch (Exception e) {
                System.out.println("   ⚠️  " + e.getMessage());
            }

            // Test 7: FIND - Recherche par consultation
            System.out.println("\n7. TEST FIND - Recherche par consultation (ID: " + CONSULTATION_ID + "):");
            try {
                var ordonnancesByConsultation = ordonnanceService.findByConsultationId(CONSULTATION_ID);
                System.out.println("   ✅ Nombre d'ordonnances trouvées: " + ordonnancesByConsultation.size());
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


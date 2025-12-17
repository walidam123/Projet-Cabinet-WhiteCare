package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.mvc.dto.dossierMedical.PrescriptionDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreatePrescriptionDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdatePrescriptionDTO;

import java.util.List;

public interface PrescriptionService {

    // ========== CRUD PRESCRIPTION ==========
    PrescriptionDTO createPrescription(CreatePrescriptionDTO dto);
    PrescriptionDTO updatePrescription(Long prescriptionId, UpdatePrescriptionDTO dto);
    void deletePrescription(Long prescriptionId);
    PrescriptionDTO getPrescriptionById(Long prescriptionId);
    List<PrescriptionDTO> getAllPrescriptions();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<PrescriptionDTO> findByOrdonnanceId(Long ordonnanceId);
    List<PrescriptionDTO> findByMedicamentId(Long medicamentId);
    List<PrescriptionDTO> findByDureeSuperieure(Integer dureeMin);
    List<PrescriptionDTO> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId);

    // ========== CALCULS ==========
    Double calculateCoutTotalOrdonnance(Long ordonnanceId);

    // ========== VALIDATION ==========
    boolean existsById(Long prescriptionId);

    // ========== STATISTIQUES ==========
    long countAllPrescriptions();
    long countByOrdonnanceId(Long ordonnanceId);
    long countByMedicamentId(Long medicamentId);

    // ========== MÉTHODES MÉTIER IMPORTANTES ==========
    List<PrescriptionDTO> getPrescriptionsActives(Long patientId);
    List<PrescriptionDTO> getHistoriquePrescriptions(Long patientId);
}


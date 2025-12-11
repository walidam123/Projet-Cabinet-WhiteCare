package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.entities.medical.Prescription;

import java.util.List;

public interface PrescriptionService {

    // ========== CRUD PRESCRIPTION ==========
    Prescription createPrescription(Prescription prescription);
    Prescription updatePrescription(Long prescriptionId, Prescription prescription);
    void deletePrescription(Long prescriptionId);
    Prescription getPrescriptionById(Long prescriptionId);
    List<Prescription> getAllPrescriptions();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<Prescription> findByOrdonnanceId(Long ordonnanceId);
    List<Prescription> findByMedicamentId(Long medicamentId);
    List<Prescription> findByDureeSuperieure(Integer dureeMin);
    List<Prescription> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId);

    // ========== CALCULS ==========
    Double calculateCoutTotalOrdonnance(Long ordonnanceId);

    // ========== VALIDATION ==========
    boolean existsById(Long prescriptionId);

    // ========== STATISTIQUES ==========
    long countAllPrescriptions();
    long countByOrdonnanceId(Long ordonnanceId);
    long countByMedicamentId(Long medicamentId);
}


package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.entities.medical.DossierMedicale;

import ma.whitecare.mvc.dto.DossierMedicale.ConsultationDTO;
import ma.whitecare.mvc.dto.DossierMedicale.CreateDossierMedicalDTO;
import ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO;
import ma.whitecare.mvc.dto.DossierMedicale.UpdateDossierMedicalDTO;

import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalService {

    // Retrieve a single DossierMedicale entity by patient ID (returns null if not
    // found)
    DossierMedicale getDossierByPatientId(Long patientId);

    // Save a DossierMedicale entity directly (used by SecretaryController)
    DossierMedicale saveDossier(DossierMedicale dossierMedical);

    // ========== CRUD DOSSIER MÉDICAL ==========
    DossierMedicalDTO createDossierMedical(CreateDossierMedicalDTO dto);

    DossierMedicalDTO updateDossierMedical(Long dossierId, UpdateDossierMedicalDTO dto);

    void deleteDossierMedical(Long dossierId);

    DossierMedicalDTO getDossierMedicalById(Long dossierId);

    List<DossierMedicalDTO> getAllDossiersMedicaux();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<DossierMedicalDTO> findByPatientId(Long patientId);

    List<DossierMedicalDTO> findByMedecinId(Long medecinId);

    List<DossierMedicalDTO> findByDateCreation(LocalDate date);

    List<DossierMedicalDTO> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);

    // ========== VALIDATION ==========
    boolean existsByPatientId(Long patientId);

    boolean existsById(Long dossierId);

    // ========== STATISTIQUES ==========
    long countAllDossiers();

    long countByPatientId(Long patientId);

    long countByMedecinId(Long medecinId);

    // ========== MÉTHODES MÉTIER IMPORTANTES ==========
    ConsultationDTO getDerniereConsultation(Long dossierId);

    List<ConsultationDTO> getHistoriqueCompletPatient(Long patientId);
}
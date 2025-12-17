package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.mvc.dto.dossierMedical.ConsultationDTO;
import ma.whitecare.mvc.dto.dossierMedical.DossierMedicalDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreateDossierMedicalDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdateDossierMedicalDTO;

import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalService {

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


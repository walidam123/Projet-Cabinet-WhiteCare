package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.mvc.dto.DossierMedicale.ConsultationDTO;
import ma.whitecare.mvc.dto.DossierMedicale.CreateConsultationDTO;
import ma.whitecare.mvc.dto.DossierMedicale.UpdateConsultationDTO;
import ma.whitecare.mvc.dto.dossierMedical.ConsultationCompleteDTO;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationService {

    // ========== CRUD CONSULTATION ==========
    ConsultationDTO createConsultation(CreateConsultationDTO dto);
    ConsultationDTO updateConsultation(Long consultationId, UpdateConsultationDTO dto);
    void deleteConsultation(Long consultationId);
    ConsultationDTO getConsultationById(Long consultationId);
    List<ConsultationDTO> getAllConsultations();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<ConsultationDTO> findByDossierMedicalId(Long dossierId);
    List<ConsultationDTO> findByStatut(StatutConsultation statut);
    List<ConsultationDTO> findByDate(LocalDate date);
    List<ConsultationDTO> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<ConsultationDTO> findByDossierAndDate(Long dossierId, LocalDate date);

    // ========== GESTION STATUT ==========
    void changeStatut(Long consultationId, StatutConsultation nouveauStatut);

    // ========== VALIDATION ==========
    boolean existsById(Long consultationId);

    // ========== STATISTIQUES ==========
    long countAllConsultations();
    long countByStatut(StatutConsultation statut);
    long countByDossierMedicalId(Long dossierId);

    // ========== MÉTHODES MÉTIER IMPORTANTES ==========
    List<ConsultationDTO> getConsultationsDuJour();
    ConsultationCompleteDTO getConsultationComplete(Long consultationId);
}
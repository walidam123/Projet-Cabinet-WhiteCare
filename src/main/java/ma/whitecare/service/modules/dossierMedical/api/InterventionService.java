package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.mvc.dto.dossierMedical.InterventionDTO;
import ma.whitecare.mvc.dto.dossierMedical.CreateInterventionDTO;
import ma.whitecare.mvc.dto.dossierMedical.UpdateInterventionDTO;

import java.util.List;
import java.util.Map;

public interface InterventionService {

    // ========== CRUD INTERVENTION ==========
    InterventionDTO createIntervention(CreateInterventionDTO dto);
    InterventionDTO updateIntervention(Long interventionId, UpdateInterventionDTO dto);
    void deleteIntervention(Long interventionId);
    InterventionDTO getInterventionById(Long interventionId);
    List<InterventionDTO> getAllInterventions();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<InterventionDTO> findByConsultationId(Long consultationId);
    List<InterventionDTO> findByActeId(Long acteId);
    List<InterventionDTO> findByNumDent(Integer numDent);
    List<InterventionDTO> findByConsultationAndActe(Long consultationId, Long acteId);

    // ========== CALCULS ==========
    Double calculateTotalByConsultation(Long consultationId);

    // ========== VALIDATION ==========
    boolean existsById(Long interventionId);

    // ========== STATISTIQUES ==========
    long countAllInterventions();
    long countByConsultationId(Long consultationId);
    long countByActeId(Long acteId);

    // ========== MÉTHODES MÉTIER IMPORTANTES ==========
    Map<Integer, List<InterventionDTO>> getHistoriqueDentaire(Long dossierId);
    Double getCoutTotalPatient(Long dossierId);
}


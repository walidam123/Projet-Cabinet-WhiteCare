package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.entities.medical.InterventionMedecin;

import java.util.List;

public interface InterventionService {

    // ========== CRUD INTERVENTION ==========
    InterventionMedecin createIntervention(InterventionMedecin intervention);
    InterventionMedecin updateIntervention(Long interventionId, InterventionMedecin intervention);
    void deleteIntervention(Long interventionId);
    InterventionMedecin getInterventionById(Long interventionId);
    List<InterventionMedecin> getAllInterventions();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<InterventionMedecin> findByConsultationId(Long consultationId);
    List<InterventionMedecin> findByActeId(Long acteId);
    List<InterventionMedecin> findByNumDent(Integer numDent);
    List<InterventionMedecin> findByConsultationAndActe(Long consultationId, Long acteId);

    // ========== CALCULS ==========
    Double calculateTotalByConsultation(Long consultationId);

    // ========== VALIDATION ==========
    boolean existsById(Long interventionId);

    // ========== STATISTIQUES ==========
    long countAllInterventions();
    long countByConsultationId(Long consultationId);
    long countByActeId(Long acteId);
}


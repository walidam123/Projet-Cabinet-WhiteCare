package ma.whitecare.repository.modules.dossierMedical.api;

import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;

public interface InterventionRepository extends CrudRepository<InterventionMedecin, Long> {
    List<InterventionMedecin> findByConsultationId(Long consultationId);
    List<InterventionMedecin> findByActeId(Long acteId);
    List<InterventionMedecin> findByNumDent(Integer numDent);
    List<InterventionMedecin> findByConsultationAndActe(Long consultationId, Long acteId);
    Double calculateTotalByConsultation(Long consultationId);
    boolean existsById(Long interventionId);
    long countAll();
    long countByConsultationId(Long consultationId);
    long countByActeId(Long acteId);
}

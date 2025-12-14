package ma.whitecare.repository.modules.dossierMedical.api;



import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationRepository extends CrudRepository<Consultation, Long> {
    List<Consultation> findByDossierMedicalId(Long dossierId);
    List<Consultation> findByStatut(StatutConsultation statut);
    List<Consultation> findByDate(LocalDate date);
    List<Consultation> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Consultation> findByDossierAndDate(Long dossierId, LocalDate date);
    boolean existsById(Long consultationId);
    long countAll();
    long countByStatut(StatutConsultation statut);
    long countByDossierMedicalId(Long dossierId);
}
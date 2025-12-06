package ma.whitecare.repository.modules.ordonnance.api;

import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceRepository extends CrudRepository<Ordonnance, Long> {
    
    List<Ordonnance> findByDossierMedicalId(Long dossierId);
    List<Ordonnance> findByConsultationId(Long consultationId);
    List<Ordonnance> findByDate(LocalDate date);
    List<Ordonnance> findByDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsById(Long id);
    long count();
}


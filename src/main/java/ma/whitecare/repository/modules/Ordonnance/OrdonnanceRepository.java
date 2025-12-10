package ma.whitecare.repository.modules.Ordonnance;

import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceRepository extends CrudRepository<Ordonnance,Long> {


    boolean existsById(Long id);

    List<Ordonnance> findByDossierMedicaleId(Long dossierId);

    /**
     * Rechercher par consultation
     */
    List<Ordonnance> findByConsultationId(Long consultationId);

    List<Ordonnance> findByDateBetween(LocalDate startDate, LocalDate endDate);
}

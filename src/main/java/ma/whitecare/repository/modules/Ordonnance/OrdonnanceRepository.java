package ma.whitecare.repository.modules.Ordonnance;

import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.entities.medical.Prescription;
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
    
    /**
     * Récupère toutes les prescriptions d'une ordonnance
     * @param ordonnanceId l'ID de l'ordonnance
     * @return la liste des prescriptions de l'ordonnance
     */
    List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId);
}

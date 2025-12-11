package ma.whitecare.repository.modules.dossierMedical.api;

import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;

public interface PrescriptionRepository extends CrudRepository<Prescription,Long> {
    List<Prescription> findByOrdonnanceId(Long ordonnanceId);
    List<Prescription> findByMedicamentId(Long medicamentId);
    List<Prescription> findByDureeSuperieure(Integer dureeMin);
    List<Prescription> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId);
    Double calculateCoutTotalOrdonnance(Long ordonnanceId);
    boolean existsById(Long prescriptionId);
    long countAll();
    long countByOrdonnanceId(Long ordonnanceId);
    long countByMedicamentId(Long medicamentId);
}

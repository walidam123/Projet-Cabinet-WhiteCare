package ma.whitecare.repository.modules.dossierMedical.api;

import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalRepository extends CrudRepository<DossierMedicale, Long> {
    List<DossierMedicale> findByPatientId(Long patientId);
    List<DossierMedicale> findByMedecinId(Long medecinId);
    List<DossierMedicale> findByDateCreation(LocalDate date);
    List<DossierMedicale> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByPatientId(Long patientId);
    boolean existsById(Long dossierId);
    long countAll();
    long countByPatientId(Long patientId);
    long countByMedecinId(Long medecinId);
}

package ma.whitecare.repository.modules.medicament.api;

import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.common.CrudRepository;

public interface MedicamentRepository extends CrudRepository<Medicament, Long> {
    boolean existsById(Long medicamentId);
}



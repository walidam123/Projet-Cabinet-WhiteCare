package ma.whitecare.repository.modules.Medicament;

import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MedicamentRepository extends CrudRepository<Medicament, Long> {

    Optional<Medicament> findByNomExact(String nom);


    List<Medicament> findByLaboratoire(String laboratoire);


    List<Medicament> findByType(String type);


}

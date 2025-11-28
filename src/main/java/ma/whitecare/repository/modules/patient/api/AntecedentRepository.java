package ma.whitecare.repository.modules.patient.api;

import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AntecedentRepository extends CrudRepository<Antecedents, Long> {

    Optional<Antecedents> findByNom(String nom);
    List<Antecedents> findByCategorie(String categorie);
    List<Antecedents> findByNiveauRisque(NiveauDeRisque niveau);
    boolean existsById(Long id);
    long count();
    List<Antecedents> findPage(int limit, int offset);

    // ---- Navigation inverse ----
    List<Patient> getPatientsHavingAntecedent(Long antecedentId);
}

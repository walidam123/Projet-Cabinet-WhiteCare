package ma.whitecare.repository.modules.medicament.api;

import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;

public interface MedicamentRepository extends CrudRepository<Medicament, Long> {
    
    List<Medicament> findByNom(String nom);
    List<Medicament> findByLaboratoire(String laboratoire);
    List<Medicament> findByType(String type);
    List<Medicament> findByRemboursable(boolean remboursable);
    void updatePrix(Long medicamentId, Double nouveauPrix);
    boolean existsById(Long id);
    long count();
}


package ma.whitecare.repository.modules.actes.api;

import ma.whitecare.entities.medical.Acte;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;

public interface ActeRepository extends CrudRepository<Acte, Long> {

    List<Acte> findByLibelle(String libelle);
    List<Acte> findByCategorie(String categorie);
    void updatePrix(Long acteId, Double nouveauPrix);
    void updateCategorie(Long acteId, String nouvelleCategorie);
    void updateLibelle(Long acteId, String nouveauLibelle);
    Long countByCategorie(String categorie);
}

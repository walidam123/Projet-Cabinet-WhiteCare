package ma.whitecare.service.modules.Antecedent.api;

import ma.whitecare.entities.enums.NiveauDeRisque;
import ma.whitecare.entities.patient.Antecedents;

import java.util.List;
import java.util.Optional;

public interface AntecedentService {

    List<Antecedents> getAllAntecedents();

    Antecedents getAntecedentById(Long id);

    void createAntecedent(Antecedents antecedent);

    void updateAntecedent(Antecedents antecedent);

    void deleteAntecedent(Antecedents antecedent);

    void deleteAntecedentById(Long id);

    Optional<Antecedents> findByNom(String nom);

    List<Antecedents> findByCategorie(String categorie);

    List<Antecedents> findByNiveauRisque(NiveauDeRisque niveau);

    long count();

    List<Antecedents> findPage(int limit, int offset);
}

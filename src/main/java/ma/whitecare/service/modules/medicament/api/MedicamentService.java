package ma.whitecare.service.modules.medicament.api;


import ma.whitecare.entities.medical.Medicament;

import java.util.List;
import java.util.Optional;

public interface MedicamentService {

    List<Medicament> getAll();

    Medicament getById(Long id);

    void create(Medicament medicament);

    void update(Medicament medicament);

    void delete(Long id);

    Optional<Medicament> getByExactName(String nom);

    List<Medicament> getByLaboratoire(String laboratoire);

    List<Medicament> getByType(String type);
}

